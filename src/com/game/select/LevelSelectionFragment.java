package com.game.select;

import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.game.data.Level;
import com.main.model.GameStatistics;
import com.project.main.R;

public class LevelSelectionFragment extends ViewPagerFragment implements OnLevelListener
{
	private LevelSelectionFragmentListener mCallback;

	private List<Level> mLevelList;
	private GameStatistics[] mStatistics;
	private int originalIndex;

	/* Constructora */

	public static final LevelSelectionFragment newInstance(LevelSelectionFragmentListener callback, List<Level> levelList, GameStatistics[] statistics)
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		fragment.setParameters(callback, levelList, statistics, -1);
		return fragment;
	}
	
	public static final LevelSelectionFragment newInstance(LevelSelectionFragmentListener callback, List<Level> levelList, GameStatistics[] statistics, TTypeLevel level)
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		fragment.setParameters(callback, levelList, statistics, level.ordinal());
		return fragment;
	}

	private void setParameters(LevelSelectionFragmentListener callback, List<Level> levelList, GameStatistics[] statistics, int index)
	{
		mCallback = callback;
		mLevelList = levelList;
		mStatistics = statistics;
		originalIndex = index;
	}

	public interface LevelSelectionFragmentListener
	{
		public void onLevelSelectionSelectLevel(final TTypeLevel level);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_game_selection_layout, container, false);

		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewLevelSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());

		int i = 0;
		Iterator<Level> it = mLevelList.iterator();
		while (it.hasNext())
		{
			Level nivel = it.next();
			viewPager.addView(LevelSelectFragment.newInstance(this, nivel, mStatistics[i]), getString(nivel.getLevelName()));

			i++;
		}
		
		if (originalIndex != -1)
		{
			viewPager.selectView(originalIndex);
		}
		
		return rootView;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		mLevelList = null;
		mStatistics = null;
	}

	/* Métodos abstractos de ViewPagerFragment */

	@Override
	public void onPageSelected(int page) { }

	/* Métodos abstractos de OnLevelListener */

	@Override
	public void onLevelSelected(TTypeLevel level)
	{
		mCallback.onLevelSelectionSelectLevel(level);
	}
}
