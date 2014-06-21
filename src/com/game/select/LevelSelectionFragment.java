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

	private List<Level> listaNiveles;
	private GameStatistics[] estadoNiveles;
	private int posicionInicial;

	/* Constructora */

	public static final LevelSelectionFragment newInstance(LevelSelectionFragmentListener c, List<Level> lista, GameStatistics[] estado)
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		fragment.setParameters(c, lista, estado, -1);
		return fragment;
	}
	
	public static final LevelSelectionFragment newInstance(LevelSelectionFragmentListener c, List<Level> lista, GameStatistics[] estado, TTypeLevel nivel)
	{
		LevelSelectionFragment fragment = new LevelSelectionFragment();
		fragment.setParameters(c, lista, estado, nivel.ordinal());
		return fragment;
	}

	private void setParameters(LevelSelectionFragmentListener c, List<Level> lista, GameStatistics[] estado, int nivel)
	{
		mCallback = c;
		listaNiveles = lista;
		estadoNiveles = estado;
		posicionInicial = nivel;
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
		Iterator<Level> it = listaNiveles.iterator();
		while (it.hasNext())
		{
			Level nivel = it.next();
			viewPager.addView(LevelSelectFragment.newInstance(this, nivel, estadoNiveles[i]), getString(nivel.getNombreNivel()));

			i++;
		}
		
		if (posicionInicial != -1)
		{
			viewPager.selectView(posicionInicial);
		}
		
		return rootView;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		listaNiveles = null;
		estadoNiveles = null;
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
