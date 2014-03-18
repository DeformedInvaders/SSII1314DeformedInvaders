package com.game.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.storage.ExternalStorageManager;
import com.android.view.OpenGLFragment;
import com.game.data.Level;
import com.game.data.Personaje;
import com.project.main.R;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;
	
	private ExternalStorageManager manager;
	
	private Level level;
	private Personaje personaje;

	private GameOpenGLSurfaceView canvas;
	private ImageButton botonRun, botonJump, botonCrouch, botonAttack;
	
	/* SECTION Constructora */
	
	public static final GameFragment newInstance(Personaje p, ExternalStorageManager m, Level l)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(p, m, l);
		return fragment;
	}
	
	private void setParameters(Personaje p, ExternalStorageManager m, Level l)
	{	
		personaje = p;
		manager = m;
		level = l;
	}
	
	public interface GameFragmentListener
	{
        public void onGameFinished(int level);
        public void onGameFailed(int level);
    }
	
	/* SECTION Métodos Fragment */
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (GameFragmentListener) activity;
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
        View rootView = inflater.inflate(R.layout.fragment_game_game_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(personaje, manager, this, level);
		
		botonRun = (ImageButton) rootView.findViewById(R.id.imageButtonGame1);
		botonJump = (ImageButton) rootView.findViewById(R.id.imageButtonGame2);
		botonCrouch = (ImageButton) rootView.findViewById(R.id.imageButtonGame3);
		botonAttack = (ImageButton) rootView.findViewById(R.id.imageButtonGame4);
		
		botonRun.setOnClickListener(new OnRunGameClickListener());
		botonJump.setOnClickListener(new OnJumpGameClickListener());
		botonCrouch.setOnClickListener(new OnCrouchGameClickListener());
		botonAttack.setOnClickListener(new OnAttackGameClickListener());
		
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
		
		botonRun = null;
		botonJump = null;
		botonAttack = null;
		botonCrouch = null;
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
	protected void reiniciarInterfaz() { }
	
	@Override
	protected void actualizarInterfaz() { }

	/* SECTION Métodos Listener onClick */
	
	private class OnRunGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarRun();
		}
	}
	
	private class OnJumpGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarJump();
		}
	}
	
	private class OnCrouchGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCrouch();			
		}
	}
	
	private class OnAttackGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAttack();
		}
	}
	
	/* SECTION Métodos de OnGameListener */
	
	public void onGameFinished()
	{
		mCallback.onGameFinished(level.getIndiceNivel());
	}
	
	public void onGameFailed()
	{
		mCallback.onGameFinished(level.getIndiceNivel());
	}
}
