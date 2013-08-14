package com.ulg.game.with.a.purpose;

import java.util.List;

import com.ulg.game.with.a.purpose.R.id;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This class deals with the activity that shows the scatter plot.
 * It loads data and call the scatter plot view.
 * 
 * @author Hervé de Sélys
 *
 */
public class ScatterPlotActivityGame extends Activity{
		
	private ScatterPlotView scatterView;
	private ScatterPlot scatter;
	private TextView scoreText;
	private CountDownTimer cdt;
	private ScatterPlotGenerator scpg;

	private int playerScore = 0;
	private TextView timer;
	private TextView level;
	private TextView nextLevelText;
	private TextView scoreBonusText;
	private TextView timeBonusText;
	private ProgressBar progressbar;
	private int currentLevel;
    private long timeLeft;
    private int bonus = 100;
    private Button skipButton;
    private Button validateButton;
    private Resources res;
    private boolean firstTouch = false;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_scatter_game);
        
        res = getResources();
        progressbar = (ProgressBar) findViewById(id.progress_bar);
        timer = (TextView) findViewById(id.timer);
		scoreText = (TextView) findViewById(id.score);	     
	    scoreText.setText(res.getString(R.string.score)+" "+playerScore);
	    nextLevelText = (TextView) findViewById(id.next_level);
	    scoreBonusText = (TextView) findViewById(id.score_bonus);
	    timeBonusText = (TextView) findViewById(id.time_bonus);
	    
	    scoreBonusText.setTextColor(Color.GREEN);
	    scoreBonusText.setText("+100");
		Intent intent = getIntent();
		@SuppressWarnings("unchecked")
		List<String[]> data = (List<String[]>) intent.getSerializableExtra("Dataset");

		scpg = new ScatterPlotGenerator(this);
		scpg.generateScatterPlot(data);
		
		scatterView = (ScatterPlotView) findViewById(R.id.scatterPlot);
		level = (TextView) findViewById(id.level);
		currentLevel = scpg.getLevel();
		level.setText(res.getString(R.string.level_h)+" "+currentLevel);
		nextLevelText.setText(res.getString(R.string.level)+" "+ currentLevel);
		timer.setText(res.getString(R.string.timer_header)+" 20");
		toggleStopButton(true);
			
		scatter = scpg.getNextScatterPlot();
				
		scatterView.loadScatterPlot(scatter);			
		progressbar.setMax(scpg.getTotalNumberOfInstanceInLS());
	    
	    //Next button gives the next scatter plot of the tree
        skipButton = (Button) findViewById(id.skip_button);
	    OnClickListener buttonNextListener = new OnClickListener(){       	
        	public void onClick(View actualView){      		
        		scatter = scpg.getNextScatterPlot();
    			scatterView.loadScatterPlot(scatter);
    			nextLevelText.setText("");
    			scatterView.refreshView(false);	    			
				toggleStopButton(true);
        	} 	
        };
        skipButton.setOnClickListener(buttonNextListener);
        
        //Validate button
        validateButton = (Button) findViewById(id.validate_button);
        OnClickListener buttonValidateListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(scatterView.isGameOver()){
					showDialog();
					return;
				}
				
				Point start = scatterView.getRawPoint(scatterView.getStartPoint());
				Point end = scatterView.getRawPoint(scatterView.getEndoint());				
				EquationLine line = new EquationLine(start, end, 
													scatter.getPairAttribute());
				boolean ok = scpg.processTree(line);
				if(!ok)
					return;				
				
				if (currentLevel != scpg.getLevel()){
					currentLevel = scpg.getLevel();
					nextLevelText.setText(res.getString(R.string.level)+
							" "+ currentLevel);
					
					playerScore += bonus;
					bonus = 100;
					scoreText.setText(res.getString(R.string.score)+" "+playerScore);
					scoreBonusText.setText("+100");
				}
				else{			
					if(!scpg.isPureNodeCreated()){
						bonus = bonus - 5;
						scoreBonusText.setText("+"+bonus);	
					}
					else{
						bonus = bonus - 2;
						scoreBonusText.setText("+"+bonus);
					}			
				}
				if (scpg.getLevel() == 16){
					currentLevel--;
					scoreBonusText.setText("");
					showDialog();
					return;
				}
	    		scatter = scpg.getNextScatterPlot();
	    		level.setText(res.getString(R.string.level_h)+" "+scpg.getLevel());
				progressbar.setProgress(scpg.getNumberOfStoppedInstance());
				scatterView.loadScatterPlot(scatter);
				scatterView.refreshView(false);
				toggleStopButton(true);	
				createNewTimer(false, 2000);
				timeBonusText.setTextColor(Color.GREEN);
				timeBonusText.setText("+2s");
			}			
		};
		validateButton.setOnClickListener(buttonValidateListener);
		        
         
        //Checks constantly if the screen contains a line or not.
	    scatterView.setOnTouchListener(new OnTouchListener() {
	    	public boolean onTouch(View v, MotionEvent event) {
				if (!scatterView.isGameOver()){
					if (!firstTouch){
						createNewTimer(true, 0);
						firstTouch = true;
					}
	 				if (scatterView.isNoLineTraced())
	 					toggleStopButton(true);
	 				else{
	 					toggleStopButton(false);
	 					nextLevelText.setText("");
	 					timeBonusText.setText("");
	 				}		
				}
				return false;
			}
		});  
	}
	
	/**
	 * TODO
	 */
	private void showDialog(){
        Ranking ranking = new Ranking(this);
        List<Rank> list = ranking.readRanking();
        Resources r = getResources();
        //Show a box for entering score if the current score is better
        //than the last one or if there is no score in the ranking 
        if (list.size() == 0 || list.size() < 10 || playerScore >= ranking.getLastRankingScore() ){
        	Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(r.getString(R.string.dialog_title_end));
        	builder.setMessage(r.getString(R.string.dialog_msg_score));
        	builder.setPositiveButton(r.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			registerNameAndScore();
        		} });
        
        	builder.setNegativeButton(r.getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			finish();
        		} });
        	builder.setCancelable(false);
        	builder.show();
        }
        else{
        	Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(r.getString(R.string.dialog_title_end));
        	builder.setMessage(r.getString(R.string.dialog_try_again));
        	builder.setNeutralButton(r.getString(R.string.dialog_ok), 
        								new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			finish();
        		} });
        	builder.setCancelable(false);
        	builder.show();
        }	
	}
	
	/**
	 * TODO
	 */
	private void registerNameAndScore(){		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		Resources r = getResources();
		alert.setTitle(r.getString(R.string.dialog_ranking));
		alert.setMessage(r.getString(R.string.dialog_enter_name));

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		alert.setView(input);

		alert.setPositiveButton(r.getString(R.string.dialog_ok),
										new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editable playerName = input.getText();
		  		  
				Intent intent = new Intent(ScatterPlotActivityGame.this,
														RankingActivity.class);
				intent.putExtra("PlayerName", playerName.toString());
				intent.putExtra("PlayerScore", String.valueOf(playerScore));
				intent.putExtra("Level", currentLevel);
				startActivity(intent);
				finish();
		  }
		});

		alert.setNegativeButton(r.getString(R.string.dialog_cancel), 
										new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
		  }
		});
		
		alert.show();
	}
	
	/**
	 * Method that allow to switch between next or stop button active
	 * 
	 * @param setStop, true if stop button has to be enabled, 
	 * 				   false to enable next button.
	 */
	private void toggleStopButton(boolean setStop){
		Button validateButton = (Button) findViewById(id.validate_button);
		Button skipButton = (Button) findViewById(id.skip_button);
		
		if (setStop){
			skipButton.setEnabled(true);
			validateButton.setEnabled(false);
		}
		else{
			skipButton.setEnabled(false);
			validateButton.setEnabled(true);
		}
	}
	
	private void createNewTimer(boolean firstLaunch, long timeToAddOrRetrieve){
		if (!firstLaunch)
			cdt.cancel();
		else
			timeLeft = 20000;
		cdt = new CountDownTimer(timeLeft+timeToAddOrRetrieve, 1000) {	
			@Override
			public void onTick(long millisUntilFinished) {
				timeLeft = millisUntilFinished;
				timer.setText(res.getString(R.string.timer_header)+" "+ timeLeft / 1000);
			}
			
			@Override
			public void onFinish() {
				timer.setText(res.getString(R.string.timer_over));
				nextLevelText.setText(res.getString(R.string.game_over));
	            scatterView.setGameOver();
	            scatterView.refreshView(false);
	            skipButton.setEnabled(false);
	            skipButton.setAlpha(100);
	            validateButton.setEnabled(true);
	            validateButton.setText(res.getString(R.string.back));
			}
		};
		cdt.start();
	}
	
	 @Override
     public boolean onKeyDown(int keyCode, KeyEvent event){
	            
	     if (keyCode == KeyEvent.KEYCODE_BACK && scatterView.isGameOver()) {
	    	 showDialog();
	     }
	     else if (keyCode == KeyEvent.KEYCODE_BACK && !scatterView.isGameOver())
	    	 finish();
	     return false;
     }
}