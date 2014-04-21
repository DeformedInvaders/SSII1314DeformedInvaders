package com.android.view;

public abstract class ViewPagerFragment extends AlertFragment
{
	protected ViewPagerSwipeable viewPager;
	private int savedPosition;
	
	/* M�todos Abstractos */

	protected abstract void onPageSelected(int page);
	
	/* M�todos Fragment */
	
	@Override
	public void onResume()
	{
		super.onResume();
		viewPager.selectView(savedPosition);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		savedPosition = viewPager.getPosition();
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		viewPager = null;
	}
}
