package com.android.view;

public abstract class ViewFrameFragment extends AlertFragment
{
	protected ViewFrameSwipeable frameLayout;
	private int savedPosition;
	
	/* Métodos Abstractos */

	protected abstract void onPageSelected(int page);
	
/* Métodos Fragment */
	
	@Override
	public void onResume()
	{
		super.onResume();
		frameLayout.selectView(savedPosition);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		savedPosition = frameLayout.getPosition();
	}
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		frameLayout = null;
	}
}
