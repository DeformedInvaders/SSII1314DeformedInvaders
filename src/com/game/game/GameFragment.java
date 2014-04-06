package com.game.game;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.storage.ExternalStorageManager;
import com.android.view.OpenGLFragment;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.project.main.R;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;

	private ExternalStorageManager manager;

	private InstanciaNivel level;
	private Personaje personaje;

	private GameOpenGLSurfaceView canvas;
	private ImageButton botonPlay;

	private boolean gamePaused;

	/* SECTION Constructora */

	public static final GameFragment newInstance(Personaje p, ExternalStorageManager m, InstanciaNivel l)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(p, m, l);
		return fragment;
	}

	private void setParameters(Personaje p, ExternalStorageManager m, InstanciaNivel l)
	{
		personaje = p;
		manager = m;
		level = l;
	}

	public interface GameFragmentListener
	{
		public void onGameFinished(int level, int idImage, String nameLevel);

		public void onGameFailed(int level, int idImage);
	}

	/* SECTION Métodos Fragment */

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (GameFragmentListener) activity;

		gamePaused = true;
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
		View rootView = inflater.inflate(R.layout.fragment_game_layout, container, false);

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewGame1);
		imageBackground.setBackgroundResource(level.getFondoNivel().getIdTexturaCielo());

		canvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(personaje, manager, this, level);
		
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonGame1);
		botonPlay.setOnClickListener(new onPlayGameClickListener());

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

		botonPlay = null;
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
		botonPlay.setBackgroundResource(R.drawable.icon_game_pause);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (gamePaused)
		{
			botonPlay.setBackgroundResource(R.drawable.icon_game_play);
		}
	}

	/* SECTION Métodos Listener onClick */

	private class onPlayGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			gamePaused = !gamePaused;

			if (gamePaused)
			{
				canvas.seleccionarPause();

				Toast.makeText(getActivity(), R.string.text_game_paused, Toast.LENGTH_SHORT).show();
			}
			else
			{
				canvas.seleccionarResume();
			}

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	/* SECTION Métodos de OnGameListener */

	public void onGameFinished()
	{
		mCallback.onGameFinished(level.getIndiceNivel(), level.getFondoNivel().getIdTextureLevelCompleted(), level.getNombreNivel());
	}

	public void onGameFailed()
	{
		mCallback.onGameFailed(level.getIndiceNivel(), level.getFondoNivel().getIdTextureGameOver());
	}
}
