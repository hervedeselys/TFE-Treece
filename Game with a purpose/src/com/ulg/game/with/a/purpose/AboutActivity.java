package com.ulg.game.with.a.purpose;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AboutActivity extends Activity{

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		Button buttonBack = (Button) findViewById(R.id.back);
        OnClickListener buttonBackListener = new OnClickListener(){
        	public void onClick(View actualView){
        		finish();
        	}
        };
        buttonBack.setOnClickListener(buttonBackListener);
	}
}
