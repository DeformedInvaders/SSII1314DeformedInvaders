package com.android.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.project.main.R;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class ViewFrameSwipeable<T extends Fragment> extends FrameLayout implements ActionBar.TabListener
{
	private ViewFrameFragment frameFragment;
	private ActionBar actionBar;
	private FragmentManager layoutManager;
	
	private List<T> listaFragmentos;
	private List<String> listaNombres;
	
	/* Constructora */

	public ViewFrameSwipeable(Context context)
	{
		this(context, null);
	}

	public ViewFrameSwipeable(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		listaFragmentos = new ArrayList<T>();
		listaNombres = new ArrayList<String>();
	}

	/* M�todos Publicos */
	
	public void setAdapter(final ViewFrameFragment fragment, FragmentManager manager, ActionBar bar)
	{
		frameFragment = fragment;
		actionBar = bar;
		layoutManager = manager;

		removeAllViews();
	}

	public void addView(T t, String s)
	{
		listaFragmentos.add(t);
		listaNombres.add(s);

		actionBar.addTab(actionBar.newTab().setText(s).setTabListener(this));
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
	
	/* M�todos interfaz ActionListener */

	@Override
	public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction)
	{		
		frameFragment.onPageSelected(tab.getPosition());
		
		FragmentTransaction transaction = layoutManager.beginTransaction();
		transaction.replace(getId(), listaFragmentos.get(tab.getPosition()));
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
		transaction.commit();
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) { }

	@Override
	public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) { }
}
