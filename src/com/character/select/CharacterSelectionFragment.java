package com.character.select;

import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.project.main.R;

public class CharacterSelectionFragment extends ViewPagerFragment implements OnCharacterListener
{
	private CharacterSelectionFragmentListener mCallback;
	
	private List<Personaje> listaPersonajes;
	private int posicionInicial;
	
	/* Constructora */

	public static final CharacterSelectionFragment newInstance(CharacterSelectionFragmentListener c, List<Personaje> l)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(c, l, -1);
		return fragment;
	}
	
	public static final CharacterSelectionFragment newInstance(CharacterSelectionFragmentListener c, List<Personaje> l, CharacterSelectionDataSaved datosSalvados)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(c, l, datosSalvados.getIndice());
		return fragment;
	}

	private void setParameters(CharacterSelectionFragmentListener c, List<Personaje> l, int indice)
	{
		mCallback = c;
		listaPersonajes = l;
		posicionInicial = indice;
	}

	public interface CharacterSelectionFragmentListener
	{
		public void onCharacterSelectionSelectCharacter(final int indice);
		public void onCharacterSelectionDeleteCharacter(final int indice);
		public void onCharacterSelectionRepaintCharacter(final int indice, final CharacterSelectionDataSaved datosSalvados);
		public void onCharacterSelectionRenameCharacter(final int indice);
		public void onCharacterSelectionExportCharacter(final int indice);
		public void onCharacterSelectionPostPublish(final String mensaje, final Bitmap bitmap);
		public void onCharacterSelectionPlaySound(final TTipoMovimiento tipo, final int indice);
	}

	/* Métodos Fragment */

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
			viewPager.addView(CharacterSelectFragment.newInstance(this, p), p.getNombre());
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
		listaPersonajes = null;
	}

	/* Métodos abstractos de ViewPagerFragment */

	@Override
	public void onPageSelected(int position) { }

	/* Métodos abstractos de OnCharacterListener */
	
	@Override
	public void onCharacterSelected()
	{
		mCallback.onCharacterSelectionSelectCharacter(viewPager.getPosition());
	}

	@Override
	public void onCharacterDeleted()
	{
		mCallback.onCharacterSelectionDeleteCharacter(viewPager.getPosition());
	}
	
	@Override
	public void onCharacterRepainted()
	{
		mCallback.onCharacterSelectionRepaintCharacter(viewPager.getPosition(), new CharacterSelectionDataSaved(viewPager.getPosition()));
	}
	
	@Override
	public void onCharacterRenamed()
	{
		mCallback.onCharacterSelectionRenameCharacter(viewPager.getPosition());
	}
	
	@Override
	public void onCharacterExported()
	{
		mCallback.onCharacterSelectionExportCharacter(viewPager.getPosition());
	}

	@Override
	public void onPostPublished(String text, Bitmap bitmap)
	{
		mCallback.onCharacterSelectionPostPublish(text, bitmap);
	}

	@Override
	public void onSetSwipeable(boolean swipeable)
	{
		viewPager.setSwipeable(swipeable);
	}

	@Override
	public void onPlaySound(TTipoMovimiento tipo)
	{
		mCallback.onCharacterSelectionPlaySound(tipo, viewPager.getPosition());
	}
}
