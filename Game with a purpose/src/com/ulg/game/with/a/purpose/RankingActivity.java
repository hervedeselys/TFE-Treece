package com.ulg.game.with.a.purpose;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
/**
 * This class deals with the ranking activity. It show the top 10
 * of best players.
 * 
 * @author Hervé de Sélys
 *
 */
public class RankingActivity extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        
        Intent intent = getIntent();
        
        Ranking r = new Ranking(this);

        //if we want to enter a new score int the ranking
        if (intent.getStringExtra("PlayerName") != null){
        	String newPlayerName = intent.getStringExtra("PlayerName");
    		String newPlayerScore = intent.getStringExtra("PlayerScore");
    		int newPlayerLevel = intent.getIntExtra("Level", 0);
    		
        	r.writeRanking(newPlayerName, newPlayerScore, newPlayerLevel);
        }
        //We show the ranking list
        List<Rank> rankingList = r.readRanking();
        displayRanking(rankingList);
	}
	
	/**
	 * Display the ranking list on the screen
	 * 
	 * @param list
	 */
	private void displayRanking(List<Rank> list) {
		
		for(int i = 1; i <= list.size() && i <= 10; i++ ){
			
			Rank r = list.get(i-1);
			
			if (r == null)
				return;
			
			String playerName = r.getName();
			int playerScore = r.getScore();
			int playerLevel = r.getLevel();
			
			String idPlayer = "rank_"+i+"_player";
			String idScore = "rank_"+i+"_score";
			String idLevel = "rank_"+i+"_level";
			int resIdPlayer = getResources().getIdentifier(idPlayer, "id", getPackageName());
			int resIdScore = getResources().getIdentifier(idScore, "id", getPackageName());
			int resIdLevel = getResources().getIdentifier(idLevel, "id", getPackageName());

			TextView playerNameText = (TextView) findViewById(resIdPlayer);
			TextView scoreRank = (TextView) findViewById(resIdScore);
			TextView levelRank = (TextView) findViewById(resIdLevel);
	    
			playerNameText.setText(playerName);
			scoreRank.setText(String.valueOf(playerScore));
			levelRank.setText(String.valueOf(playerLevel));
			
			scoreRank.setGravity(Gravity.CENTER_HORIZONTAL);
			levelRank.setGravity(Gravity.CENTER_HORIZONTAL);
		}		
	}
}
