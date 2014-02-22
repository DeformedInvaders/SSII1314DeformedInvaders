package com.view.select;

import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.view.SwipeableViewPager;
import com.project.data.Personaje;
import com.project.main.R;
import com.project.main.ViewPagerFragment;

public class SelectionFragment extends ViewPagerFragment
{
	private ExternalStorageManager manager;
	private SocialConnector connector;
	
	private SelectionFragmentListener mCallback;
	
	private ImageButton botonReady, botonDelete;
	
	private List<Personaje> listaPersonajes;
	
	/* SECTION Constructora */
	
	public static final SelectionFragment newInstance(List<Personaje> l, ExternalStorageManager m, SocialConnector c)
	{
		SelectionFragment fragment = new SelectionFragment();
		fragment.setParameters(l, m, c);
		return fragment;
	}
	
	private void setParameters(List<Personaje> l, ExternalStorageManager m, SocialConnector c)
	{
		listaPersonajes = l;
		manager = m;
		connector = c;
	}
	
	public interface SelectionFragmentListener
	{
        public void onSelectionSelectClicked(int indice);
        public void onSelectionDeleteButtonClicked(int indice);
    }
	
	/* SECTION Métodos Fragment */
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (SelectionFragmentListener) activity;
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
		View rootView = inflater.inflate(R.layout.fragment_selection_layout, container, false);
		
		// Instanciar Elementos de la GUI
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonSelection1);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonSelection2);
		
		botonReady.setOnClickListener(new OnReadyClickListener());		
		botonDelete.setOnClickListener(new OnDeleteClickListener());

		viewPager = (SwipeableViewPager) rootView.findViewById(R.id.pagerViewSelection1);
		viewPager.setAdapter(this, getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		Iterator<Personaje> it = listaPersonajes.iterator();
		while(it.hasNext())
		{
			Personaje p = it.next();
			viewPager.addView(SelectFragment.newInstance(p, viewPager, manager, connector), p.getNombre());
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
			ActionBar actionBar = getActivity().getActionBar();
			mCallback.onSelectionSelectClicked(actionBar.getSelectedNavigationIndex());
		}
    }
    
    private class OnDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			ActionBar actionBar = getActivity().getActionBar();
			mCallback.onSelectionDeleteButtonClicked(actionBar.getSelectedNavigationIndex());
		}
    }
    
    /* SECTION Métodos abstractos de ViewPagerFragment */
    
    @Override
    public void onPageSelected() { }
}
