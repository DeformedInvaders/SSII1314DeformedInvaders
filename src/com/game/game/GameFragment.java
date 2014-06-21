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
import com.game.data.InstanceLevel;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.main.model.GameResources;
import com.project.main.R;

public class GameFragment extends OpenGLFragment implements OnGameListener
{
	private GameFragmentListener mCallback;

	private InstanceLevel mLevel;
	private Character mCharacter;
	private int characterLivesEnemiesPhase;

	private GameOpenGLSurfaceView mCanvas;
	private TextView textScore;
	private IconImageButton buttonPlay;
	private ImageView[] imageCharacterLives, imageBossLives;

	private boolean gamePaused;
	
	BackgroundDataSaved dataSaved;

	/* Constructora */

	public static final GameFragment newInstance(GameFragmentListener callback, Character character, InstanceLevel level)
	{
		GameFragment fragment = new GameFragment();
		fragment.setParameters(callback, character, level);
		return fragment;
	}

	private void setParameters(GameFragmentListener callback, Character character, InstanceLevel level)
	{
		mCallback = callback;
		mCharacter = character;
		mLevel = level;
		gamePaused = true;
	}

	public interface GameFragmentListener
	{
		public void onGameFinished(final InstanceLevel nivel, final int score, TTypeEndgame endgame);
		public void onGameFailed(final InstanceLevel nivel, TTypeEndgame endgame);
		public void onGamePlaySoundEffect(int sound, boolean blockable);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_layout, container, false);
		
		imageCharacterLives = new ImageView[GamePreferences.MAX_CHARACTER_LIVES];
		imageBossLives = new ImageView[GamePreferences.MAX_BOSS_LIVES];

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewGame1);
		imageBackground.setBackgroundResource(mLevel.getBackground().getIdSun());

		mCanvas = (GameOpenGLSurfaceView) rootView.findViewById(R.id.gameGLSurfaceViewGame1);
		mCanvas.setParameters(this, mCharacter, mLevel);
		
		textScore = (TextView) rootView.findViewById(R.id.textViewGame1);
		
		buttonPlay = (IconImageButton) rootView.findViewById(R.id.imageButtonGame1);
		buttonPlay.setOnClickListener(new onPlayGameClickListener());
		
		LinearLayout layoutCharacter = (LinearLayout) rootView.findViewById(R.id.linearLayoutGame1);
		LinearLayout layoutBoss = (LinearLayout) rootView.findViewById(R.id.linearLayoutGame2);
		
		int imageWidth = (int) getActivity().getResources().getDimension(R.dimen.FragmentButton_LayoutWidth_Dimen);
		int imageHeight = (int) getActivity().getResources().getDimension(R.dimen.FragmentButton_LayoutHeight_Dimen);
		
		for(int i = 0; i < GamePreferences.MAX_CHARACTER_LIVES; i++)
		{
			imageCharacterLives[i] = new ImageView(getActivity());
			imageCharacterLives[i].setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			imageCharacterLives[i].setBackgroundResource(R.drawable.lives_character_heart);
			layoutCharacter.addView(imageCharacterLives[i]);
		}
		
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imageBossLives[i] = new ImageView(getActivity());
			imageBossLives[i].setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageHeight));
			imageBossLives[i].setBackgroundResource(R.drawable.lives_boss_heart);
			layoutBoss.addView(imageBossLives[i]);
		}
		
		hideBossLives();
		
		setCanvasListener(mCanvas);

		resetInterface();
		updateInterface();
		
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

		mCanvas = null;
		textScore = null;
		buttonPlay = null;
		imageCharacterLives = null;
		imageBossLives = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		mLevel = null;
		mCharacter = null;
		dataSaved = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		
		if (dataSaved != null)
		{
			mCanvas.restoreData(dataSaved);
		}
		
		mCanvas.onResume();
		
		resetInterface();
		updateInterface();
	}

	@Override
	public void onPause()
	{
		super.onPause();		
		mCanvas.onPause();
		dataSaved = mCanvas.saveData();
		
		mCanvas.selectPause();
		gamePaused = true;
	}

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void resetInterface() { }

	@Override
	protected void updateInterface()
	{
		buttonPlay.setActivo(!gamePaused);
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
				mCanvas.selectPause();

				sendToastMessage(R.string.text_game_paused);
			}
			else
			{
				mCanvas.selectResume();
			}

			resetInterface();
			updateInterface();
		}
	}

	/* Métodos privados */
	
	private void updateCharacterLives(int lives)
	{
		for(int i = 0; i < GamePreferences.MAX_CHARACTER_LIVES; i++)
		{
			imageCharacterLives[i].setBackgroundResource(R.drawable.lives_character_heart_broken);
		}
		
		for(int i = 0; i < lives; i++)
		{
			imageCharacterLives[i].setBackgroundResource(R.drawable.lives_character_heart);
		}
	}
	
	private void updateBossLives(int lives)
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imageBossLives[i].setBackgroundResource(R.drawable.lives_boss_heart_broken);
		}
		
		for(int i = 0; i < lives; i++)
		{
			imageBossLives[i].setBackgroundResource(R.drawable.lives_boss_heart);
		}
	}
	
	private void hideBossLives()
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imageBossLives[i].setVisibility(View.INVISIBLE);
		}		
	}
	
	private void showBossLives()
	{
		for(int i = 0; i < GamePreferences.MAX_BOSS_LIVES; i++)
		{
			imageBossLives[i].setVisibility(View.VISIBLE);
		}
	}
	
	private void updateScore(int score)
	{
		textScore.setText(getActivity().getString(R.string.text_game_score)+" "+score);
	}
	
	/* Métodos de OnGameListener */
	
	@Override
	public void onGameEnemiesFinished(int score, int characterLives, int bossLives)
	{
		characterLivesEnemiesPhase = characterLives;
		
		showBossLives();
		
		updateScore(score);
		updateCharacterLives(characterLives);
		updateBossLives(bossLives);
	}

	@Override
	public void onGameBossFinished(int score, int characterLives, int bossLives)
	{
		updateScore(score);
		updateCharacterLives(characterLives);
		updateBossLives(bossLives);
		
		if (characterLivesEnemiesPhase == GamePreferences.MAX_CHARACTER_LIVES)
		{
			if (characterLives == GamePreferences.MAX_CHARACTER_LIVES)
			{
				mCallback.onGameFinished(mLevel, score, TTypeEndgame.LevelMastered);
			}
			else
			{
				mCallback.onGameFinished(mLevel, score, TTypeEndgame.LevelPerfected);
			}
		}
		else
		{
			mCallback.onGameFinished(mLevel, score, TTypeEndgame.LevelCompleted);
		}
	}

	@Override
	public void onGameFailed(int score, int characterLives)
	{
		updateScore(score);
		updateCharacterLives(characterLives);
		
		mCallback.onGameFailed(mLevel, TTypeEndgame.GameOver);
	}
	
	@Override
	public void onGameScoreChanged(int score)
	{
		updateScore(score);
	}
	
	@Override
	public void onGameLivesChanged(int characterLives)
	{
		updateCharacterLives(characterLives);
	}
	
	@Override
	public void onGameLivesChanged(int characterLives, int bossLives)
	{
		updateCharacterLives(characterLives);
		updateBossLives(bossLives);
	}

	@Override
	public void onGamePlaySoundEffect(int sound, boolean blockable)
	{
		mCallback.onGamePlaySoundEffect(sound, blockable);
	}
}
