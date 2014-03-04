package com.game.game;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.OpenGLFragment;
import com.game.data.Personaje;
import com.project.main.R;

public class GameFragment extends OpenGLFragment
{
	private Personaje personaje;

	private GameGLSurfaceView canvas;

	/* SECTION Constructora */
	
	public static final GameFragment newInstance(Personaje p)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(p);
		return fragment;
	}
	
	private void setParameters(Personaje p)
	{	
		personaje = p;
	}
	
	/* SECTION Métodos Fragment */
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_game_game_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (GameGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(personaje);
		
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
	
	/* SECTION Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz() { }
	
	@Override
	protected void actualizarInterfaz() { }

}
