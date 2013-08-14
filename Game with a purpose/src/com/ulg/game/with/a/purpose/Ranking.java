package com.ulg.game.with.a.purpose;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * This class establish the ranking list. It creates the file if it doesn't
 * exist or update the actual ranking.dat.
 * 
 * @author Hervé de Sélys
 *
 */
public class Ranking {
	
	private String filename = "ranking.dat";
	
	private Context context;
	
	Ranking(Context c){
		context = c;
	}
		
	/**
	 * Method used to write the new score in the stored ranking.dat file
	 * @param newPlayerLevel 
	 * 
	 * @param intent 
	 */
	public void writeRanking(String newPlayerName, String newPlayerScore, int newPlayerLevel){
		
		List<Rank> list = readRanking();
		
		//Ajouter le nouveau score dans la liste
		List<Rank> rankingList = addNewScore(list, newPlayerName, Integer.valueOf(newPlayerScore), newPlayerLevel);
		
		FileOutputStream outputFile = null; 
        OutputStreamWriter osw = null;
        
        try {
			outputFile = context.openFileOutput(filename, 0);
			osw = new OutputStreamWriter(outputFile); 
			//We add each rank in the file
			for (int i=0; i < rankingList.size(); i++){
				
				Rank r = rankingList.get(i);
				
				int rank = r.getRank();
				String name = r.getName();
				int score = r.getScore();
				int level = r.getLevel();
				
				osw.write(rank + ","); 
		        osw.write(name + ","); 
		        osw.write(score + ",");
		        osw.write(level + "\n");
			}
			 
	        osw.flush(); 
	        osw.close();
	        outputFile.close();
	        
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O error");
			System.err.println(e.getMessage());
		}
			
	}

	/**
	 * Method used to read the current ranking file
	 * 
	 */
	public List<Rank> readRanking(){
		
		FileInputStream inputFile = null; 
        InputStreamReader isr = null; 
		
        //This way of doing allows to separate implementation and representation
        //(make easier to change the arraylist in linkedlist later for example)
    	List<Rank> rankingList = new ArrayList<Rank>();
    	
    	try {
    		//TODO: raise an error when the file doesn't exist (always when you install the app for the first time)
			inputFile = context.openFileInput(filename);
			isr = new InputStreamReader(inputFile);
			BufferedReader br = new BufferedReader(isr);
			
			//We read each line of the file
			for (String line = br.readLine(); line != null; line = br.readLine()){
				//We split each line based on the coma
				String[] oneRanking = line.split(",");
				
				//We retrieve each value separately
				int rank = Integer.valueOf(oneRanking[0]);
				String name = oneRanking[1];
				int score = Integer.valueOf(oneRanking[2]);
				int level = Integer.valueOf(oneRanking[3]);
				
				//We add the rank in the list
				Rank r = new Rank(rank, name, score, level);
				rankingList.add(r);
			}
			
		} catch (FileNotFoundException e) {
			System.err.println("File not found");
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println("I/O error");
			System.err.println(e.getMessage());
		}
    	return rankingList;
	}
	
	/**
	 * This method add the new score in the list. It has to compare the new
	 * score with the others to know where to put it. When it's done, it must
	 * shift the inferior score at the bottom.
	 * 
	 * @param newPlayerScore 
	 * @param newPlayerName 
	 * @param list 
	 * @return 
	 * 
	 */
	private List<Rank> addNewScore(List<Rank> list, String newPlayerName, int newPlayerScore, int newPlayerLevel) {
		List<Rank> newRanking = new ArrayList<Rank>();
		int rank;
		
		//If there is no player in the list
		if (list.size() == 0){
			Rank r = new Rank(1, newPlayerName, newPlayerScore, newPlayerLevel);
			list.add(r);
			return list;
		}
		
		//We remove the last element if there are 10 elements in the list
		if(list.size() == 10)
			list.remove(list.size()-1);

		//We rewrite the list in the new list until the new element to insert
		for (rank = 0; rank < list.size() && rank < 10; rank++){
			if (newPlayerScore < list.get(rank).getScore())
				newRanking.add(list.get(rank));
			else
				break;
		}
		//We create and insert the new element
		Rank r = new Rank(rank, newPlayerName, newPlayerScore, newPlayerLevel);
		newRanking.add(r);
		
		//We have to rewrite the rest of the old list minus the last
		for(int i=rank; i < list.size() && i < 10; i++){
			newRanking.add(list.get(i));
		}
		
		return newRanking;
		
	}
	
	public int getLastRankingScore(){
		List<Rank> r = readRanking();
		int size = r.size();
		Rank rank = r.get(size-1);
		return rank.getScore();
	}
}


/**
 * This class represents a row in the ranking. It contains the rank, the name
 * and the score of the player at this rank. Rank is between 1 and 10.
 * 
 * @author Hervé de Sélys
 *
 */

class Rank {
	private int rank;
	private String name;
	private int score;
	private int level;
	
	public Rank(int r, String n, int s, int l){
		rank = r;
		name = n;
		score = s;
		level = l;
	}
	
	/**
	 * Returns the rank of the player
	 * 
	 * @return rank
	 */
	public int getRank(){
		return rank;
	}
	
	/**
	 * Returns the name of the player
	 * 
	 * @return name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Returns the score of the player
	 * 
	 * @return score
	 */
	public int getScore(){
		return score;
	}
	
	/**
	 * Returns the level reached by the player
	 * 
	 * @return level
	 */
	public int getLevel(){
		return level;
	}
}
