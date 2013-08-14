package com.ulg.game.with.a.purpose;

import java.io.Serializable;

import com.ulg.game.with.a.purpose.R.id;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * This class deals with the activity that shows the scatter plot.
 * It loads data and call the scatter plot view.
 * 
 * @author Hervé de Sélys
 *
 */
public class ScatterPlotActivityLearning extends Activity{
	
	private ScatterPlotView scatterView;
	
    private int numberOfScatterPlot;
    private int scatterPlotCount = 0;
    private int scatterChoice;
    private int numberOfStoppedInstance;
    private int numberOfCurrentInstance;
    private int defaultValue = -1;
    private ProgressBar progressBar;
    private ScatterPlot scatter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
        
		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME  | 
        							ActionBar.DISPLAY_SHOW_CUSTOM);	
		
        setContentView(R.layout.activity_scatter_learning);
        Resources res = getResources();
        
        progressBar = (ProgressBar) findViewById(id.progress_bar);
        TextView scatterInfo = (TextView) findViewById(id.scatter_info);  
        TextView localErrorText = (TextView) findViewById(id.local_error);
        TextView globalErrorText = (TextView) findViewById(id.global_error);
		scatterView = (ScatterPlotView) findViewById(R.id.scatterPlot);
		
		toggleValidationButton(true);
		
		Intent intent = getIntent();
		scatterChoice = intent.getIntExtra("ScatterChoice", defaultValue);
		int totalNumberOfInstance = intent.getIntExtra("TotalNumberOfInstance",
														defaultValue);
		scatter = (ScatterPlot) intent.getSerializableExtra("ScatterPlot");
		
		scatterPlotCount = scatterChoice+1;
		scatterView.loadScatterPlot(scatter);
		numberOfScatterPlot = intent.getIntExtra("NumberOfScatterPlot", 0);
		numberOfStoppedInstance = intent.getIntExtra("NumberOfStoppedInstance", 
														0);
		CharSequence localError = intent.getCharSequenceExtra("LocalError");
		localErrorText.setText(localError);
		CharSequence globalError = intent.getCharSequenceExtra("GlobalError");
		globalErrorText.setText(globalError);
		
		numberOfCurrentInstance = scatter.getInstanceSet().size();		
		
		progressBar.setMax(totalNumberOfInstance);
		progressBar.setSecondaryProgress(numberOfStoppedInstance+
											numberOfCurrentInstance);
		progressBar.setProgress(numberOfStoppedInstance);
		scatterInfo.setText(res.getString(R.string.n_scatter)+" "+
								scatterPlotCount+"/"+numberOfScatterPlot);
	    	    
		
	    //Back button to return to the gridview
        final Button backButton = (Button) findViewById(id.back_button);
	    OnClickListener buttonBackListener = new OnClickListener(){       	
        	public void onClick(View actualView){
        		finish();
        	}
        };
        backButton.setOnClickListener(buttonBackListener);
        
        //Validate button
        Button validateButton = (Button) findViewById(id.validate_button);
        OnClickListener buttonValidateListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Point start = scatterView.getRawPoint(scatterView.getStartPoint());
				Point end = scatterView.getRawPoint(scatterView.getEndoint());
				EquationLine line = new EquationLine(start, end, 
													scatter.getPairAttribute());
							
				Intent returnIntent = new Intent();
				returnIntent.putExtra("NumberOfStoppedInstance",
										numberOfStoppedInstance);
				returnIntent.putExtra("currentScatter", scatterChoice);
				returnIntent.putExtra("Line",(Serializable)line);
				setResult(RESULT_OK, returnIntent);
				finish();		
        	}
			
		};
		validateButton.setOnClickListener(buttonValidateListener);
         
         //Checks constantly if the screen contains a line or not.
         scatterView.setOnTouchListener(new OnTouchListener() {
 			public boolean onTouch(View v, MotionEvent event) {
 				if (scatterView.isNoLineTraced())
 					toggleValidationButton(true);
 				else
 					toggleValidationButton(false);
 				
 				return false;
 			}
 		});
         
        //Switch used to switch the trace mode
 	    final ToggleButton modeToggle = (ToggleButton) findViewById(id.toggleMode);
 	    modeToggle.setChecked(false);
        modeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {	
 			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
 				if (isChecked) {
 		            // The toggle is enabled
 					scatterView.setStraightMode(true);
 					scatterView.refreshView(false);
 		        } else {
 		            // The toggle is disabled
 		        	scatterView.setStraightMode(false);
 					scatterView.refreshView(false);
 		        }
 			}
 		});  
	}
	
	/**
	 * Method that allow to switch enabled or disabled the validate button
	 * 
	 * @param setStop, true if stop button has to be enabled, 
	 * 				   false to enable next button.
	 */
	private void toggleValidationButton(boolean on){
		Button validateButton = (Button) findViewById(id.validate_button);		
		if (on){
			validateButton.setEnabled(false);
		}
		else{
			validateButton.setEnabled(true);
		}
	}
}