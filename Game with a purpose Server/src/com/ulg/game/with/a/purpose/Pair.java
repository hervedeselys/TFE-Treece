package com.ulg.game.with.a.purpose;

import java.io.Serializable;

/**
 * This class implements the representation of a pair.
 * This is a structure that contains two integers.
 * 
 * @author Hervé de Sélys
 *
 */

@SuppressWarnings("serial")
class Pair implements Serializable{
	private int firstCol;
	private int secondCol;
	
	public Pair(int first, int second){
		firstCol = first;
		secondCol = second;
	}
	
	/**
	 * Returns the first element of the pair.
	 * 
	 * @return firstCol
	 */
	public int getFirst(){
		return firstCol;
	}
	
	/**
	 * Returns the second element of the pair.
	 * 
	 * @return secondCol
	 */
	public int getSecond(){
		return secondCol;
	}
}