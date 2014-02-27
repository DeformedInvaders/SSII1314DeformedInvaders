package com.game.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.SwipeableViewPager;
import com.android.view.ViewPagerFragment;
import com.project.main.R;

public class LevelSelectionFragment extends ViewPagerFragment
{	
	/* SECTION Constructora */
	
	public static final LevelSelectionFragment newInstance()
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		return fragment;
	}
	
	/* SECTION Métodos Fragment */
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_game_level_selection_layout, container, false);

		viewPager = (SwipeableViewPager) rootView.findViewById(R.id.pagerViewLevelSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		viewPager.addView(LevelSelectFragment.newInstance(R.drawable.background_moon, R.string.text_level_section_moon), getString(R.string.title_level_section_moon));
		viewPager.addView(LevelSelectFragment.newInstance(R.drawable.background_stonehenge, R.string.text_level_section_stonehenge), getString(R.string.title_level_section_stonehenge));
		viewPager.addView(LevelSelectFragment.newInstance(R.drawable.background_egypt, R.string.text_level_section_egypt), getString(R.string.title_level_section_egypt));
		viewPager.addView(LevelSelectFragment.newInstance(R.drawable.background_rome, R.string.text_level_section_rome), getString(R.string.title_level_section_rome));
		viewPager.addView(LevelSelectFragment.newInstance(R.drawable.background_newyork, R.string.text_level_section_newyork), getString(R.string.title_level_section_newyork));
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		viewPager = null;
	}
    
    /* SECTION Métodos abstractos de ViewPagerFragment */
    
    @Override
    public void onPageSelected() { }
}
