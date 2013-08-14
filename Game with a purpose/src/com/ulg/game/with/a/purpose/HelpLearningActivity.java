package com.ulg.game.with.a.purpose;


import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class HelpLearningActivity extends FragmentActivity {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager pager;
    private String mode = "learning";

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        
        List<HelpFragment> fragments = new ArrayList<HelpFragment>();
        
        fragments.add(new HelpFragment().create(0, mode));
        fragments.add(new HelpFragment().create(1, mode));
        fragments.add(new HelpFragment().create(2, mode));
        fragments.add(new HelpFragment().create(3, mode));
        fragments.add(new HelpFragment().create(4, mode));
        fragments.add(new HelpFragment().create(5, mode));

        // Instantiate a ViewPager and a PagerAdapter.
        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new HowToPlayFragmentAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pagerAdapter);
    }
    
    public void backToMenu(View view){
    	finish();
    }
    
    private class HowToPlayFragmentAdapter extends FragmentStatePagerAdapter {
    	
    	private List<HelpFragment> fragments;
       
       public HowToPlayFragmentAdapter(FragmentManager fm, List<HelpFragment> list) {
           super(fm);
           fragments = list;
       }

       @Override
       public HelpFragment getItem(int position) {
           return fragments.get(position);
       }

       @Override
       public int getCount() {
           return fragments.size();
       }
   }
}