package com.character.select;

import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.project.main.R;

public class CharacterSelectionFragment extends ViewPagerFragment implements OnCharacterListener
{
	private CharacterSelectionFragmentListener mCallback;
	
	private List<Character> mCharacterList;
	private int originalIndex;
	
	/* Constructora */

	public static final CharacterSelectionFragment newInstance(CharacterSelectionFragmentListener callback, List<Character> characterList)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(callback, characterList, -1);
		return fragment;
	}
	
	public static final CharacterSelectionFragment newInstance(CharacterSelectionFragmentListener callback, List<Character> characterList, CharacterSelectionDataSaved dataSaved)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(callback, characterList, dataSaved.getIndex());
		return fragment;
	}

	private void setParameters(CharacterSelectionFragmentListener callback, List<Character> characterList, int index)
	{
		mCallback = callback;
		mCharacterList = characterList;
		originalIndex = index;
	}

	public interface CharacterSelectionFragmentListener
	{
		public void onCharacterSelectionSelectCharacter(final int indice);
		public void onCharacterSelectionDeleteCharacter(final int indice);
		public void onCharacterSelectionRepaintCharacter(final int indice, final CharacterSelectionDataSaved datosSalvados);
		public void onCharacterSelectionRedeformCharacter(final int indice, final CharacterSelectionDataSaved datosSalvados);
		public void onCharacterSelectionRenameCharacter(final int indice);
		public void onCharacterSelectionExportCharacter(final int indice);
		public void onCharacterSelectionPostPublish(final String mensaje, final Bitmap bitmap);
		public void onCharacterSelectionPlaySoundEffect(int sound);
	}

	/* M�todos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_character_selection_layout, container, false);

		// Instanciar Elementos de la GUI
		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewCharacterSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		ImageView fondo = (ImageView) rootView.findViewById(R.id.imageViewCharacterSelection1);
		if (GamePreferences.IS_LONG_RATIO())
		{
			fondo.setBackgroundResource(R.drawable.background_long_display);
		}
		else
		{
			fondo.setBackgroundResource(R.drawable.background_notlong_display);
		}

		Iterator<Character> it = mCharacterList.iterator();
		while (it.hasNext())
		{
			Character character = it.next();
			viewPager.addView(CharacterSelectFragment.newInstance(this, character), character.getName());
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
		mCharacterList = null;
	}

	/* M�todos abstractos de ViewPagerFragment */

	@Override
	public void onPageSelected(int position) { }

	/* M�todos abstractos de OnCharacterListener */
	
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
	public void onCharacterRedeformed()
	{
		mCallback.onCharacterSelectionRedeformCharacter(viewPager.getPosition(), new CharacterSelectionDataSaved(viewPager.getPosition()));
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
	public void onPlaySoundEffect(int sound)
	{
		mCallback.onCharacterSelectionPlaySoundEffect(sound);
	}
}
