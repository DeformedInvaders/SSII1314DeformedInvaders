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
	private boolean[] estadoNiveles;
	
	/* SECTION Constructora */
	
	public static final LevelSelectionFragment newInstance(boolean[] niveles)
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		fragment.setParameters(niveles);
		return fragment;
	}
	
	private void setParameters(boolean[] niveles)
	{
		estadoNiveles = niveles;
	}
	
	/* SECTION Métodos Fragment */
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_game_level_selection_layout, container, false);

		viewPager = (SwipeableViewPager) rootView.findViewById(R.id.pagerViewLevelSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		//FIXME
		viewPager.addView(LevelSelectFragment.newInstance(estadoNiveles[0], TLevelTipo.Moon), getString(R.string.title_level_section_moon));
		viewPager.addView(LevelSelectFragment.newInstance(estadoNiveles[1], TLevelTipo.NewYork), getString(R.string.title_level_section_newyork));
		viewPager.addView(LevelSelectFragment.newInstance(estadoNiveles[2], TLevelTipo.Rome), getString(R.string.title_level_section_rome));
		viewPager.addView(LevelSelectFragment.newInstance(estadoNiveles[3], TLevelTipo.Egypt), getString(R.string.title_level_section_egypt));
		viewPager.addView(LevelSelectFragment.newInstance(estadoNiveles[4], TLevelTipo.Stonehenge), getString(R.string.title_level_section_stonehenge));
		
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
