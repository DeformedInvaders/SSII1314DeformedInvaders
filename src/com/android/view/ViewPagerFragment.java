package com.android.view;

import android.support.v4.app.Fragment;

public abstract class ViewPagerFragment extends Fragment
{
	protected ViewPagerSwipeable viewPager;
	
	/* SECTION M�todos Abstractos */
	
	public abstract void onPageSelected(int page);
}
