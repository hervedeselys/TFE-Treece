package com.ulg.game.with.a.purpose;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class ThumbGenerator {
	
	private int width = 200, height = 200, stroke = 4;
	
	public ThumbGenerator() { }

	//http://www.java2s.com/Code/Java/2D-Graphics-GUI/DrawanImageandsavetopng.htm
	public List<Bitmap> generateThumbs(List<ScatterPlot> scatterList) {
				
		List<Bitmap> bitmapList = new ArrayList<Bitmap>();		
		Paint paint = new Paint();
		paint.setStrokeWidth(stroke);
		Paint text = new Paint();
		text.setTextSize(22);
		text.setColor(Color.WHITE);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inInputShareable = true;
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	
	    for(int k=0; k<scatterList.size(); k++){	    	
			Canvas canvas = new Canvas(bitmap);
	    	canvas.drawColor(Color.BLACK);
	    	ScatterPlot sc = scatterList.get(k);
	    	Pair pair = sc.getPairAttribute();
		    List<Instance> set = sc.getInstanceSet();
		    float minX = sc.getMinX();
		    float maxX = sc.getMaxX();
		    float minY = sc.getMinY();
		    float maxY = sc.getMaxY();
		    float xDiff = maxX-minX;
			float yDiff = maxY-minY;
			float hMargin = 0;
			float vMargin = 0;
			
			if (xDiff == 0){
				xDiff = (float) 1;
				hMargin = width/2;
			}
			if (yDiff == 0){
				yDiff = (float) 1;
				vMargin = height/2;
			}
			
			float xScale = (width-stroke)/xDiff;
			float yScale = (height-stroke)/yDiff;
		    for(int i=0; i<set.size(); i++){
		    	//Allow just 200 point to avoid memory issue and keep readability
		    	if (i == 200)
		    		break;
		    	Instance inst = set.get(i);
		    	List<Attribute> record = inst.getAttributeList();
		    	float x = record.get(pair.getFirst()).getValue();
				float y = record.get(pair.getSecond()).getValue();
				
		    	float xCoord;
		    	float yCoord; 
		    	
		    	if(set.size() > 1){
		    		xCoord = ((x-minX)*xScale)+(stroke/2)+hMargin;
			    	yCoord = ((y-minY)*yScale)+(stroke/2)+vMargin; 
		    	}
		    	//Only one point to draw, draw it on the middle of the thumbs
		    	else{
		    		xCoord = width/2;
			    	yCoord = height/2; 
		    	}
		    		
		    	int outputId = inst.getOutputId();
				
				if (outputId == 0)
					paint.setColor(Color.BLUE);
				else if (outputId == 1)
					paint.setColor(Color.MAGENTA);
				else if (outputId == 2)
					paint.setColor(Color.CYAN);
				else if(outputId == 3)
					paint.setColor(Color.GREEN);
				else if(outputId == 4)
					paint.setColor(Color.RED);
				else if(outputId == 5)
					paint.setColor(Color.GRAY);
				else if(outputId == 6)
					paint.setColor(Color.WHITE);
				else if(outputId == 7)
					paint.setColor(Color.YELLOW);
				else if(outputId == 8)
					paint.setColor(Color.LTGRAY);
				else if(outputId == 9)
					paint.setColor(Color.DKGRAY);
				
		    	canvas.drawPoint(xCoord, yCoord, paint);
		    }
		    canvas.drawText(String.valueOf(pair.getFirst()), width/2, height, text);
		    canvas.drawText(String.valueOf(pair.getSecond()), 0, height/2, text);
		    ByteArrayOutputStream stream = new ByteArrayOutputStream();
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		    byte[] imageInByte = stream.toByteArray();
		    //Copy in order to use BitmapFactory.Options
		    Bitmap copy = BitmapFactory.decodeByteArray(imageInByte, 0,
		    							imageInByte.length, options);	    
		    bitmapList.add(copy);
		    try {
				stream.close();
			} catch (IOException e) {
				System.err.println("I/O exception");
				System.err.println(e.getMessage());
			}
	    }
	    bitmap.recycle();
		return bitmapList;   
	}
}
