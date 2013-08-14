package com.ulg.game.with.a.purpose;

import com.ulg.game.with.a.purpose.R.id;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_menu);
        
        Button helpGameButton = (Button) findViewById(id.help_game);
        OnClickListener helpGameButtonListener = new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), HelpGameActivity.class);
				startActivity(intent);
			}
		};
		helpGameButton.setOnClickListener(helpGameButtonListener);
        
        Button helpLearningButton = (Button) findViewById(id.help_learning);
        OnClickListener helpLearningButtonListener = new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), HelpLearningActivity.class);
				startActivity(intent);
			}
		};
		helpLearningButton.setOnClickListener(helpLearningButtonListener);
		
		Button buttonBack = (Button) findViewById(R.id.back);
        OnClickListener buttonBackListener = new OnClickListener(){
        	public void onClick(View actualView){
        		finish();
        	}
        };
        buttonBack.setOnClickListener(buttonBackListener);
	}

}
