package com.ulg.game.with.a.purpose;


import java.util.ArrayList;
import java.util.HashMap;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;

import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 *  Main activity that is started when we lunch the application
 * 
 * @author Hervé de Sélys
 *
 */

public class MainActivity extends Activity {
	
	private AlertDialog ad;
	private ProgressDialog loader;
	private Resources r;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final ArrayList<HashMap<String, String>> listItem = 
        		new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        r = getResources();
        
        map = new HashMap<String, String>();
        map.put("Title", "IRIS");
        map.put("Description", r.getString(R.string.number_of_instances)+" 120 \n" +
        		r.getString(R.string.number_of_attributes)+" 4 \n" +
        		r.getString(R.string.number_of_scatter_plot)+" 12 \n" +
        		r.getString(R.string.number_of_classes)+" 3");
        listItem.add(map);
        
        map = new HashMap<String, String>();
        map.put("Title", "BREASTCANCER");
        map.put("Description", r.getString(R.string.number_of_instances)+" 559 \n" +
        		r.getString(R.string.number_of_attributes)+" 9 \n" +
        		r.getString(R.string.number_of_scatter_plot)+" 72 \n" +
        		r.getString(R.string.number_of_classes)+" 2");
        listItem.add(map);
        
        map = new HashMap<String, String>();
        map.put("Title", "PIMA-INDIANS-DIABETES");
        map.put("Description", r.getString(R.string.number_of_instances)+" 614 \n" +
        		r.getString(R.string.number_of_attributes)+" 8 \n" +
        		r.getString(R.string.number_of_scatter_plot)+" 56 \n" +
        		r.getString(R.string.number_of_classes)+" 2");
        listItem.add(map);
        
        map = new HashMap<String, String>();
        map.put("Title", "VEHICLE");
        map.put("Description", r.getString(R.string.number_of_instances)+" 677 \n" +
        		r.getString(R.string.number_of_attributes)+" 18 \n" +
        		r.getString(R.string.number_of_scatter_plot)+" 306 \n" +
        		r.getString(R.string.number_of_classes)+" 4");
        listItem.add(map);
        
        map = new HashMap<String, String>();
        map.put("Title", "LANDSAT");
        map.put("Description", r.getString(R.string.number_of_instances)+" 3548 \n" +
        		r.getString(R.string.number_of_attributes)+" 36 \n" +
        		r.getString(R.string.number_of_scatter_plot)+" 1260 \n" +
        		r.getString(R.string.number_of_classes)+" 7");
        listItem.add(map);
        
        final SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
        						listItem, R.layout.database_list, 
        						new String[] {"Title", "Description"}, 
        						new int[] {R.id.database, R.id.description});
        
        final Builder databaseChoice = new AlertDialog.Builder(this);
       
        Button buttonGame = (Button) findViewById(R.id.the_game);
        OnClickListener buttonGameListener = new OnClickListener(){     	
        	public void onClick(View actualView){
        		Intent intent = new Intent(getApplicationContext(), 
        												GameActivity.class);
        		startActivity(intent);	
        	}
        };
        buttonGame.setOnClickListener(buttonGameListener);
        
        Button buttonLearning = (Button) findViewById(R.id.learning);
        OnClickListener buttonLearningListener = new OnClickListener(){       	
			public void onClick(View actualView){
		    	databaseChoice.setTitle(r.getString(R.string.choose_database));
				databaseChoice.setSingleChoiceItems(adapter, -1,
										new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ad.dismiss();
						loader = new ProgressDialog(MainActivity.this);
						loader.setMessage(r.getString(R.string.loading));
						loader.show();
						Intent intent = new Intent(getApplicationContext(), 
															GridActivity.class);
						HashMap<String, String> selectedItem = listItem.get(which);
						intent.putExtra("DATABASE", selectedItem.get("Title"));						
		        		startActivity(intent);
					}
				});    					
				ad = databaseChoice.create();
				ad.show();
				
        	}
        };
        buttonLearning.setOnClickListener(buttonLearningListener);
        
        ConnectivityManager connMgr = (ConnectivityManager) 
        						getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    
	    if (networkInfo == null || !networkInfo.isConnected()) {
	    	Builder lackOfConnectivty = new AlertDialog.Builder(this);
	    	TextView message = new TextView(this);
	    	message.setText(r.getString(R.string.network_issue));
	    	message.setGravity(Gravity.CENTER_HORIZONTAL);
	    	//message.setTextSize(r.getDimension(R.dimen.text_size));
	    	message.setTextSize(20);
	    	lackOfConnectivty.setTitle(r.getString(R.string.title_network_issue));
	    	lackOfConnectivty.setView(message);
	    	lackOfConnectivty.setNeutralButton(r.getString(R.string.dialog_ok), 
	    								new DialogInterface.OnClickListener() {
	    		public void onClick(DialogInterface dialog, int which) {
	    			
	    		} });
	    	lackOfConnectivty.setCancelable(false);
	    	lackOfConnectivty.show();
	    	
	    	buttonGame.setEnabled(false);
	    	buttonLearning.setEnabled(false);
	    }
        
        Button buttonHelp = (Button) findViewById(R.id.help);
        OnClickListener buttonHelpListener = new OnClickListener(){     	
        	public void onClick(View actualView){
        		Intent intent = new Intent(getApplicationContext(), 
        									HelpActivity.class);
        		startActivity(intent);	
        	}
        };
        buttonHelp.setOnClickListener(buttonHelpListener);
        
        Button buttonAbout = (Button) findViewById(R.id.about);
        OnClickListener buttonAboutListener = new OnClickListener(){       	
        	public void onClick(View actualView){
        		Intent intent = new Intent(getApplicationContext(), 
						AboutActivity.class);
        		startActivity(intent);
        	}
        };
        buttonAbout.setOnClickListener(buttonAboutListener);
        
        Button buttonLeave = (Button) findViewById(R.id.leave);
        OnClickListener buttonLeaveListener = new OnClickListener(){       	
        	public void onClick(View actualView){
        		finish();
        	}
        };
        buttonLeave.setOnClickListener(buttonLeaveListener);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	//this allows to don't show alertdialog when we come back to this activity
    	if(ad != null && loader != null){
            ad.dismiss();
            loader.dismiss();
         } 	
    }
}
