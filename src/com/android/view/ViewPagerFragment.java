package com.android.view;


import android.support.v4.app.Fragment;

public abstract class ViewPagerFragment extends Fragment
{
	protected SwipeableViewPager viewPager;
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onPageSelected();
}
