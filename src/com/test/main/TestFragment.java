package com.test.main;

import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.NonSwipeableViewPager;
import com.project.main.R;
import com.test.audio.AudioFragment;
import com.test.multitouch.MultitouchFragment;
import com.test.social.SocialFragment;

public class TestFragment extends Fragment
{
	private ActionBar actionBar;
	
	private MultitouchFragment multiTouchFragment;
	private AudioFragment audioFragment;
	private SocialFragment socialFragment;
	
	private SectionViewPagerAdapter pageAdapter;
	private NonSwipeableViewPager viewPager;
	
	/* Constructora */
	
	public static final TestFragment newInstance()
	{
		TestFragment fragment = new TestFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		multiTouchFragment = MultitouchFragment.newInstance();
		audioFragment = AudioFragment.newInstance();
		socialFragment = SocialFragment.newInstance();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_test_layout, container, false);
		
		// Instanciar Elementos de la GUI
				
		actionBar = getActivity().getActionBar();
		
		pageAdapter = new SectionViewPagerAdapter(getActivity().getSupportFragmentManager());

		viewPager = (NonSwipeableViewPager) rootView.findViewById(R.id.pagerViewTest1);
		viewPager.removeAllViews();
		viewPager.setAdapter(pageAdapter);

		viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < pageAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(pageAdapter.getPageTitle(i)).setTabListener(pageAdapter));
		}
				
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		actionBar = null;
		pageAdapter = null;
		viewPager = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		multiTouchFragment = null;
		audioFragment = null;
		socialFragment = null;
	}
    
    /* Adaptador de PagerViewer */

	public class SectionViewPagerAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener
	{
		public SectionViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch(position)
			{
				case 0:
					return multiTouchFragment;
				case 1:
					return audioFragment;
				case 2:
					return socialFragment;
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_test_section_multitouch).toUpperCase(l);
				case 1:
					return getString(R.string.title_test_section_audio).toUpperCase(l);
				case 2:
					return getString(R.string.title_test_secton_social).toUpperCase(l);
			}
			
			return null;
		}
		
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			viewPager.setCurrentItem(tab.getPosition());
		}
	
		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
}
