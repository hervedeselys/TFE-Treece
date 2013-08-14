package com.ulg.game.with.a.purpose;

import com.ulg.game.with.a.purpose.R.id;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class DecisionTreeViewerActivity extends Activity {
	
	private int level;
	private LightNode root;
	private ExpandableListView tree;
	private int parentPosition;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decisiontreeviewer);
        Intent intent = getIntent();
        parentPosition = intent.getIntExtra("ParentPosition", 0);     
		root = (LightNode) intent.getSerializableExtra("Node");		
        level = (int) intent.getIntExtra("Level", 0);
        TextView navigationBar = (TextView) findViewById(id.navigation_bar);
        Resources r = getResources();
        if (level == 0)
        	navigationBar.setText(r.getString(R.string.root_level));
        else{
        	CharSequence nav = r.getString(R.string.root_level);
        	for(int i = 1; i < level+1;i++)
        		nav = nav +" > "+r.getString(R.string.level)+" "+ i;
        	navigationBar.setText(nav);
        }
        tree = (ExpandableListView) findViewById(id.tree);
        NodeAdapter nodeAdapter = new NodeAdapter(DecisionTreeViewerActivity.this,
        							root, level, parentPosition, r);
        tree.setAdapter(nodeAdapter);
        tree.expandGroup(0);
	}

}
