package com.android.view;

public abstract class ViewPagerFragment extends AlertFragment
{
	protected ViewPagerSwipeable viewPager;

	/* M�todos Abstractos */

	public abstract void onPageSelected(int page);
}
