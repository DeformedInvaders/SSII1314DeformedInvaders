package com.game.game;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.opengl.BackgroundDataSaved;
import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.creation.data.TTipoMovimiento;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.game.select.TTipoLevel;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;

	private InstanciaNivel level;
	private Personaje personaje;

	private GameOpenGLSurfaceView canvas;
	private TextView textoPuntuacion;
	private IconImageButton botonPlay;
	private ImageView[] imagenCharacterVidas, imagenBossVidas;

	private boolean gamePaused;
	
	BackgroundDataSaved dataSaved;

	/* Constructora */

	public static final GameFragment newInstance(GameFragmentListener c, Personaje p, InstanciaNivel l)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(c, p, l);
		return fragment;
	}

	private void setParameters(GameFragmentListener c, Personaje p, InstanciaNivel l)
	{
		mCallback = c;
		personaje = p;
		level = l;
		gamePaused = true;
	}

	public interface GameFragmentListener
	{
		public void onGameFinished(final TTipoLevel nivel, final int score, final int idImage, final String nameLevel, final boolean perfecto);
		public void onGameFailed(final TTipoLevel level, final int idImage);
		public void onGamePlaySound(final TTipoMovimiento tipo);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_layout, container, false);
		
		imagenCharacterVidas = new ImageView[GamePreferences.MAX_CHARACTER_LIVES];
		imagenBossVidas = new ImageView[GamePreferences.MAX_BOSS_LIVES];

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewGame1);
		imageBackground.setBackgroundResource(level.getFondoNivel().getIdTexturaSol());

		canvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		canvas.setParameters(this, personaje, level);
		
		textoPuntuacion = (TextView) rootView.findViewById(R.id.textViewGame1);
		
		botonPlay = (IconImageButton) rootView.findViewById(R.id.imageButtonGame1);
		botonPlay.setOnClickListener(new onPlayGameClickListener());
		
		LinearLayout layoutCharacter = (LinearLayout) rootView.findViewById(R.id.linearLayoutGame1);
		LinearLayout layoutBoss = (LinearLayout) rootView.findViewById(R.id.linearLayoutGame2);
		
		int imageWidth = (int) getActivity().getResources().getDimension(R.dimen.FragmentButton_LayoutWidth_Dimen);
		int imageHeight = (int) getActivity().getResources().getDimension(R.dimen.FragmentButton_LayoutHeight_Dimen);
		
		for(int i = 0; i < GamePreferences.MAX_CHARACTER_LIVES; i++)
		{
			imagenCharacterVidas[i] = new ImageView(getActivity());
			imagenCharacterVidas[i].setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			imagenCharacterVidas[i].setBackgroundResource(R.drawable.lives_character_heart);
			layoutCharacter.addView(imagenCharacterVidas[i]);
		}
		
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imagenBossVidas[i] = new ImageView(getActivity());
			imagenBossVidas[i].setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			imagenBossVidas[i].setBackgroundResource(R.drawable.lives_boss_heart);
			layoutBoss.addView(imagenBossVidas[i]);
		}
		
		ocultarVidasBoss();
		
		setCanvasListener(canvas);

		reiniciarInterfaz();
		actualizarInterfaz();
		
		List<Integer> listaMensajes = new ArrayList<Integer>();
		listaMensajes.add(R.string.text_tip_game_lives);
		listaMensajes.add(R.string.text_tip_game_crouch);
		listaMensajes.add(R.string.text_tip_game_jump);
		listaMensajes.add(R.string.text_tip_game_attack);
		listaMensajes.add(R.string.text_tip_game_complete);
		
		List<String> listaVideos = new ArrayList<String>();
		listaVideos.add(GameResources.VIDEO_GAME_LIVES_PATH);
		listaVideos.add(GameResources.VIDEO_GAME_CROUCH_PATH);
		listaVideos.add(GameResources.VIDEO_GAME_JUMP_PATH);
		listaVideos.add(GameResources.VIDEO_GAME_ATTACK_PATH);
		listaVideos.add(GameResources.VIDEO_GAME_COMPLETE_PATH);
		
		sendAlertMessage(R.string.text_tip_game_title, listaMensajes, listaVideos);		
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
		textoPuntuacion = null;
		botonPlay = null;
		imagenCharacterVidas = null;
		imagenBossVidas = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		level = null;
		personaje = null;
		dataSaved = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		if (dataSaved != null)
		{
			canvas.restoreData(dataSaved);
		}
		
		canvas.onResume();
		
		reiniciarInterfaz();
		actualizarInterfaz();
	}

	@Override
	public void onPause()
	{
		super.onPause();		
		canvas.onPause();
		dataSaved = canvas.saveData();
		
		canvas.seleccionarPause();
		gamePaused = true;
	}

	/* Métodos abstractos de OpenGLFragment */

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

	/* Métodos Listener onClick */

	private class onPlayGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			gamePaused = !gamePaused;

			if (gamePaused)
			{
				canvas.seleccionarPause();

				sendToastMessage(R.string.text_game_paused);
			}
			else
			{
				canvas.seleccionarResume();
			}

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}

	/* Métodos privados */
	
	private void actualizarVidasPersonaje(int lives)
	{
		for(int i = 0; i < GamePreferences.MAX_CHARACTER_LIVES; i++)
		{
			imagenCharacterVidas[i].setBackgroundResource(R.drawable.lives_character_heart_broken);
		}
		
		for(int i = 0; i < lives; i++)
		{
			imagenCharacterVidas[i].setBackgroundResource(R.drawable.lives_character_heart);
		}
	}
	
	private void actualizarVidasBoss(int lives)
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imagenBossVidas[i].setBackgroundResource(R.drawable.lives_boss_heart_broken);
		}
		
		for(int i = 0; i < lives; i++)
		{
			imagenBossVidas[i].setBackgroundResource(R.drawable.lives_boss_heart);
		}
	}
	
	private void ocultarVidasBoss()
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imagenBossVidas[i].setVisibility(View.INVISIBLE);
		}		
	}
	
	private void mostrarVidasBoss()
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imagenBossVidas[i].setVisibility(View.VISIBLE);
		}
	}
	
	private void actualizarPuntuacion(int score)
	{
		textoPuntuacion.setText(getActivity().getString(R.string.text_game_score)+" "+score);
	}
	
	/* Métodos de OnGameListener */
	
	@Override
	public void onGameEnemiesFinished(int score, int characterLives, int bossLives)
	{
		mostrarVidasBoss();
		
		actualizarPuntuacion(score);
		actualizarVidasPersonaje(characterLives);
		actualizarVidasBoss(bossLives);
	}

	@Override
	public void onGameBossFinished(int score, int characterLives)
	{
		actualizarPuntuacion(score);
		actualizarVidasPersonaje(characterLives);
		
		if(characterLives == GamePreferences.MAX_CHARACTER_LIVES)
		{
			mCallback.onGameFinished(level.getTipoNivel(), score, level.getFondoNivel().getIdPolaroid(TTipoEndgame.LevelPerfected), level.getNombreNivel(), true);

		}
		else
		{
			mCallback.onGameFinished(level.getTipoNivel(), score, level.getFondoNivel().getIdPolaroid(TTipoEndgame.LevelCompleted), level.getNombreNivel(), false);
		}
	}

	@Override
	public void onGameFailed(int score, int characterLives)
	{
		actualizarPuntuacion(score);
		actualizarVidasPersonaje(characterLives);
		
		mCallback.onGameFailed(level.getTipoNivel(), level.getFondoNivel().getIdPolaroid(TTipoEndgame.GameOver));
	}
	
	@Override
	public void onGameScoreChanged(int score)
	{
		actualizarPuntuacion(score);
	}
	
	@Override
	public void onGameLivesChanged(int characterLives)
	{
		actualizarVidasPersonaje(characterLives);
	}
	
	@Override
	public void onGameLivesChanged(int characterLives, int bossLives)
	{
		actualizarVidasPersonaje(characterLives);
		actualizarVidasBoss(bossLives);
	}

	@Override
	public void onGamePlaySound(TTipoMovimiento tipo)
	{
		mCallback.onGamePlaySound(tipo);
	}
}
