package com.ulg.game.with.a.purpose;

import com.ulg.game.with.a.purpose.R.id;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncLoader extends AsyncTask<Object, Void, Integer> {
	
	private ProgressDialog loader;
	private GridView gridView;
	private ScatterPlotAdapter spa;
	private String request;
	private Activity activityContext;
	private ScatterPlotGenerator scpg;
	private Resources res;
	
	public AsyncLoader(Activity c, String r, Resources resources){
		loader = new ProgressDialog(c);
		loader.setCancelable(false);
		request = r;
		activityContext = c;
		res = resources;
	}

	protected void onPreExecute() {		
		if (request.equals("processTree"))
			loader.setMessage(res.getString(R.string.process_tree));
		else if(request.equals("getNextNode"))
			loader.setMessage(res.getString(R.string.next_node));
		else if(request.equals("previousPage"))
			loader.setMessage(res.getString(R.string.build_prev_page));
		else if(request.equals("nextPage"))
			loader.setMessage(res.getString(R.string.build_next_page));
		else
			loader.setMessage(res.getString(R.string.loading));
		loader.show();
    }

    protected void onPostExecute(Integer result) {
    	gridView.setAdapter(spa);
	    if(request.equals("processTree") || request.equals("getNextNode")){
		    TextView localErrorText = (TextView) activityContext.findViewById(id.local_error);
		    TextView globalErrorText = (TextView) activityContext.findViewById(id.global_error);
		    Node currentNode = scpg.getScatterPlotDecisionTree().getCurrentNode();
		    float localErrorOnLS = currentNode.getLocalErrorOnLS();
		    float localErrorOnTS = currentNode.getLocalErrorOnTS();
		    int numberOfObjectInLS = scpg.getScatterPlotDecisionTree()
					.getCurrentNode().getLearningSetSize();
		    int numberOfObjectInTS = scpg.getScatterPlotDecisionTree()
					.getCurrentNode().getTestSetSize();
		    int numberOfObjectTotalInLS = scpg.getTotalNumberOfInstanceInLS();
		    int numberOfObjectTotalInTS = scpg.getTotalNumberOfInstanceInTS();
		    localErrorText.setText(res.getString(R.string.local_error)+" "+
		    		localErrorOnLS+"% ("+numberOfObjectInLS+"/"+
		    		numberOfObjectTotalInLS+") (LS) \n" +
		    		"		"+localErrorOnTS+"% ("+
		    		numberOfObjectInTS+"/"+numberOfObjectTotalInTS+") (TS)");
		    
		    float globalErrorOnLS = scpg.getScatterPlotDecisionTree()
		    							.getGlobalErrorOnLS();
		    float globalErrorOnTS = scpg.getScatterPlotDecisionTree()
		    							.getGlobalErrorOnTS();
			
		    globalErrorText.setText(res.getString(R.string.global_error)+" "+
		    		globalErrorOnLS+"% (LS) \n" +
		    		"		"+globalErrorOnTS+"% (TS)");
	    }
	    loader.dismiss();
    }
        
	@Override
	protected Integer doInBackground(Object... params) {
		if(request.equals("getNextNode")){
			scpg = (ScatterPlotGenerator) params[0];
			spa = (ScatterPlotAdapter) params[1];
			gridView = (GridView) params[2];
			ProgressBar progressBar = (ProgressBar) params[3];
			Integer numberOfStoppedInstance = (Integer) params[4];
			scpg.processToNextNode();
			spa.setBitmapList(scpg.getBitmapList());
			int numberOfCurrentInstance = scpg.getNumberOfCurrentInstance();
			progressBar.setSecondaryProgress(numberOfStoppedInstance+
												numberOfCurrentInstance);
		}
		else if(request.equals("processTree")){
			scpg = (ScatterPlotGenerator) params[0];
			spa = (ScatterPlotAdapter) params[1];
			gridView = (GridView) params[2];
			EquationLine line = (EquationLine) params[3];
			ProgressBar progressBar = (ProgressBar) params[4];
			Integer numberOfStoppedInstance = (Integer) params[5];
			scpg.processTree(line);
			spa.setBitmapList(scpg.getBitmapList());
			progressBar.setSecondaryProgress(numberOfStoppedInstance+
					scpg.getNumberOfCurrentInstance());
		}
		else if(request.equals("previousPage")){
			scpg = (ScatterPlotGenerator) params[0];
			spa = (ScatterPlotAdapter) params[1];
			gridView = (GridView) params[2];
			spa.setBitmapList(scpg.getPreviousPageBitmapList());
		}
		else if(request.equals("nextPage")){
			scpg = (ScatterPlotGenerator) params[0];
			spa = (ScatterPlotAdapter) params[1];
			gridView = (GridView) params[2];
			spa.setBitmapList(scpg.getNextPageBitmapList());
		}
		return 1;
	}

}
