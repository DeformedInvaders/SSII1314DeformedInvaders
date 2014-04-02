package com.character.select;

import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.view.ViewPagerFragment;
import com.android.view.ViewPagerSwipeable;
import com.game.data.Personaje;
import com.project.main.R;

public class CharacterSelectionFragment extends ViewPagerFragment
{
	private ExternalStorageManager manager;
	private SocialConnector connector;
	
	private CharacterSelectionFragmentListener mCallback;
	
	private ImageButton botonReady, botonDelete;
	
	private List<Personaje> listaPersonajes;
	
	/* SECTION Constructora */
	
	public static final CharacterSelectionFragment newInstance(List<Personaje> l, ExternalStorageManager m, SocialConnector c)
	{
		CharacterSelectionFragment fragment = new CharacterSelectionFragment();
		fragment.setParameters(l, m, c);
		return fragment;
	}
	
	private void setParameters(List<Personaje> l, ExternalStorageManager m, SocialConnector c)
	{
		listaPersonajes = l;
		manager = m;
		connector = c;
	}
	
	public interface CharacterSelectionFragmentListener
	{
        public void onCharacterSelectionSelectClicked(int indice);
        public void onCharacterSelectionDeleteButtonClicked(int indice);
    }
	
	/* SECTION Métodos Fragment */
	
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
		manager = null;
		connector = null;
		listaPersonajes = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_selection_character_selection_layout, container, false);
		
		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonSelection1);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonSelection2);
		
		botonReady.setOnClickListener(new OnReadyClickListener());		
		botonDelete.setOnClickListener(new OnDeleteClickListener());

		viewPager = (ViewPagerSwipeable) rootView.findViewById(R.id.pagerViewSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		Iterator<Personaje> it = listaPersonajes.iterator();
		while(it.hasNext())
		{
			Personaje p = it.next();
			viewPager.addView(CharacterSelectFragment.newInstance(p, viewPager, manager, connector), p.getNombre());
		}
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonReady = null;
		botonDelete = null;
		viewPager = null;
	}
	
	/* SECTION Métodos Listener onClick */
	 
	private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			mCallback.onCharacterSelectionSelectClicked(viewPager.getPosition());
		}
    }
    
    private class OnDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			mCallback.onCharacterSelectionDeleteButtonClicked(viewPager.getPosition());
		}
    }
    
    /* SECTION Métodos abstractos de ViewPagerFragment */
    
    @Override
    public void onPageSelected(int position) { }
}
