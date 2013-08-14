package com.ulg.game.with.a.purpose;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.ulg.game.with.a.purpose.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * This class represents the grid of scatter plot. This grid allows
 * the user to choose which scatter he wants to process.
 * 
 * @author Hervé de Sélys
 *
 */
public class GridActivity extends Activity {
	
	private GridView gridView;
	private ScatterPlotAdapter spa;
	private String database;
	private int nodeCount = 1;
	private int totalNumberOfInstance;
	private int numberOfStoppedInstance = 0;
	private int numberOfCurrentInstance;
	private Button stopButton, treeViewButton;
	private ScatterPlotGenerator scpg;
	private ProgressBar progressBar;
	private int pageNumber = 1;
	private int numberOfPage;
	private TextView localErrorText;
	private TextView globalErrorText;
	private int thumbsPerPage = 12;
	private TextView pageNumberText;
	private int limitOfObjectOnScatterPlotView = 200;
	private String preference = "PreferenceFile";
	private Resources r;
	private boolean sendData = true;


	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid);
		
		SharedPreferences settings = getSharedPreferences(preference, 0);
        if(settings.getBoolean("firstTimeLearning", true)){
        	settings.edit().putBoolean("firstTimeLearning", false).commit(); 
        	Resources r = getResources();
        	Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(r.getString(R.string.dialog_title_first_time_learning));
        	builder.setMessage(r.getString(R.string.dialog_msg_first_time_learning));
        	builder.setPositiveButton(r.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			Intent intent = new Intent(getApplicationContext(), HelpLearningActivity.class);
        			startActivity(intent);
        		} });
        
        	builder.setNegativeButton(r.getString(R.string.skip), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			return;
        		} });
        	builder.setCancelable(false);
        	builder.show();
        }
		r = getResources();
		int size = r.getConfiguration().screenLayout & 
							Configuration.SCREENLAYOUT_SIZE_MASK;
		if(size == Configuration.SCREENLAYOUT_SIZE_NORMAL)
			thumbsPerPage = 9;
		Intent intent = getIntent();
		database = intent.getStringExtra("DATABASE");
		gridView = (GridView) findViewById(id.grid);
		progressBar = (ProgressBar) findViewById(id.progress_bar);
		pageNumberText = (TextView) findViewById(id.page_info);
		localErrorText = (TextView) findViewById(id.local_error);
		globalErrorText = (TextView) findViewById(id.global_error);
		
		SocketClientManager scm = new SocketClientManager();
        scm.setDataToSend(database);
        List<String[]> data = null;
        try {
			data = (List<String[]>) scm.execute("newdataset").get();
			if (data == null){
				showDialogNetworkError();
				return;
			}		
		} catch (InterruptedException e) {
			System.err.println("Interrupted Exception");
            System.err.println(e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Execution Exception");
            System.err.println(e.getMessage());
		}

        scpg = new ScatterPlotGenerator(thumbsPerPage);
        scpg.generateScatterPlot(data);		
		spa = new ScatterPlotAdapter(GridActivity.this, scpg.getBitmapList());
		gridView.setAdapter(spa);
		numberOfCurrentInstance = scpg.getNumberOfCurrentInstance();
		totalNumberOfInstance = scpg.getTotalNumberOfInstanceInLS();
		numberOfPage = scpg.getNumberOfPage();
		pageNumberText.setText(r.getString(R.string.page_info)+" "+
				pageNumber+"/"+ numberOfPage);
	
		progressBar.setMax(totalNumberOfInstance);
		progressBar.setSecondaryProgress(numberOfStoppedInstance+
											numberOfCurrentInstance);
		progressBar.setProgress(numberOfStoppedInstance);
		
		Node currentNode = scpg.getScatterPlotDecisionTree().getCurrentNode();
		float localErrorOnLS = currentNode.getLocalErrorOnLS();
	    float localErrorOnTS = currentNode.getLocalErrorOnTS();
	    int numberOfObjectInLS = scpg.getScatterPlotDecisionTree()
	    							.getCurrentNode().getLearningSetSize();
	    int numberOfObjectInTS = scpg.getScatterPlotDecisionTree()
									.getCurrentNode().getTestSetSize();
	    int numberOfObjectTotalInLS = scpg.getTotalNumberOfInstanceInLS();
	    int numberOfObjectTotalInTS = scpg.getTotalNumberOfInstanceInTS();
	    localErrorText.setText(r.getString(R.string.local_error)+" "+
	    		localErrorOnLS+"% ("+numberOfObjectInLS+"/"+
	    		numberOfObjectTotalInLS+") (LS) \n" +
	    		"		"+localErrorOnTS+"% ("+numberOfObjectInTS+"/"+
	    		numberOfObjectTotalInTS+") (TS)");
	    
	    float globalErrorOnLS = scpg.getScatterPlotDecisionTree()
	    							.getGlobalErrorOnLS();
	    float globalErrorOnTS = scpg.getScatterPlotDecisionTree()
	    							.getGlobalErrorOnTS();
		
	    globalErrorText.setText(r.getString(R.string.global_error)+" "+
	    		globalErrorOnLS+"% (LS) \n" +
	    		"		"+globalErrorOnTS+"% (TS)");
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	nodeCount--;
	        	scpg.setCurrentScatterPlot(position);
	        	ScatterPlot sc = scpg.getScatterPlotList().get(position);
	        	//Build a light scatter plot from chosen scatter plot in order to
	        	//avoid failed binder exception and to not have too many points 
	        	//on the screen.
	        	List<Instance> scInstanceSet = sc.getInstanceSet();
	        	List<Instance> light = new ArrayList<Instance>();
	        	int limit;
	        	if(scInstanceSet.size() < limitOfObjectOnScatterPlotView)
	        		limit = scInstanceSet.size();
	        	else
	        		limit = limitOfObjectOnScatterPlotView;
	        	for(int i = 0; i < limit; i++)
	        		light.add(scInstanceSet.get(i));
	        	ScatterPlot lightScatter = new ScatterPlot(sc.getId(), light, sc.getPairAttribute());
	        	Intent intent = new Intent(getApplicationContext(), ScatterPlotActivityLearning.class);
	        	intent.putExtra("ScatterChoice", position);
	        	intent.putExtra("TotalNumberOfInstance", totalNumberOfInstance);
	        	intent.putExtra("NumberOfScatterPlot", thumbsPerPage);
	        	intent.putExtra("NumberOfStoppedInstance",numberOfStoppedInstance);
	        	intent.putExtra("LocalError", localErrorText.getText());
	        	intent.putExtra("GlobalError", globalErrorText.getText());
	        	intent.putExtra("ScatterPlot", (Serializable) lightScatter);
	        	startActivityForResult(intent, 1);
	        }
	    });
		
		stopButton = (Button) findViewById(id.stop_button);		
		OnClickListener stopButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (nodeCount == 0){
					finish();
					return;
				}
				else if(nodeCount == 1){
					progressBar.setProgress(totalNumberOfInstance);
					//Send an empty line with the id of the tree to know in the 
					//DB if the tree is complete or not
					if(sendData){
						SocketClientManager scm = new SocketClientManager();
						EquationLine line = new EquationLine();
						line.setDatabase("END");
						line.setTreeID(scpg.getTreeID());
						scm.setDataToSend(line);
						scm.execute("equationlinefromlearning");
					}
					nodeCount--;
					stopButton.setText(r.getString(R.string.leave));
					gridView.setEnabled(false);
					showDialogFinish();
					return;
				}
				
				numberOfCurrentInstance = scpg.getNumberOfCurrentInstance();
				numberOfStoppedInstance += numberOfCurrentInstance;			
				AsyncLoader load = new AsyncLoader(GridActivity.this,"getNextNode", r);
				load.execute(scpg, spa, gridView, progressBar, numberOfStoppedInstance);
				nodeCount--;
				if (nodeCount == 1)
					stopButton.setText(r.getString(R.string.finish));
				progressBar.setProgress(numberOfStoppedInstance);
			}
		};
		stopButton.setOnClickListener(stopButtonListener);
		stopButton.setText(r.getString(R.string.finish));
		
		treeViewButton = (Button) findViewById(id.tree_view_button);
		OnClickListener treeViewButtonListener = new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GridActivity.this, DecisionTreeViewerActivity.class);
				LightNode lightRoot = scpg.getScatterPlotDecisionTree().getLightRoot();
				intent.putExtra("Node", (Serializable) lightRoot);
				intent.putExtra("Level", 0);
				startActivity(intent);
				return;				
			}
		};
		treeViewButton.setOnClickListener(treeViewButtonListener);
		
		ImageButton previousPage = (ImageButton) findViewById(id.previous_page_button);
		OnClickListener buttonPreviousScatterListener = new OnClickListener() {
			@Override
			public void onClick(View v) {	
				pageNumberText.setText(r.getString(R.string.page_info)+" "+ 
							(--pageNumber)+"/"+ numberOfPage);
				AsyncLoader load = new AsyncLoader(GridActivity.this,"previousPage", r);
				load.execute(scpg, spa, gridView);			
				checkNavigationPageButton();
			}
		};
		previousPage.setOnClickListener(buttonPreviousScatterListener);
		previousPage.setEnabled(false);
		previousPage.setAlpha(100);
		
		ImageButton nextPage = (ImageButton) findViewById(id.next_page_button);	
		OnClickListener buttonNextScatterListener = new OnClickListener() {
			@Override
			public void onClick(View v) {		
				pageNumberText.setText(r.getString(R.string.page_info)+" "+
						(++pageNumber)+"/"+ numberOfPage);
				AsyncLoader load = new AsyncLoader(GridActivity.this,"nextPage", r);
				load.execute(scpg, spa, gridView);		
				checkNavigationPageButton();
			}
		};
		nextPage.setOnClickListener(buttonNextScatterListener);
		if (numberOfPage == 1){
			nextPage.setEnabled(false);
			nextPage.setAlpha(100);
		}
	}
	
	/**
	 * This method is only called if the user has made
	 * some treatment to the scatter plot chosen. It allows
	 * to not fail when the user click on the back button
	 * without processing the scatter plot.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == 1 && data !=null){
			//If we have processed the chosen scatter plot
			if (resultCode == RESULT_OK){
				stopButton.setEnabled(true);	
				int currentScatterChoice = data.getIntExtra("currentScatter", 0);
				scpg.setCurrentScatterPlot(currentScatterChoice);
				numberOfStoppedInstance = data.getIntExtra
											("NumberOfStoppedInstance",0);
				EquationLine line = (EquationLine) data.getSerializableExtra("Line");
				line.setDatabase(database);
				AsyncLoader load = new AsyncLoader(GridActivity.this,"processTree", r);
				load.execute(scpg, spa, gridView, line, progressBar, numberOfStoppedInstance);
				nodeCount += 2;			
				progressBar.setProgress(numberOfStoppedInstance);
				stopButton.setText(r.getString(R.string.stop));
			}
		//If we just zoom on it by selecting it and back to the gridview.
		}else
			nodeCount++;
	}
	
	/**
	 * TODO
	 * @param finish
	 */
	private void showDialogFinish(){
    	Builder builder = new AlertDialog.Builder(this);
    	TextView message = new TextView(this);
    	builder.setTitle(r.getString(R.string.dialog_complete));
    	message.setText(r.getString(R.string.thank_you));
    	
    	message.setGravity(Gravity.CENTER_HORIZONTAL);
    	//message.setTextSize(r.getDimension(R.dimen.text_size));
    	message.setTextSize(20);
    	builder.setView(message);
    	builder.setNeutralButton(r.getString(R.string.dialog_ok),
    								new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			//finish();
    		} });
    	builder.setCancelable(false);
    	builder.show();
	}
	
	/**
	 * 
	 */
	private void showDialogNetworkError(){
    	Builder builder = new AlertDialog.Builder(this);
    	TextView message = new TextView(this);
    	Resources r = getResources();
		builder.setTitle(r.getString(R.string.server_unreachable));
    	message.setText(r.getString(R.string.server_unreachable_msg));
    	message.setGravity(Gravity.CENTER_HORIZONTAL);
    	//message.setTextSize(r.getDimension(R.dimen.text_size));
    	message.setTextSize(20);
    	builder.setView(message);
    	builder.setNeutralButton(r.getString(R.string.dialog_ok),
    								new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			finish();
    		} });
    	builder.setCancelable(false);
    	builder.show();
	}
	
	@SuppressWarnings("deprecation")
	private void checkNavigationPageButton(){
		ImageButton previousPage = (ImageButton) findViewById(id.previous_page_button);
		ImageButton nextPage = (ImageButton) findViewById(id.next_page_button);
		if (pageNumber > numberOfPage-1){
			nextPage.setAlpha(100);
			nextPage.setEnabled(false);
		}
		else{
			nextPage.setAlpha(250);
			nextPage.setEnabled(true);
		}
		
		if (pageNumber == 1){
			previousPage.setAlpha(100);
			previousPage.setEnabled(false);
		}
		else{
			previousPage.setAlpha(250);
			previousPage.setEnabled(true);
		}
		
	}
}
