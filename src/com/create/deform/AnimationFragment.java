package com.create.deform;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Textura;
import com.project.main.R;

public class AnimationFragment extends Fragment
{
	private AnimationFragmentListener mCallback;
	
	private ImageButton botonAnimReady;
	private SectionsAnimPagerAdapter sectionsAnimPagerAdapter;
	private ViewPager viewAnimPager;
	private List<DeformFragment> listaAnimFragmentos;
	
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	
	public static final AnimationFragment newInstance(Esqueleto e, Textura t)
	{
		AnimationFragment fragment = new AnimationFragment();
		fragment.setParameters(e, t);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t)
	{
		esqueletoActual = e;
		texturaActual = t;
	}
	
	public interface AnimationFragmentListener
	{
        public void onAnimationReadyButtonClicked(Movimientos m);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (AnimationFragmentListener) activity;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_animation_layout, container, false);
		
		// Instanciar Elementos de la GUI
		botonAnimReady = (ImageButton) rootView.findViewById(R.id.imageButtonAnim1);		
		botonAnimReady.setOnClickListener(new OnAnimReadyClickListener());	
				
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		listaAnimFragmentos = new ArrayList<DeformFragment>();
		
		for(int i = 0; i < 4; i++)
		{
			listaAnimFragmentos.add(DeformFragment.newInstance(esqueletoActual, texturaActual));
		}

		sectionsAnimPagerAdapter = new SectionsAnimPagerAdapter(getActivity().getSupportFragmentManager());

		viewAnimPager = (ViewPager) rootView.findViewById(R.id.pagerAnim1);
		viewAnimPager.removeAllViews();
		viewAnimPager.setAdapter(sectionsAnimPagerAdapter);

		viewAnimPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < sectionsAnimPagerAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(sectionsAnimPagerAdapter.getPageTitle(i)).setTabListener(new TabAnimListener()));
		}
				
        return rootView;
    }
	
    private class OnAnimReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			//TODO
			mCallback.onAnimationReadyButtonClicked(null);
		}
    }
	
	public class TabAnimListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			viewAnimPager.setCurrentItem(tab.getPosition());
		}
	
		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
	
	public class SectionsAnimPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsAnimPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if(position >= 0 && position < listaAnimFragmentos.size())
			{
				return listaAnimFragmentos.get(position);
			}
			
			return null;
		}

		@Override
		public int getCount()
		{
			return listaAnimFragmentos.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_section_run).toUpperCase(l);
				case 1:
					return getString(R.string.title_section_jump).toUpperCase(l);
				case 2:
					return getString(R.string.title_section_down).toUpperCase(l);
				case 3:
					return getString(R.string.title_section_attack).toUpperCase(l);
			}
			
			return null;
		}
	}
}
