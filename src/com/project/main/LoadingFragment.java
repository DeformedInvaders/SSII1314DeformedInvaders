package com.project.main;

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

import com.project.data.Personaje;
import com.view.display.DisplayGLSurfaceView;

public class LoadingFragment extends Fragment
{
	private LoadingFragmentListener mCallback;
	
	private DisplayGLSurfaceView canvas;
	private ImageButton botonAdd, botonPlay, botonView;
	
	private List<Personaje> listaPersonajes;
	private int personajeSeleccionado;
	
	/* Constructora */
	
	public static final LoadingFragment newInstance(List<Personaje> lista, int indice)
	{
		LoadingFragment fragment = new LoadingFragment();
		fragment.setParameters(lista, indice);
		return fragment;
	}
	
	private void setParameters(List<Personaje> lista, int indice)
	{
		listaPersonajes = lista;
		personajeSeleccionado = indice;
	}
	
	public interface LoadingFragmentListener
	{
		public void onLoadingCreateButtonClicked();
		public void onLoadingSelectButtonClicked();
		public void onLoadingPlayButtonClicked();
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (LoadingFragmentListener) activity;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_loading_layout, container, false);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.removeAllTabs();
		
		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.DisplayGLSurfaceViewLoading1);
		if(personajeSeleccionado >= 0 && personajeSeleccionado < listaPersonajes.size())
		{
			Personaje p = listaPersonajes.get(personajeSeleccionado);
			canvas.setParameters(p.getEsqueleto(), p.getTextura());
		}
		
		botonAdd = (ImageButton) rootView.findViewById(R.id.imageButtonLoading1);
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonLoading2);
		botonView = (ImageButton) rootView.findViewById(R.id.imageButtonLoading3);
		
		botonAdd.setOnClickListener(new OnAddClickListener());
		botonView.setOnClickListener(new OnViewClickListener());
		botonPlay.setOnClickListener(new OnPlayClickListener());
		
		if(listaPersonajes.size() > 0) botonView.setVisibility(View.VISIBLE);
		
        return rootView;
    }
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
	}
	
	/* Listener de Botones */
	
	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onLoadingCreateButtonClicked();
		}
	}
	
	private class OnViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onLoadingSelectButtonClicked();
		}
	}
	
	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onLoadingPlayButtonClicked();
		}
	}

}
