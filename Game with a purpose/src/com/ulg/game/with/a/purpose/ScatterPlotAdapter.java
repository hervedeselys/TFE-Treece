package com.ulg.game.with.a.purpose;

import java.util.ArrayList;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 
 * 	@author Hervé de Sélys
 *
 */
public class ScatterPlotAdapter extends BaseAdapter {
	
	private Context context;
	private List<Bitmap> thumbs;
	private List<ImageView> imageViewList;

	// Constructor
	public ScatterPlotAdapter(Context c, List<Bitmap> bitmapList){
        context = c;
        thumbs = bitmapList;
        imageViewList = new ArrayList<ImageView>();
    }
	
	public void setBitmapList(List<Bitmap> bitmapList){		
		if(!thumbs.isEmpty())		
			thumbs.clear();
		if(!imageViewList.isEmpty())
			imageViewList.clear();
		thumbs = bitmapList;
	}
	
	public int getCount() {
		return thumbs.size();
	}

	public Bitmap getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		
        if (convertView == null)     	
            imageView = new ImageView(context);       
        else
            imageView = (ImageView) convertView;
      
        imageView.setImageBitmap(thumbs.get(position));
        imageViewList.add(imageView);
           
        return imageView;
	}
}
