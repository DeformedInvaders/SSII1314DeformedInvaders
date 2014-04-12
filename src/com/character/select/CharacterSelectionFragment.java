package com.character.select;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.game.data.Personaje;
import com.project.main.R;

public class CharacterSelectionFragment extends ViewPagerFragment implements OnCharacterListener
{
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;
	private SocialConnector connector;

	private CharacterSelectionFragmentListener mCallback;

	private List<Personaje> listaPersonajes;

	/* Constructora */

	public static final CharacterSelectionFragment newInstance(List<Personaje> l, InternalStorageManager im, ExternalStorageManager em, SocialConnector c)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(l, im, em, c);
		return fragment;
	}

	private void setParameters(List<Personaje> l, InternalStorageManager im, ExternalStorageManager em, SocialConnector c)
	{
		listaPersonajes = l;
		internalManager = im;
		externalManager = em;
		connector = c;
	}

	public interface CharacterSelectionFragmentListener
	{
		public void onCharacterSelectionSelectClicked(int indice);
		public void onCharacterSelectionDeleteButtonClicked(int indice);
		public void onCharacterSelectionRepaintButtonClicked(int indice);
		public void onCharacterSelectionRenameButtonClicked(int indice);
		public void onCharacterSelectionExportButtonClicked(int indice);
	}

	/* Métodos Fragment */

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (CharacterSelectionFragmentListener) activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
		internalManager = null;
		externalManager = null;
		connector = null;
		listaPersonajes = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_character_selection_layout, container, false);

		// Instanciar Elementos de la GUI
		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewCharacterSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());

		Iterator<Personaje> it = listaPersonajes.iterator();
		while (it.hasNext())
		{
			Personaje p = it.next();
			viewPager.addView(CharacterSelectFragment.newInstance(this, p, viewPager, internalManager, externalManager, connector), p.getNombre());
		}

		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		viewPager = null;
	}

	/* Métodos abstractos de ViewPagerFragment */

	@Override
	public void onPageSelected(int position) { }

	/* Métodos abstractos de OnCharacterListener */
	
	@Override
	public void onCharacterSelected()
	{
		mCallback.onCharacterSelectionSelectClicked(viewPager.getPosition());
	}

	@Override
	public void onCharacterDeleted()
	{
		mCallback.onCharacterSelectionDeleteButtonClicked(viewPager.getPosition());
	}
	
	@Override
	public void onCharacterRepainted()
	{
		mCallback.onCharacterSelectionRepaintButtonClicked(viewPager.getPosition());
	}
	
	@Override
	public void onCharacterRenamed()
	{
		mCallback.onCharacterSelectionRenameButtonClicked(viewPager.getPosition());
	}
	
	@Override
	public void onCharacterExported()
	{
		mCallback.onCharacterSelectionExportButtonClicked(viewPager.getPosition());
	}
}
