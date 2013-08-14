package com.ulg.game.with.a.purpose;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * This class represents a view of a scatter plot.
 * It manages the drawing on the screen.
 * 
 * @author Hervé de Sélys
 *
 */

public class ScatterPlotView extends SurfaceView implements Callback {

	//Our variables
	private Paint line;
	private Paint points;

	private List<Instance> instanceList;
	private List<Point> scaledPoint;
	private Pair pair;

	private Canvas canvas;
	private SurfaceHolder sh;
	private boolean gameOver;
	
	private float yStart;
	private float xStart;
	private float xEnd;
	private float yEnd;
	private float xScale;
	private float yScale;

    private boolean noLineTraced = true;
    private boolean color = true; 
    private boolean firstDrawing = true;
    
    private float hMargin;
    private float vMargin;
    
    private boolean straightLine = false;
    
    private float minX;
    private float maxX; 
    private float minY;
    private float maxY; 
        
	
	public ScatterPlotView(Context context, AttributeSet attrs ) {	
		super(context, attrs);
		getHolder().addCallback(this);
		sh = getHolder();
		this.setDrawingCacheEnabled(true);
		canvas = null;
		setFocusable(true);
		
		//Point
		points = new Paint();
		points.setStrokeWidth(6);
		
		//Line
		line = new Paint();
		line.setStyle(Paint.Style.STROKE);
		line.setStrokeWidth(4);
		line.setColor(Color.WHITE);
		
		gameOver = false;
		
		Resources res = getResources();
		hMargin = res.getDimension(R.dimen.horizontal_margin);
		vMargin = res.getDimension(R.dimen.vertical_margin);
		
		scaledPoint = new ArrayList<Point>();
	}

	/**
	 * Draw the view
	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.BLACK);
		
		if (firstDrawing){
			//Mandatory to do that here in order to be able 
			//to get height and width available in the onDraw function
			scaledPoint = computeScaledPoint(getHeight(), getWidth());
			firstDrawing = false;
		}
				
		//Drawing points
		for(int i=0; i< scaledPoint.size(); i++){
			Point p = scaledPoint.get(i);
			float pointCoordinateX = p.getxData();
			float pointCoordinateY = p.getyData();
			int id = p.getOutputId();
			if (color){
				if (id == 0)
					points.setColor(Color.BLUE);
				else if (id == 1)
					points.setColor(Color.MAGENTA);
				else if (id == 2)
					points.setColor(Color.CYAN);
				else if(id == 3)
					points.setColor(Color.GREEN);
				else if(id == 4)
					points.setColor(Color.RED);
				else if(id == 5)
					points.setColor(Color.GRAY);
				else if(id == 6)
					points.setColor(Color.WHITE);
				else if(id == 7)
					points.setColor(Color.YELLOW);
				else if(id == 8)
					points.setColor(Color.LTGRAY);
				else if(id == 9)
					points.setColor(Color.DKGRAY);
			}
			else
				points.setColor(Color.WHITE);
					
			canvas.drawPoint(pointCoordinateX, pointCoordinateY, points);
		}
		
		//Drawing trace
		if(!straightLine)
			canvas.drawLine(xStart, yStart, xEnd, yEnd, line);
		else{
			//Vertical line
			if(yStart >= yEnd+50 || yStart < yEnd-50)
				xEnd = (float) (xStart);
			//Horizontal line
			else if(xStart >= xEnd+50 || xStart < xEnd-50)
				yEnd = yStart;			
			canvas.drawLine(xStart, yStart, xEnd, yEnd, line);
		}
			if (xStart != xEnd || yStart != yEnd)
				noLineTraced = false;
			else
				noLineTraced = true;
    }

	/**
	 * Manage the gesture actions of the user
	 */
	public boolean onTouchEvent(MotionEvent event) {
		
		if (gameOver)
			return true;
				
		int pointCnt = event.getPointerCount();
		//If we deal with one finger
		if (pointCnt == 1){
			//Retrieve coordinates information when we touch the screen
			if (event.getAction() == MotionEvent.ACTION_DOWN){
				xStart = event.getX();
				yStart = event.getY();
				xEnd = event.getX();
				yEnd = event.getY();
			}
				
			//Retrieve coordinates information when we move our finger on the screen
			if (event.getAction() == MotionEvent.ACTION_MOVE){
				xEnd = event.getX();
				yEnd = event.getY();
			}
		}
	    
		refreshView();

		return true;
    }
	
	/**
	 * Redraw the view
	 * @param drawLine tells if we have to redraw trace made by user
	 */
	@SuppressLint("WrongCall")
	public void refreshView(boolean drawLine){
		canvas = null;
		
		if (drawLine == false){
			xStart = 0;
			yStart = 0;
			xEnd = 0;
			yEnd = 0;
			//twoFingers = false;
		}
		try{
			//We unlock the canvas in order to draw on it
			canvas = sh.lockCanvas();
			if (canvas != null)
				onDraw(canvas);
		}
		finally{
			//We apply the drawing on the canvas and we release it.
			if (canvas != null)
				sh.unlockCanvasAndPost(canvas);
		}
	}
	
	/**
	 * Redraw the view
	 */
	public void refreshView(){
		refreshView(true);
	}

	/**
	 * Action to do when the view is modified
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) { }

	/**
	 * Action to do when we create the view
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		//We call the onDraw method in order to draw the grid in the first place 
		refreshView();				
	}

	
	/**
	 * Actions to do when we leave the view
	 */
	public void surfaceDestroyed(SurfaceHolder holder) { }
	
	/**
	 * Load the scatter plot into the view.
	 * 
	 * @param sp, the scatter plot to load
	 * @param learning, boolean to deactivate penalty & level indications
	 */
	public void loadScatterPlot(ScatterPlot sp){	
		instanceList = sp.getInstanceSet();
		pair = sp.getPairAttribute();
		minX = sp.getMinX();
		maxX = sp.getMaxX();
		minY = sp.getMinY();
		maxY = sp.getMaxY();
		firstDrawing = true;
	}
	
	
	public void setGameOver(){
		gameOver = true;
	}
	
	public boolean isGameOver(){
		return gameOver;
	}
	
	public void setStraightMode(boolean s){
		straightLine = s;
	}
	
	/**
	 * Compute the distance between the line made by the user and
	 * each point of the database and change the color of the line if
	 * this distance is less than a threshold.
	 */
//	private void computeDistance(){
//		
//		final int THRESHOLD = 5;
//		
//		//Variable for the equation of the line
//		float u = yEnd - yStart;
//		float v = xStart - xEnd;
//		float h = (yStart*xEnd) - (xStart*yEnd);
//	
//		for(int i=0; i< scaledPoint.size(); i++){
//			Point p = scaledPoint.get(i);
//			float pointCoordinateX = p.getxData();
//			float pointCoordinateY = p.getyData();
//
//			//Computation of the distance between a line and a point
//			float distance = (float) (Math.abs(u*pointCoordinateX + v*pointCoordinateY + h)/ Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2) ));
//		
//			if (distance < THRESHOLD){
//				line.setColor(Color.RED);
//				penalityText.setColor(Color.RED);
//				penality = true;
//				isTouch = true;
//			}
//			else
//				isTouch = false;
//		}	
//	}
	
	/**
	 * Function used to re-scale the value of attribute in order
	 * that points fit between max and min for each axis.
	 * 
	 * @param viewHeight
	 * @param viewWidth
	 * @return
	 */
	private List<Point> computeScaledPoint(float viewHeight, float viewWidth){
		List<Point> list = new ArrayList<Point>();
		Float left = hMargin;
		Float top = vMargin;
		Float right = viewWidth-hMargin;
		Float bottom = viewHeight-vMargin;
		Float xDiff = maxX-minX;
		Float yDiff = maxY-minY;
		
		//Where left points are aligned, they put on the center of the screen
		if (xDiff == 0){
			xDiff = (float) 1;
			hMargin = viewWidth/2;
		}
		if (yDiff == 0){
			yDiff = (float) 1;
			vMargin = viewHeight/2;
		}
		
		xScale = (right-left)/xDiff;
		yScale = (bottom-top)/yDiff;
		
		System.out.println("xScale: "+xScale+" yScale: "+yScale);
		
		if(instanceList.size() > 1){
			for(int i=0; i< instanceList.size(); i++){
				Instance inst = instanceList.get(i);
		    	List<Attribute> record = inst.getAttributeList();
		    	float x = record.get(pair.getFirst()).getValue();
				float y = record.get(pair.getSecond()).getValue();
				float pointCoordinateX = ((x-minX)*xScale)+hMargin;
				float pointCoordinateY = ((y-minY)*yScale)+vMargin;
				
				Point pScaled = new Point(inst.getOutputId(),
										pointCoordinateX, pointCoordinateY);								  
				list.add(pScaled);
			}
		}
		//if there is one point to draw, draw it on the middle of the screen
		else{
			Instance inst = instanceList.get(0);
			
			float pointCoordinateX = viewWidth/2;
			float pointCoordinateY = viewHeight/2;
			
			Point pScaled = new Point(inst.getOutputId(),
					pointCoordinateX, pointCoordinateY);								  
			list.add(pScaled);
		}
		
		return list;
	}
	
	/**
	 * Allows to retrieve the original value of a scaled point. 
	 *
	 * @param scaledPoint
	 * @return
	 */
	public Point getRawPoint(Point scaledPoint){
		
		float rawPointCoordinateX = ((scaledPoint.getxData()-hMargin)/xScale)+minX;
		float rawPointCoordinateY = ((scaledPoint.getyData()-vMargin)/yScale)+minY;
		
		Point p = new Point(scaledPoint.getOutputId(),
				rawPointCoordinateX, rawPointCoordinateY);
		return p;
	}
	
	/**
	 * Method used to know if there is a line traced or not. 
	 * It's useful to don't show any penalty when we don't have
	 * touch the screen yet.
	 * 
	 * @return true, if there is no line traced.
	 */
	public boolean isNoLineTraced(){
		return noLineTraced;
			
	}
	
	public Point getStartPoint(){
		Point p = new Point(0, xStart, yStart);
		return p;
	}
	
	public Point getEndoint(){
		Point p = new Point(0, xEnd, yEnd);
		return p;
	}
	
	public List<Point> getScaledPoint(){
		return scaledPoint;
	}
}


