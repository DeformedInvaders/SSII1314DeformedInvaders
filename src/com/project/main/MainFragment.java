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

public class MainFragment extends Fragment
{
	private MainFragmentListener mCallback;
	
	private DisplayGLSurfaceView canvas;
	private ImageButton botonAdd, botonPlay, botonView;
	
	private List<Personaje> listaPersonajes;
	private int personajeSeleccionado;
	
	/* Constructora */
	
	public static final MainFragment newInstance(List<Personaje> lista, int indice)
	{
		MainFragment fragment = new MainFragment();
		fragment.setParameters(lista, indice);
		return fragment;
	}
	
	private void setParameters(List<Personaje> lista, int indice)
	{
		listaPersonajes = lista;
		personajeSeleccionado = indice;
	}
	
	public interface MainFragmentListener
	{
		public void onMainCreateButtonClicked();
		public void onMainSelectButtonClicked();
		public void onMainPlayButtonClicked();
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (MainFragmentListener) activity;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{		
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_main_layout, container, false);
		
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.removeAllTabs();
		
		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewMain1);
		if(personajeSeleccionado >= 0 && personajeSeleccionado < listaPersonajes.size() && !listaPersonajes.isEmpty())
		{
			Personaje p = listaPersonajes.get(personajeSeleccionado);
			canvas.setParameters(p.getEsqueleto(), p.getTextura());
		}
		else
		{
			canvas.setParameters();
		}
		
		botonAdd = (ImageButton) rootView.findViewById(R.id.imageButtonLoading1);
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonLoading2);
		botonView = (ImageButton) rootView.findViewById(R.id.imageButtonLoading3);
		
		botonAdd.setOnClickListener(new OnAddClickListener());
		botonView.setOnClickListener(new OnViewClickListener());
		botonPlay.setOnClickListener(new OnPlayClickListener());
		
		if(listaPersonajes.isEmpty()) botonView.setVisibility(View.INVISIBLE);		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		canvas = null;
		botonAdd = null;
		botonPlay = null;
		botonView = null;
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
			mCallback.onMainCreateButtonClicked();
		}
	}
	
	private class OnViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainSelectButtonClicked();
		}
	}
	
	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayButtonClicked();
		}
	}

}
