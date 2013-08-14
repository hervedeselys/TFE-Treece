package com.ulg.game.with.a.purpose;


import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends Activity {
	
	List<String[]> data = null;
	private String preference = "PreferenceFile";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        SharedPreferences settings = getSharedPreferences(preference, 0);
        if(settings.getBoolean("firstTimeGame", true)){
        	settings.edit().putBoolean("firstTimeGame", false).commit(); 
        	Resources r = getResources();
        	Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle(r.getString(R.string.dialog_title_first_time_game));
        	builder.setMessage(r.getString(R.string.dialog_msg_first_time_game));
        	builder.setPositiveButton(r.getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			Intent intent = new Intent(getApplicationContext(), HelpGameActivity.class);
        			startActivity(intent);
        		} });
        
        	builder.setNegativeButton(r.getString(R.string.skip), new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface dialog, int which) {
        			return;
        		} });
        	builder.setCancelable(false);
        	builder.show();
        }
         
        Button buttonNewGame = (Button) findViewById(R.id.new_game);
        OnClickListener buttonNewGameListener = new OnClickListener(){	
        	public void onClick(View actualView){  	
		    	Intent intent = new Intent(getApplicationContext(), ScatterPlotActivityGame.class);
		    	intent.putExtra("Dataset",(Serializable) data);
        		startActivity(intent);	
        	}
        };
        buttonNewGame.setOnClickListener(buttonNewGameListener);
        
        Button buttonRanking = (Button) findViewById(R.id.ranking);
        OnClickListener buttonRankingListener = new OnClickListener(){	
        	public void onClick(View actualView){
        		Intent intent = new Intent(getApplicationContext(), RankingActivity.class);
        		startActivity(intent);     		
        	}
        };
        buttonRanking.setOnClickListener(buttonRankingListener);
        
        Button buttonBack = (Button) findViewById(R.id.back);
        OnClickListener buttonBackListener = new OnClickListener(){
        	public void onClick(View actualView){
        		finish();
        	}
        };
        buttonBack.setOnClickListener(buttonBackListener);
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
	
	@SuppressWarnings("unchecked")
	protected void onResume(){
		super.onResume();
		SocketClientManager scm = new SocketClientManager();
        scm.setDataToSend("LEVEL1");     
        try {
			data = (List<String[]>) scm.execute("newdataset").get();
			if (data == null){
				showDialogNetworkError();
				return;
			}		
		} catch (InterruptedException e) {
			System.err.println("Interrupted exception.");
			System.err.println(e.getMessage());
		} catch (ExecutionException e) {
			System.err.println("Execution exception.");
			System.err.println(e.getMessage());
		}
	}
}
