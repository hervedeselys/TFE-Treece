package com.ulg.game.with.a.purpose;

import java.io.Serializable;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;

public class NodeAdapter extends BaseExpandableListAdapter{
	
	private Context context;
	private LayoutInflater inflater;
	private LightNode node;
	private int level;
	private int parentPosition;
	private Resources res;
	
	public NodeAdapter(Context c, LightNode n, int l, int p, Resources r){
		context = c;
		inflater = LayoutInflater.from(context);
		node = n;
		level = l;
		parentPosition = p;
		res = r;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return node.getChildren().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder;
		final LightNode childNode = (LightNode) getChild(groupPosition, childPosition);
		final int position = childPosition;

        if (convertView == null) {
            holder = new ViewHolder();   
            convertView = inflater.inflate(R.layout.child_button, null);
            holder.button = (Button) convertView.findViewById(R.id.child_button);
            convertView.setTag(holder);
        } else {     	
            holder = (ViewHolder) convertView.getTag();
        }
        if(childPosition == 0)
        	holder.button.setText(res.getString(R.string.left_child));
        else if(childPosition == 1)
        	holder.button.setText(res.getString(R.string.right_child));
        
        if (childNode.isCurrentNode()){
        	CharSequence string = holder.button.getText();
        	holder.button.setText(string+" "+res.getString(R.string.current_node));
        }
        
        if (childNode.isCurrentBranch()){
        	CharSequence string = holder.button.getText();
        	holder.button.setText(string+" "+res.getString(R.string.current_branch));
        }
        convertView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, DecisionTreeViewerActivity.class);
				intent.putExtra("Node", (Serializable)childNode);
				intent.putExtra("ParentPosition", position);
				int nextLevel = level+1;
				intent.putExtra("Level", nextLevel);
				context.startActivity(intent);
			}
		});
        convertView.setEnabled(true);
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return node.getChildren().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return node;
	}

	@Override
	public int getGroupCount() {
		return 1;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder;
		LightNode parentNode = (LightNode) getGroup(groupPosition);

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.group_button, null);
            holder.button = (Button) convertView.findViewById(R.id.group_button);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if(parentNode.isRoot())
        	holder.button.setText(res.getString(R.string.root)+" ");
        else
        	holder.button.setText(res.getString(R.string.node)+" "+parentNode.getNodeID());
        
        if (parentNode.isCurrentNode()){
        	CharSequence string = holder.button.getText();
        	holder.button.setText(string+" "+res.getString(R.string.current_node));
        }
        
        convertView.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if (level != 0){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(res.getString(R.string.node_threshold));
					EquationLine threshold = node.getThreshold();
					float p = threshold.getYIntercept();
					float slope = threshold.getSlope();
					
					//Rounding
					int tmp = (int) (slope*100);
					slope = (float)tmp/100;
					tmp = (int)(p*100);
					p = (float)tmp/100;
					
					CharSequence charSeq;
					if (threshold.isVertical()){
						float x = threshold.getFirstPoint().getxData();
						//Rounding
						tmp = (int) (x*100);
						x = (float)tmp/100;
						if (parentPosition == 0)
							charSeq = "Equation: x > "+x;
						else
							charSeq = "Equation: x =< "+x;
					}
					else{
						if (parentPosition == 0)
							if (slope !=0){
								charSeq = "Equation: y > "+slope+"x";
								if (p > 0)
									charSeq = charSeq + "+"+ p;
								else
									charSeq = charSeq +""+ p;
							}
							else
								charSeq = "Equation: y > "+p;
						else{
							if(slope !=0){
								charSeq = "Equation: y =< "+slope+"x";
								if (p > 0)
									charSeq = charSeq + "+"+ p;
								else
									charSeq = charSeq +""+ p;
							}
							else
								charSeq = "Equation: y =< "+p;		
						}
					}
					
					builder.setMessage(charSeq);
					builder.setNeutralButton(res.getString(R.string.dialog_ok), null);
					AlertDialog ad = builder.create();
					ad.show();
				}			
			}
		});

        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	class ViewHolder{
		public Button button;
	}

}
