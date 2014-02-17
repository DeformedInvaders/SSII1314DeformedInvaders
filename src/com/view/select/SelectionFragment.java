package com.view.select;

import java.util.Iterator;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.storage.ExternalStorageManager;
import com.android.view.SwipeableViewPager;
import com.project.data.Personaje;
import com.project.main.R;

public class SelectionFragment extends Fragment
{
	private ExternalStorageManager manager;
	
	private SelectionFragmentListener mCallback;
	
	private ImageButton botonReady, botonDelete;
	private SwipeableViewPager viewPager;
	
	private List<Personaje> listaPersonajes;
	
	/* Constructora */
	
	public static final SelectionFragment newInstance(List<Personaje> lista)
	{
		SelectionFragment fragment = new SelectionFragment();
		fragment.setParameters(lista);
		return fragment;
	}
	
	private void setParameters(List<Personaje> lista)
	{
		listaPersonajes = lista;
	}
	
	public interface SelectionFragmentListener
	{
        public void onSelectionSelectClicked(int indice);
        public void onSelectionDeleteButtonClicked(int indice);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (SelectionFragmentListener) activity;
		
		manager = new ExternalStorageManager();
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;

		manager = null;
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
		viewPager.setAdapter(getActivity().getSupportFragmentManager(), getActivity().getActionBar());
		
		Iterator<Personaje> it = listaPersonajes.iterator();
		while(it.hasNext())
		{
			Personaje p = it.next();
			viewPager.addView(SelectFragment.newInstance(p.getEsqueleto(), p.getTextura(), p.getNombre(), viewPager, manager), p.getNombre());
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
	
	/* Listeners de Botones */
	 
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
}
