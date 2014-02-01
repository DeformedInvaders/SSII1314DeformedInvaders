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
	private ImageButton botonMainAdd, botonMainPlay, botonMainView;
	
	private List<Personaje> listaPersonajes;
	private int personajeSeleccionado;
	
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
		
		botonMainAdd = (ImageButton) rootView.findViewById(R.id.imageButtonMain1);
		botonMainPlay = (ImageButton) rootView.findViewById(R.id.imageButtonMain2);
		botonMainView = (ImageButton) rootView.findViewById(R.id.imageButtonMain3);
		
		botonMainAdd.setOnClickListener(new OnMainAddClickListener());
		botonMainView.setOnClickListener(new OnMainViewClickListener());
		botonMainPlay.setOnClickListener(new OnMainPlayClickListener());
		
		botonMainView.setEnabled(listaPersonajes.size() > 0);
		
        return rootView;
    }

	private void destroyLoadingActivity(int buttonId)
	{
		if(buttonId == botonMainAdd.getId())
		{
			mCallback.onLoadingCreateButtonClicked();
		}
		else if(buttonId == botonMainView.getId())
		{
			mCallback.onLoadingSelectButtonClicked();
		}
		else if(buttonId == botonMainPlay.getId())
		{
			mCallback.onLoadingPlayButtonClicked();
		}
	}
	
	private class OnMainAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}
	
	private class OnMainViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}
	
	private class OnMainPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}

}
