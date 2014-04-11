package com.android.view;

public abstract class ViewPagerFragment extends AlertFragment
{
	protected ViewPagerSwipeable viewPager;

	/* Métodos Abstractos */

	public abstract void onPageSelected(int page);
}
