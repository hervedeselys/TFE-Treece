package com.ulg.game.with.a.purpose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


public class ScatterPlotDecisionTree {
	
	private Node root;
	private Node currentNode;
	private LightNode currentLightNode;
	private Stack<Node> tree; //Represents the actual tree
	private Stack<LightNode> lightTree; //Used to know the current node of the tree for the view 
	private List<Node> treeList; //Used to compute global error
	
	public ScatterPlotDecisionTree(Node r){
		tree = new Stack<Node>();
		treeList = new ArrayList<Node>();
		lightTree = new Stack<LightNode>();
		root = r;
		root.setRoot();
		currentNode = root;
		tree.push(root);
		treeList.add(root);
		currentLightNode = new LightNode(root.getNodeID(), root.getThreshold());
		currentLightNode.setRoot();
		lightTree.push(currentLightNode);
	}
	
	public void addChildren(Node rightNode, Node leftNode){
		currentNode.setChildren(rightNode, leftNode);
		LightNode lightNodeRight = new LightNode(rightNode.getNodeID(), rightNode.getThreshold());
		LightNode lightNodeLeft = new LightNode(leftNode.getNodeID(), leftNode.getThreshold());
		currentLightNode.setChildren(lightNodeRight, lightNodeLeft);
		tree.push(rightNode);
		tree.push(leftNode);
		treeList.add(leftNode);
		treeList.add(rightNode);
		lightTree.push(lightNodeRight);
		lightTree.push(lightNodeLeft);
	}
	
	public Node getCurrentNode(){
		return currentNode;
	}
	
	public Node getNextNode(){
		Node node = tree.peek();
		if (!node.isRoot()){
			Node nextNode = tree.pop();
			LightNode nextLightNode = lightTree.pop();
			currentLightNode.setCurrentNode(false);
			currentNode = nextNode;
			currentLightNode = nextLightNode;
			currentLightNode.setCurrentNode(true);
			return nextNode;
		}
		else
			return null;
	}	
	
	public LightNode getLightRoot(){
		return lightTree.firstElement();
	}
	
	public float getGlobalErrorOnLS(){
		float error = 0;
		int totalNumberOfInstanceInLS = getTotalNumberOfInstanceInLS();
		for(int i=0; i < treeList.size(); i++){
			Node n = treeList.get(i);
			if(n.isLeaf())
				error += n.getLocalErrorOnLS()*
					((float)n.getLearningSetSize()/(float)totalNumberOfInstanceInLS);
		}		
		//Rounding
	    int tmp = (int) (error *100);
	    error = (float)tmp/100;
		
		return error;
	}
	
	public float getGlobalErrorOnTS(){
		float error = 0;
		int totalNumberOfInstanceInTS = getTotalNumberOfInstanceInTS();
		for(int i = 0; i < treeList.size(); i++){
			Node n = treeList.get(i);
			if (n.isLeaf())
				error += n.getLocalErrorOnTS()*
					((float)n.getTestSetSize()/(float)totalNumberOfInstanceInTS);
		}	
		//Rounding
	    int tmp = (int) (error *100);
	    error = (float)tmp/100;
		
		return error;
	}
	
	public int getTotalNumberOfInstanceInLS(){
		int total = 0;
		for(int i = 0; i < treeList.size(); i++){
			Node n = treeList.get(i);
			if (n.isLeaf())
				total += n.getLearningSetSize();
		}			
		return total;
	}
	
	public int getTotalNumberOfInstanceInTS(){
		int total = 0;
		for(int i = 0; i < treeList.size(); i++){
			Node n = treeList.get(i);
			if (n.isLeaf())
				total += n.getTestSetSize();
		}
		return total;
	}
}

/**
 * This is a light weigthed version of Node in order to build
 * the tree structure view. 
 * 
 * @author Hervé de Sélys
 *
 */
@SuppressWarnings("serial")
class LightNode implements Serializable{
	private int nodeID;
	private EquationLine threshold;
	private List<LightNode> children;
	private boolean root = false;
	private boolean currentNode = false;
	
	public LightNode(int n,  EquationLine line) {
		nodeID = n;
		threshold = line;
		children = new ArrayList<LightNode>();
	}
	
	public void setChildren(LightNode right, LightNode left){
		children.add(left);
		children.add(right);
	}
	
	public LightNode getLeftChild(){
		return children.get(0);
	}
	
	public LightNode getRighChild(){
		return children.get(1);
	}
	
	public EquationLine getThreshold(){
		return threshold;
	}
	
	public List<LightNode> getChildren(){
		return children;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public void setRoot(){
		root = true;
		currentNode = true;
	}

	public boolean isRoot(){
		return root;
	}
	
	public void setCurrentNode(boolean current){
		currentNode = current;
	}
	
	public boolean isCurrentNode(){
		return currentNode;
	}
	
	public boolean isCurrentBranch(){
		if (children.size() > 0 && (children.get(0).isCurrentNode() || children.get(1).isCurrentNode()
				|| children.get(0).isCurrentBranch() 
				|| children.get(1).isCurrentBranch()))
			return true;
		else 
			return false;
	}
}

/**
 * Class that represents a node of the scatter plot decision tree
 * 
 * @author Hervé
 *
 */
class Node{
	
	private int nodeID;
	private int parentID;
	private List<Instance> learningSet;
	private List<Instance> testSet;
	private List<Node> children;
	private EquationLine threshold;
	private boolean root = false;
	private boolean currentNode = false;
	
	
	public Node(int n, int p, EquationLine line, List<Instance> ls, List<Instance> ts){
		learningSet = ls;
		testSet = ts;
		threshold = line;
		nodeID = n;
		parentID = p;
		children = new ArrayList<Node>();
	}

	public void setChildren(Node right, Node left){
		children.add(left);
		children.add(right);
	}
	
	public List<Instance> getLearningSet(){
		return learningSet;
	}
	
	public List<Instance> getTestSet(){
		return testSet;
	}
	
	public int getLearningSetSize(){
		return learningSet.size();
	}
	
	public int getTestSetSize(){
		return testSet.size();
	}
	
	public Node getLeftChild(){
		return children.get(0);
	}
	
	public Node getRighChild(){
		return children.get(1);
	}
	
	public EquationLine getThreshold(){
		return threshold;
	}
	
	public List<Node> getChildren(){
		return children;
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public int getParentID(){
		return parentID;
	}
	
	public void setRoot(){
		root = true;
		currentNode = true;
	}
	
	public boolean isRoot(){
		return root;
	}
	
	public void setCurrentNode(boolean current){
		currentNode = current;
	}
	
	public boolean isCurrentNode(){
		return currentNode;
	}
	
	public boolean isLeaf(){
		if (children.isEmpty())
			return true;
		else
			return false;				
	}
	
	/**
	 * Returns the percent of presence of the majority class in the
	 * learning set of this node.
	 * 
	 * @return purity
	 */
	public float getPurity(){
		float numberOfInstance = learningSet.size();
		//List that enumerates the number of different ID
		List<Integer> outputIds = new ArrayList<Integer>();
		//List that contains all occurrences of each output
		List<Integer> outputIdList = new ArrayList<Integer>();
		//List that contains frequency of each output
		List<Integer> frequencies = new ArrayList<Integer>();
		for(int i = 0; i < numberOfInstance; i++){
			int id = learningSet.get(i).getOutputId();
			outputIdList.add(id);
			if (!outputIds.contains(id))
				outputIds.add(id);
		}
		
		for (int i = 0; i < outputIds.size(); i++)
			frequencies.add(Collections.frequency(outputIdList, outputIds.get(i)));
				
		float purity = Collections.max(frequencies)/numberOfInstance;
		return purity;	
	}
	
	/**
	 * Returns the majority class ID in the
	 * learning set of this node.
	 * 
	 * @return purity
	 */
	public int getMajortiyClassID(){
		float numberOfInstance = learningSet.size();
		//List that enumerates the number of different ID
		List<Integer> outputIds = new ArrayList<Integer>();
		//List that contains all occurrences of each output
		List<Integer> outputIdList = new ArrayList<Integer>();
		//List that contains frequency of each output
		List<Integer> frequencies = new ArrayList<Integer>();
		for(int i = 0; i < numberOfInstance; i++){
			int id = learningSet.get(i).getOutputId();
			outputIdList.add(id);
			if (!outputIds.contains(id))
				outputIds.add(id);
		}
		
		for (int i = 0; i < outputIds.size(); i++)
			frequencies.add(Collections.frequency(outputIdList, outputIds.get(i)));
		
		int maxFrequency = Collections.max(frequencies);
		return frequencies.indexOf(maxFrequency);	
	}
	
	/**
	 * Computes the local error on the test set.
	 * 
	 * @return error
	 */
	public float getLocalErrorOnTS(){
		float numberOfInstance = testSet.size();
		if (numberOfInstance == 0)
			return 0;
		
		//List that enumerates the number of different ID
		List<Integer> outputIds = new ArrayList<Integer>();
		//List that contains all occurrences of each output
		List<Integer> outputIdList = new ArrayList<Integer>();
		//List that contains frequency of each output
		List<Integer> frequencies = new ArrayList<Integer>();
		for(int i = 0; i < numberOfInstance; i++){
			int id = testSet.get(i).getOutputId();
			outputIdList.add(id);
			if (!outputIds.contains(id))
				outputIds.add(id);
		}

		for (int i = 0; i < outputIds.size(); i++)
			frequencies.add(Collections.frequency(outputIdList, outputIds.get(i)));
		
		//Rounding
		float error = 100 - (Collections.max(frequencies)/numberOfInstance)*100;
		int tmp = (int) (error *100);
		error = (float)tmp/100;
		return error;	
	}
	
	/**
	 * Computes the local error on the learning set.
	 * 
	 * @return error
	 */
	public float getLocalErrorOnLS(){
		float error = 100 - (getPurity()*100);
		int tmp = (int) (error *100);
		error = (float)tmp/100;
		return error;	
	}
}
