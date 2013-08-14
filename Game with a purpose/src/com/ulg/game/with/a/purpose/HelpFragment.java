package com.ulg.game.with.a.purpose;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpFragment extends Fragment {
	
	private int pageNumber;
	private String mode;
	
	public HelpFragment() {}
	
	public HelpFragment create(int pn, String m) {
		
		HelpFragment fragment = new HelpFragment();
		Bundle args = new Bundle();
		args.putInt("page_number", pn);
		args.putString("mode", m);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                    Bundle savedInstanceState) {
		
		pageNumber = getArguments().getInt("page_number");
		mode = getArguments().getString("mode");
		
		switch(pageNumber){
			case 0:
				if(mode.equals("game"))
					return inflater.inflate(R.layout.help_game_fragment_0, container, false);
				else
					return inflater.inflate(R.layout.help_learning_fragment_0, container, false);
			case 1:
				if(mode.equals("game"))
					return inflater.inflate(R.layout.help_game_fragment_1, container, false);
				else
					return inflater.inflate(R.layout.help_learning_fragment_1, container, false);
			case 2:
				if(mode.equals("game"))
					return inflater.inflate(R.layout.help_game_fragment_2, container, false);
				else
					return inflater.inflate(R.layout.help_learning_fragment_2, container, false);
			case 3:
				if(mode.equals("game"))
					return inflater.inflate(R.layout.help_game_fragment_3, container, false);
				else
					return inflater.inflate(R.layout.help_learning_fragment_3, container, false);
			case 4:
				return inflater.inflate(R.layout.help_learning_fragment_4, container, false);
			case 5:
				return inflater.inflate(R.layout.help_learning_fragment_5, container, false);
		}
		return null;
    }
}