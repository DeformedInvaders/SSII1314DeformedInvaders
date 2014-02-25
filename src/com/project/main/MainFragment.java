package com.project.main;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.storage.ExternalStorageManager;
import com.create.design.TDisplayTipo;
import com.project.data.Personaje;
import com.view.display.DisplayGLSurfaceView;

public class MainFragment extends OpenGLFragment
{
	private MainFragmentListener mCallback;
	
	private DisplayGLSurfaceView canvas;
	private ImageButton botonCrear, botonJugar, botonSeleccionar;
	
	private List<Personaje> listaPersonajes;
	private int personajeSeleccionado;
	
	private ExternalStorageManager externalManager;
	
	/* SECTION Constructora */
	
	public static final MainFragment newInstance(List<Personaje> lista, int indice, ExternalStorageManager m)
	{
		MainFragment fragment = new MainFragment();
		fragment.setParameters(lista, indice, m);
		return fragment;
	}
	
	private void setParameters(List<Personaje> lista, int indice, ExternalStorageManager m)
	{
		listaPersonajes = lista;
		personajeSeleccionado = indice;
		externalManager = m;
	}
	
	public interface MainFragmentListener
	{
		public void onMainCreateButtonClicked();
		public void onMainSelectButtonClicked();
		public void onMainPlayButtonClicked();
    }
	
	/* SECTION Métodos Fragment */
	
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
		listaPersonajes = null;
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
			canvas.setParameters(p, externalManager, TDisplayTipo.Main);
		}
		else
		{
			canvas.setParameters(TDisplayTipo.Main);
		}
		
		botonCrear = (ImageButton) rootView.findViewById(R.id.imageButtonMain1);
		botonSeleccionar = (ImageButton) rootView.findViewById(R.id.imageButtonMain3);
		botonJugar = (ImageButton) rootView.findViewById(R.id.imageButtonMain2);
		
		botonCrear.setOnClickListener(new OnAddClickListener());
		botonSeleccionar.setOnClickListener(new OnViewClickListener());
		botonJugar.setOnClickListener(new OnGameClickListener());
		
		setCanvasListener(canvas);

		reiniciarInterfaz();
		actualizarInterfaz();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		canvas = null;
		botonCrear = null;
		botonJugar = null;
		botonSeleccionar = null;
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
		canvas.saveData();
		canvas.onPause();
	}
	
	/* SECTION Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz()
	{
		botonSeleccionar.setVisibility(View.INVISIBLE);	
	}

	@Override
	protected void actualizarInterfaz()
	{
		if(!listaPersonajes.isEmpty())
		{
			botonSeleccionar.setVisibility(View.VISIBLE);		
		}
	}
	
	/* SECTION Métodos Listener onClick */
	
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
	
	private class OnGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayButtonClicked();
		}
	}
}
