package com.main.view;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.character.display.DisplayOpenGLSurfaceView;
import com.character.display.OnDisplayListener;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.project.main.R;

public class MainFragment extends OpenGLFragment implements OnDisplayListener
{
	private MainFragmentListener mCallback;

	private DisplayOpenGLSurfaceView canvas;
	private IconImageButton botonCrear, botonImportar, botonJugar, botonSeleccionar, botonVideo;

	private Personaje personaje;
	private int numeroPersonajes;

	/* Constructora */

	public static final MainFragment newInstance(MainFragmentListener c, Personaje p, int n)
	{
		MainFragment fragment = new MainFragment();
		fragment.setParameters(c, p, n);
		return fragment;
	}

	private void setParameters(MainFragmentListener c, Personaje p, int n)
	{
		mCallback = c;
		personaje = p;
		numeroPersonajes = n;
	}

	public interface MainFragmentListener
	{
		public void onMainImportCharacter();
		public void onMainCreateCharacter();
		public void onMainSelectCharacter();
		public void onMainPlayGame();
		public void onMainPlaySoundEffect(int sound);
		public void onMainPlayVideo();
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_project_main_layout, container, false);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.removeAllTabs();

		// Instanciar Elementos de la GUI
		canvas = (DisplayOpenGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewMain1);
		
		if (personaje != null)
		{
			canvas.setParameters(this, personaje, true);
		}
		else
		{
			canvas.setParameters();
		}
		
		ImageView fondo = (ImageView) rootView.findViewById(R.id.imageViewMain1);
		if (GamePreferences.IS_LONG_RATIO())
		{
			fondo.setBackgroundResource(R.drawable.background_long_main);
		}
		else
		{
			fondo.setBackgroundResource(R.drawable.background_notlong_main);
		}

		botonCrear = (IconImageButton) rootView.findViewById(R.id.imageButtonMain1);
		botonSeleccionar = (IconImageButton) rootView.findViewById(R.id.imageButtonMain3);
		botonJugar = (IconImageButton) rootView.findViewById(R.id.imageButtonMain2);
		botonImportar = (IconImageButton) rootView.findViewById(R.id.imageButtonMain4);
		botonVideo = (IconImageButton) rootView.findViewById(R.id.imageButtonMain5);
		
		botonCrear.setOnClickListener(new OnAddClickListener());
		botonSeleccionar.setOnClickListener(new OnViewClickListener());
		botonJugar.setOnClickListener(new OnGameClickListener());
		botonImportar.setOnClickListener(new OnImportClickListener());
		botonVideo.setOnClickListener(new OnVideoClickListener());

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
		botonImportar = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		personaje = null;
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

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void reiniciarInterfaz()
	{
		botonSeleccionar.setVisibility(View.INVISIBLE);
		botonJugar.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (numeroPersonajes > 0 && numeroPersonajes <= GamePreferences.MAX_CHARACTERS)
		{
			botonSeleccionar.setVisibility(View.VISIBLE);
			
			if (personaje != null)
			{
				botonJugar.setVisibility(View.VISIBLE);
			}
		}
	}

	/* Métodos Listener onClick */

	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainCreateCharacter();
		}
	}

	private class OnViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainSelectCharacter();
		}
	}

	private class OnGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayGame();
		}
	}
	
	private class OnImportClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainImportCharacter();
		}
	}
	
	private class OnVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayVideo();
		}
	}
	
	/* Métodos de la interfaz OnDisplayListener */

	@Override
	public void onDisplayPlaySoundEffect(int sound)
	{
		mCallback.onMainPlaySoundEffect(sound);
	}
}
