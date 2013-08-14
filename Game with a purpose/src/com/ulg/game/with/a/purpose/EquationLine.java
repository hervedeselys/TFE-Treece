package com.ulg.game.with.a.purpose;

import java.io.Serializable;


@SuppressWarnings("serial")
public class EquationLine implements Serializable{
	
	private int treeID;
	private int nodeID;
	private int parentID;
	private int leftChildID;
	private int rightChildID;
	private float slope;
	private float yIntercept;
	private Point firstPoint, secondPoint;
	private String database;
	private Pair attributePair;
	private boolean isVertical = false;
	private int majorityClassID;
	private boolean googlePlayStore = true;
	
	public EquationLine() {}
	
	public EquationLine(Point a, Point b, Pair p){
		firstPoint = a;
		secondPoint = b;
		attributePair = p;
		float aX = firstPoint.getxData();
		float bX = secondPoint.getxData();
		float test = Math.abs(aX-bX);
		//Precision test in order to define vertical line.
		//Maybe test real point instead of raw point
		if (test < 0.01){
			isVertical = true;
			return;
		}
		
		slope = (secondPoint.getyData() - firstPoint.getyData())
				/ (secondPoint.getxData() - firstPoint.getxData());
		yIntercept = firstPoint.getyData() - (slope*firstPoint.getxData());	
		
	}
	
	public float getSlope(){
		return slope;
	}
	
	public float getYIntercept(){
		return yIntercept;
	}
	
	public Point getFirstPoint(){
		return firstPoint;
	}
	
	public Point getSecondPoint(){
		return secondPoint;
	}
	
	public String getDatabase(){
		return database;
	}
	
	public void setDatabase(String d){
		database = d;
	}
	
	public Pair getAttributePair(){
		return attributePair;
	}
	
	public boolean isVertical(){
		return isVertical;
	}

	public void setTreeID(int id) {
		treeID = id;	
	}
	
	public int getTreeID(){
		return treeID;
	}
	
	public void setNodeID(int id) {
		nodeID = id;	
	}
	
	public int getNodeID(){
		return nodeID;
	}
	
	public void setParentID(int id) {
		parentID = id;	
	}
	
	public int getParentID(){
		return parentID;
	}
	
	public void setChildrenID(int l, int r) {
		leftChildID = l;
		rightChildID = r;
	}
	
	public int getRightChildID(){
		return rightChildID;
	}
	
	public int getLeftChildID(){
		return leftChildID;
	}
	
	public void setSlope(float s){
		slope = s;
	}
	
	public void setYIntercept(float p){
		yIntercept = p;
	}
	
	public void setPairAttribute(Pair p){
		attributePair = p;
	}
	
	public int getMajorityClassID(){
		return majorityClassID;
	}
	
	public void setMajorityClassID(int m){
		majorityClassID = m;
	}
	
	public boolean isGooglePlayStore(){
		return googlePlayStore;
	}
	
}
