package com.ulg.game.with.a.purpose;

import java.io.Serializable;


@SuppressWarnings("serial")
class Point implements Serializable{
	
	private float xData;
	private float yData;
	//Used to attribute a color in function of its output
	private int outputId;
	
	public Point(int id, float x, float y){
		outputId = id;
		xData = x;
		yData = y;
	}
	
	public float getxData(){
		return xData;
	}
	
	public float getyData(){
		return yData;
	}
	
	public int getOutputId(){
		return outputId;
	}
	
	public void setOutuputId(int i){
		outputId = i;
	}
}