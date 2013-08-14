package com.ulg.game.with.a.purpose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 
 * @author Hervé de Sélys
 *
 */


public class ScatterPlotGenerator {
	
	private List<ScatterPlot> scatterList;
	private ScatterPlot currentScatterPlot;
	private ScatterPlotDecisionTree spdt;
	private ThumbGenerator thumbGen;
	private boolean missingValue = false;
	private List<Pair> pairList;
	private int totalNumberOfInstanceInLS;
	private int totalNumberOfInstanceInTS;
	private int currentNumberOfInstance;
	private int numberOfStoppedInstance;
	private int numberOfScatterPlot;
	private boolean isGameMode = true;
	private int thumbsPerPage = 0;
	private int numberOfPage = 0;
	private int thumbsIndex = 0;
	private List<Pair> pageThumbsList;
	private List<Instance> trainDataSet;
	private List<Instance> testDataSet;
	private int level = 1;
	private int scatterChoice;
	private int numberOfAttribute;
	private int treeID;
	private int nodeCount = 0;
	private List<Instance> currentInstanceSet;
	private boolean pureNode = false;
	private Activity gameActivity;
	private boolean sendData = true;
	
	public ScatterPlotGenerator(Activity a){
		gameActivity = a;
	}
	
	public ScatterPlotGenerator(int t){
		isGameMode = false;
		thumbsPerPage = t;
	}
	
	public int generateScatterPlot(List<String[]> data){
		List<Instance> fullDataSet = new ArrayList<Instance>();
		scatterList = new ArrayList<ScatterPlot>();
		
		//Remove the last element of the data that contains the treeID
		try{
			treeID = Integer.valueOf(data.remove(data.size()-1)[0]);
		}catch(NullPointerException e){
			System.err.println("NullPointerException");
			return -1;
		}
		nodeCount = 0;
		scatterChoice = 0;
		//We take 70% of the dataset to construct our train set
		//the 30 remaning % is for the test set
		int dataSize = data.size();
		int testSetSize = (int) Math.ceil(((float)dataSize/100)*30);
		int trainSetSize = dataSize - testSetSize;

		for(int i = 0; i < dataSize; i++){
			String[] record = data.get(i);
			List<Attribute> attributes = new ArrayList<Attribute>();
			for(int j = 0; j < record.length-1; j++){
				String value = record[j];
				if (value.equals("?")){
					missingValue = true;
					break;
				}
				Attribute attr = new Attribute(String.valueOf(j),
												Float.valueOf(value));
				attributes.add(attr);
			}
			//Add the entry only is there is no missing values
			if (missingValue == false){
				Instance inst = new Instance(i, attributes,
												record[record.length-1]);
				fullDataSet.add(inst);
			}
			missingValue = false;
		}		
		setNumberOfOutput(fullDataSet);
		Collections.shuffle(fullDataSet);
				
		trainDataSet = new ArrayList<Instance>();
		testDataSet = new ArrayList<Instance>();

		for(int i = 0; i < trainSetSize; i++)
			trainDataSet.add(fullDataSet.get(i));
		
		for(int i = trainSetSize; i < fullDataSet.size(); i++)
			testDataSet.add(fullDataSet.get(i));
		
		currentInstanceSet = trainDataSet;
		totalNumberOfInstanceInLS = trainDataSet.size();
		totalNumberOfInstanceInTS = testDataSet.size();
		currentNumberOfInstance = trainDataSet.size();
		numberOfStoppedInstance = 0;
		numberOfAttribute = data.get(0).length-1;

		PairCombinaisonGenerator pg = new PairCombinaisonGenerator(numberOfAttribute);
		pairList = pg.getList();
		numberOfScatterPlot = pairList.size();
		Collections.shuffle(pairList);
		
		if(isGameMode){
			//We create a scatterplot for each pair of integer in the list
			for (int k = 0; k < pairList.size(); k++){
				Pair pair = pairList.get(k);
				ScatterPlot sc = new ScatterPlot(k,fullDataSet,pair);
				scatterList.add(sc);
			}
		}else{
			thumbGen = new ThumbGenerator();
			pageThumbsList = new ArrayList<Pair>();
			numberOfPage = (int) Math.ceil((double) numberOfScatterPlot
											/ (double)thumbsPerPage);
			if (numberOfPage > 1){
				int lastPage = numberOfScatterPlot % thumbsPerPage;
				if (lastPage == 0)
					lastPage = thumbsPerPage;
				for(int i = 0; i < numberOfPage-1; i++){
					Pair p = new Pair(thumbsPerPage*i, (thumbsPerPage*(i+1)));
					pageThumbsList.add(p);
				}
				Pair lastPair = new Pair(thumbsPerPage*(numberOfPage-1), 
										(thumbsPerPage*(numberOfPage-1))+lastPage);
				pageThumbsList.add(lastPair);
				Pair firstPair = pageThumbsList.get(0);
				
				for (int k = firstPair.getFirst(); k < firstPair.getSecond(); k++){
					Pair pair = pairList.get(k);
					ScatterPlot sc = new ScatterPlot(k,currentInstanceSet,pair);
					scatterList.add(sc);
				}
				
			}
			else{
				Pair p = new Pair(0, numberOfScatterPlot);
				pageThumbsList.add(p);
				for (int k = p.getFirst(); k < p.getSecond(); k++){
					Pair pair = pairList.get(k);
					ScatterPlot sc = new ScatterPlot(k,currentInstanceSet,pair);
					scatterList.add(sc);
				}				
			}
		}
				
		Node root = new Node(nodeCount, 0, null, currentInstanceSet, testDataSet);
		spdt = new ScatterPlotDecisionTree(root);
		return 1;
	}
	
	/**
	 * This method allows to know the number of different output
	 * in the database. It sets the value of the id for the output.
	 * Each output get an id in the order of appearance in the data set.
	 * This id is used for point color and the class assigned to a node.
	 * 
	 * @param fullDataSet 
	 */
	public void setNumberOfOutput(List<Instance> fullDataSet){				
		List<String> outputs = new ArrayList<String>();
		for(int i = 0; i < fullDataSet.size(); i++){
			Instance inst = fullDataSet.get(i);
			String out = inst.getClassName();
			
			if (!outputs.contains(out))
				outputs.add(out);
			
			inst.setOutputId(outputs.indexOf(out));
		}
	}
	
	public boolean processTree(EquationLine line){
		
		Node currentNode = spdt.getCurrentNode();
		List<Instance> trainSet = currentNode.getLearningSet();
		List<Instance> testSet = currentNode.getTestSet();	
		boolean goodEquation = false;

		List<Instance> trainUp = new ArrayList<Instance>();
		List<Instance> trainDown = new ArrayList<Instance>();
		List<Instance> testUp = new ArrayList<Instance>();
		List<Instance> testDown = new ArrayList<Instance>();

		if (line.isVertical()){
			Pair p = currentScatterPlot.getPairAttribute();
			line.setPairAttribute(new Pair(p.getSecond(), p.getFirst()));
			line.setSlope(0);
			line.setYIntercept(line.getFirstPoint().getxData());
		}
		
		Pair pair = line.getAttributePair();
		float slope = line.getSlope();
		float yIntercept = line.getYIntercept();
		
		for (int i=0; i < trainSet.size(); i++){					
			Instance inst = trainSet.get(i);	
			List<Attribute> record = inst.getAttributeList();
			float x = record.get(pair.getFirst()).getValue();
			float y = record.get(pair.getSecond()).getValue();

			//Equation of the line:
			//http://www.mathforu.com/cours-103.html
			float equation = y - (slope*x) - yIntercept;
			if (equation < 0)
				trainUp.add(inst);			
			else
				trainDown.add(inst);				
		}
		
		for (int i=0; i < testSet.size(); i++){					
			Instance inst = testSet.get(i);	
			List<Attribute> record = inst.getAttributeList();
			float x = record.get(pair.getFirst()).getValue();
			float y = record.get(pair.getSecond()).getValue();

			//Equation of the line:
			//http://www.mathforu.com/cours-103.html
			float equation = y - (slope*x) - yIntercept;
			if (equation < 0)
				testUp.add(inst);			
			else
				testDown.add(inst);				
		}
		
		if (trainDown.size() > 0 && trainUp.size() > 0){
			goodEquation = true;
			SocketClientManager scm = new SocketClientManager();
			line.setNodeID(currentNode.getNodeID());
			line.setParentID(currentNode.getParentID());
			int leftID = nodeCount + 1;
			int rightID = nodeCount + 2;
			line.setChildrenID(leftID, rightID);
			line.setTreeID(treeID);
			line.setMajorityClassID(currentNode.getMajortiyClassID());
			if(sendData){
				if(isGameMode){
					line.setDatabase("LEVEL"+level);
					scm.setDataToSend(line);
					scm.execute("equationlinefromgame");
				}
				else{
					scm.setDataToSend(line);
					scm.execute("equationlinefromlearning");
				}
			}
			int parentID = currentNode.getNodeID();
			nodeCount++;
			Node left = new Node(nodeCount, parentID, line, trainUp, testUp);
			nodeCount++;
			Node right = new Node(nodeCount, parentID, line, trainDown, testDown);
			spdt.addChildren(right, left);
			processToNextNode();
		}
		
		return goodEquation;
	}
	
	public List<ScatterPlot> getScatterPlotList(){
		return scatterList;
	}
	
	public boolean isPureNodeCreated(){
		return pureNode;
	}
	
	public ScatterPlot getScatterPlot(int chosenScatter) {
		currentScatterPlot = scatterList.get(chosenScatter);
		return currentScatterPlot;
	}
	
	public ScatterPlot getNextScatterPlot(){
		currentScatterPlot = scatterList.get(scatterChoice%numberOfScatterPlot);
		scatterChoice++;
		return currentScatterPlot;
	}
	
	
	public int getNumberOfScatterPlot(){
		return numberOfScatterPlot;
	}
	
	public void setCurrentScatterPlot(int chosenScatter){
		currentScatterPlot = scatterList.get(chosenScatter);
	}
	
	@SuppressWarnings("unchecked")
	public void processToNextNode(){
		Node nextNode = spdt.getNextNode();
		pureNode = false;
		if(isGameMode){
			while(nextNode != null && nextNode.getPurity() > 0.95){
				numberOfStoppedInstance += nextNode.getLearningSetSize();
				pureNode = true;
				nextNode = spdt.getNextNode();
			}
			if(nextNode == null){
				//Send an empty line to know in the DB if the tree is
				//complete or not
				if(sendData){
					SocketClientManager scm = new SocketClientManager();
					EquationLine line = new EquationLine();
					line.setDatabase("END");
					line.setTreeID(treeID);
					scm.setDataToSend(line);
					scm.execute("equationlinefromgame");
				}
				SocketClientManager scm2 = new SocketClientManager();
				List<String[]> data;
				level++;
				if (level == 16)
					return;
		        scm2.setDataToSend("LEVEL"+level);
		        try {
					data = (List<String[]>) scm2.execute("newdataset").get();
					if (data == null){
						showDialogNetworkError();
						return;
					}
					generateScatterPlot(data);
		        } catch (InterruptedException e) {
					System.err.println("Interrupted exception.");
					System.err.println(e.getMessage());
				} catch (ExecutionException e) {
					System.err.println("Execution exception.");
					System.err.println(e.getMessage());
				}
			}else{
				currentInstanceSet = nextNode.getLearningSet();
				currentNumberOfInstance = currentInstanceSet.size();
				List<ScatterPlot> list = new ArrayList<ScatterPlot>();
				for(int i = 0; i < pairList.size(); i++){
					Pair p = pairList.get(i);
					ScatterPlot sc = new ScatterPlot(i, currentInstanceSet, p);
					list.add(sc);
				}
				scatterList = list;	
			}
			
		}else{
			currentInstanceSet = nextNode.getLearningSet();
			currentNumberOfInstance = currentInstanceSet.size();
			List<ScatterPlot> list = new ArrayList<ScatterPlot>();
			Pair thumbsPair = pageThumbsList.get(thumbsIndex);
			for(int i = thumbsPair.getFirst(); i < thumbsPair.getSecond(); i++){
				Pair p = pairList.get(i);
				ScatterPlot sc = new ScatterPlot(i, currentInstanceSet, p);
				list.add(sc);
			}
			scatterList = list;
		}
	}
	
	public ScatterPlotDecisionTree getScatterPlotDecisionTree(){
		return spdt;
	}
	
	public int getTotalNumberOfInstanceInLS(){
		return totalNumberOfInstanceInLS;
	}
	
	public int getTotalNumberOfInstanceInTS(){
		return totalNumberOfInstanceInTS;
	}
	
	public int getNumberOfCurrentInstance(){
		return currentNumberOfInstance;
	}
	
	public int getNumberOfStoppedInstance(){
		return numberOfStoppedInstance;
	}
	
	public List<Bitmap> getBitmapList(){
		return thumbGen.generateThumbs(scatterList);
	}
	
	public List<Bitmap> getNextPageBitmapList(){
		thumbsIndex++;
		List<ScatterPlot> newList = new ArrayList<ScatterPlot>();
		Pair p = pageThumbsList.get(thumbsIndex);
		for (int k = p.getFirst(); k < p.getSecond(); k++){
			Pair pair = pairList.get(k);
			ScatterPlot sc = new ScatterPlot(k,currentInstanceSet,pair);
			newList.add(sc);
		}
		scatterList = newList;
		return thumbGen.generateThumbs(scatterList);
	}
	
	public List<Bitmap> getPreviousPageBitmapList(){
		thumbsIndex--;
		List<ScatterPlot> newList = new ArrayList<ScatterPlot>();
		Pair p = pageThumbsList.get(thumbsIndex);
		for (int k = p.getFirst(); k < p.getSecond(); k++){
			Pair pair = pairList.get(k);
			ScatterPlot sc = new ScatterPlot(k,currentInstanceSet,pair);
			newList.add(sc);
		}
		scatterList = newList;
		return thumbGen.generateThumbs(scatterList);
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getNumberOfAttribute(){
		return numberOfAttribute;
	}
	
	public int getNumberOfPage(){
		return numberOfPage;
	}
	
	public int getTreeID(){
		return treeID;
	}
	
	/**
	 * 
	 */
	private void showDialogNetworkError(){
    	Builder builder = new AlertDialog.Builder(gameActivity);
    	TextView message = new TextView(gameActivity);
    	Resources r = gameActivity.getResources();
    	builder.setTitle(r.getString(R.string.network_lose_title));
    	message.setText(r.getString(R.string.network_lose_msg)); 	
    	message.setGravity(Gravity.CENTER_HORIZONTAL);
    	//message.setTextSize(r.getDimension(R.dimen.text_size));
    	message.setTextSize(20);	
    	builder.setView(message);
    	builder.setNeutralButton(r.getString(R.string.dialog_ok), 
    							new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			gameActivity.finish();
    		} });
    	builder.setCancelable(false);
    	builder.show();
	}
}

/**
 * This class allows to create a generator of pair used to
 * generate the list of scatter plot. 
 * 
 * @author Hervé de Sélys
 *
 */
class PairCombinaisonGenerator{
	
	private List<Pair> list = new ArrayList<Pair>();
	
	public PairCombinaisonGenerator(int max){	
		for (int i = 0; i < max; i++){
			for (int j = 0; j < max; j++){
				if (i != j){
					Pair p = new Pair(i,j);
					list.add(p);
				}
			}
		}
	}
	
	 /**
	  * Method that returns the list of pair.
	  * 
	  * @return list
	  */
	List<Pair> getList(){
		return list;
	}
	
	/**
	 * 
	 */
	void displayPair(){
		ListIterator<Pair> it = list.listIterator();		
		while (it.hasNext()){
			Pair p = it.next();
			System.out.println(p.getFirst() +" "+p.getSecond());
		}
	}	
}

/**
 * This class implements the representation of a pair.
 * This is a structure that contains two integers.
 * 
 * @author Hervé de Sélys
 *
 */

@SuppressWarnings("serial")
class Pair implements Serializable{
	private int firstCol;
	private int secondCol;
	
	public Pair(int first, int second){
		firstCol = first;
		secondCol = second;
	}
	
	/**
	 * Returns the first element of the pair.
	 * 
	 * @return firstCol
	 */
	public int getFirst(){
		return firstCol;
	}
	
	/**
	 * Returns the second element of the pair.
	 * 
	 * @return secondCol
	 */
	public int getSecond(){
		return secondCol;
	}
}

