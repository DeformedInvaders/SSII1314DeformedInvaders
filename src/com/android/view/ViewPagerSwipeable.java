package com.android.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerSwipeable<T extends Fragment> extends ViewPager
{
	private ActionBar actionBar;
	private SectionViewPagerAdapter pageAdapter;

	private List<T> listaFragmentos;
	private List<String> listaNombres;

	private boolean swipeable;

	/* Constructora */

	public ViewPagerSwipeable(Context context)
	{
		super(context);

		swipeable = true;
		listaFragmentos = new ArrayList<T>();
		listaNombres = new ArrayList<String>();
	}

	public ViewPagerSwipeable(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		swipeable = true;
		listaFragmentos = new ArrayList<T>();
		listaNombres = new ArrayList<String>();
	}

	/* Métodos Publicos */

	public void setSwipeable(boolean swipe)
	{
		swipeable = swipe;
	}

	public void setAdapter(final ViewPagerFragment fragment, FragmentManager manager, ActionBar bar)
	{
		actionBar = bar;
		pageAdapter = new SectionViewPagerAdapter(manager);

		removeAllViews();
		setAdapter(pageAdapter);

		setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
				fragment.onPageSelected(position);
			}
		});
	}

	public void addView(T t, String s)
	{
		listaFragmentos.add(t);
		listaNombres.add(s);

		actionBar.addTab(actionBar.newTab().setText(s).setTabListener(pageAdapter));
	}
	
	public void selectView(int position)
	{
		if (position > 0 && position < listaFragmentos.size())
		{
			actionBar.selectTab(actionBar.getTabAt(position));
		}
	}

	public Iterator<T> iterator()
	{
		return listaFragmentos.iterator();
	}

	public int getPosition()
	{
		return actionBar.getSelectedNavigationIndex();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if (swipeable)
		{
			return super.onInterceptTouchEvent(event);
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (swipeable)
		{
			return super.onTouchEvent(event);
		}

		return false;
	}

	/* Métodos Adaptador de PagerViewer */

	public class SectionViewPagerAdapter extends FragmentStatePagerAdapter implements ActionBar.TabListener
	{
		public SectionViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if (position >= 0 && position < listaFragmentos.size())
			{
				return listaFragmentos.get(position);
			}

			return null;
		}

		@Override
		public int getCount()
		{
			return listaFragmentos.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			if (position >= 0 && position < listaNombres.size())
			{
				return listaNombres.get(position).toUpperCase(Locale.getDefault());
			}

			return null;
		}

		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
}
