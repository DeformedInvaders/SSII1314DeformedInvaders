package com.game.select;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.view.ViewPagerSwipeable;
import com.android.view.ViewPagerFragment;
import com.project.main.R;

public class LevelSelectionFragment extends ViewPagerFragment
{	
	private LevelSelectionFragmentListener mCallback;
	
	private ImageButton botonNivel;
	
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
	
	public interface LevelSelectionFragmentListener
	{
        public void onLevelSelectionSelectClicked();
    }
	
	/* SECTION Métodos Fragment */
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (LevelSelectionFragmentListener) activity;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
		botonNivel = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_game_level_selection_layout, container, false);

		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewLevelSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		//FIXME
		viewPager.addView(LevelSelectFragment.newInstance(TLevelTipo.Moon), getString(R.string.title_level_section_moon));
		viewPager.addView(LevelSelectFragment.newInstance(TLevelTipo.NewYork), getString(R.string.title_level_section_newyork));
		viewPager.addView(LevelSelectFragment.newInstance(TLevelTipo.Rome), getString(R.string.title_level_section_rome));
		viewPager.addView(LevelSelectFragment.newInstance(TLevelTipo.Egypt), getString(R.string.title_level_section_egypt));
		viewPager.addView(LevelSelectFragment.newInstance(TLevelTipo.Stonehenge), getString(R.string.title_level_section_stonehenge));
		
        botonNivel = (ImageButton) rootView.findViewById(R.id.imageButtonLevel1);
		botonNivel.setOnClickListener(new OnLevelClickListener());
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		viewPager = null;
		botonNivel = null;
	}
    
    /* SECTION Métodos abstractos de ViewPagerFragment */
    
    @Override
    public void onPageSelected() { }
    
	/* SECTION Métodos Listener onClick */
	
	private class OnLevelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{			
			ActionBar actionBar = getActivity().getActionBar();
			
			if(estadoNiveles[actionBar.getSelectedNavigationIndex()])
			{
				mCallback.onLevelSelectionSelectClicked();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.text_level_disabled, Toast.LENGTH_SHORT).show();
			}
		}
    }
}
