package com.game.game;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.opengl.BackgroundDataSaved;
import com.android.sensor.OrientationDetector;
import com.android.touch.GameDetector;
import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTypeMovement;
import com.game.data.InstanceLevel;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private GameOpenGLRenderer mRenderer;
	private OnGameListener mListener;

	private boolean animationEnded;
	private int numFrames;

	private Handler handler;
	private Runnable task;

	private boolean threadActive;
	
	private GameDetector gameDetector;
	
	private OrientationDetector sensorDetector;

	/* Constructora */

	public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, true);

		animationEnded = true;
		numFrames = 0;
	}

	public void setParameters(OnGameListener listener, Character personaje, InstanceLevel nivel)
	{
		mListener = listener;

		mRenderer = new GameOpenGLRenderer(getContext(), personaje, nivel);
		setRenderer(mRenderer);
				
		handler = new Handler();
		
		task = new Runnable() {
			@Override
			public void run()
			{
				if (mRenderer.playAnimation())
				{
					mRenderer.startAnimation(TTypeMovement.Run);
					animationEnded = true;
				}

				requestRender();

				switch (mRenderer.isGameEnded())
				{
					case CharacterLifeLost:
						mListener.onGamePlaySoundEffect(R.raw.effect_game_loselife, false);
						mListener.onGameLivesChanged(mRenderer.getCharacterLives());
						mListener.onGameScoreChanged(mRenderer.getScore());
						postDelayed(this);
					break;
					case BossLifeLost:
						mListener.onGamePlaySoundEffect(R.raw.effect_game_loselife, false);
						mListener.onGameLivesChanged(mRenderer.getCharacterLives(), mRenderer.getBossLives());
						mListener.onGameScoreChanged(mRenderer.getScore());
						postDelayed(this);
					break;
					case ScoreChanged:
						mListener.onGameScoreChanged(mRenderer.getScore());
						postDelayed(this);
					break;
					case EnemiesPhaseEnded:
						mListener.onGameEnemiesFinished(mRenderer.getScore(), mRenderer.getCharacterLives(), mRenderer.getBossLives());
						sensorDetector.onSensorCalibrated();
						postDelayed(this);
					break;
					case BossPhaseEnded:
						mListener.onGameBossFinished(mRenderer.getScore(), mRenderer.getCharacterLives(), mRenderer.getBossLives());
					break;
					case GameOver:	
						mListener.onGameFailed(mRenderer.getScore(), mRenderer.getCharacterLives());
					break;
					case Nothing:
						postDelayed(this);
					break;
				}
				
				numFrames++;
				requestRender();
			}
		};

		mRenderer.startAnimation(TTypeMovement.Run);
		animationEnded = true;
		threadActive = false;
	}
	
	private void postDelayed(Runnable r)
	{
		if (GamePreferences.GET_ESTADO_GAME() == TStateGame.EnemiesPhase)
		{
			handler.postDelayed(r, GamePreferences.TIME_INTERVAL_ANIMATION(numFrames));
		}
		else
		{
			handler.postDelayed(r, GamePreferences.TIME_INTERVAL_ANIMATION(mRenderer.getBossLives()));
		}
	}

	@Override
	public void setDetectorState(TStateDetector e)
	{
		super.setDetectorState(e);
		
		if (gameDetector == null)
		{
			gameDetector = new GameDetector() {

				@Override
				public void onDragUp()
				{
					if (GamePreferences.GET_ESTADO_GAME() == TStateGame.EnemiesPhase)
					{
						playAnimation(TTypeMovement.Jump);
					}
				}

				@Override
				public void onDragDown()
				{
					if (GamePreferences.GET_ESTADO_GAME() == TStateGame.EnemiesPhase)
					{
						playAnimation(TTypeMovement.Crouch);
					}
				}
				
				@Override
				public void onTouchMove(float pixelY)
				{
					if (GamePreferences.GET_ESTADO_GAME() == TStateGame.BossPhase && !GamePreferences.IS_SENSOR_ENABLED())
					{
						moveCharacter(pixelY);
					}
				}

				@Override
				public void onTap()
				{
					playAnimation(TTypeMovement.Attack);
				}
				
			};
		}
		
		if (sensorDetector == null)
		{
			sensorDetector = new OrientationDetector(getContext()) {
				
				@Override
				public void onIncreaseXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						moveCharacter(TStateCharacter.Up);
					}
				}

				@Override
				public void onDecreaseXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						moveCharacter(TStateCharacter.Down);
					}
				}

				@Override
				public void onStabilizeXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						moveCharacter(TStateCharacter.Nothing);
					}
				}
				
			};
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event != null)
		{
			gameDetector.onTouchEvent(event);
			requestRender();
	
			return true;
		}
	
		return false;

	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		sensorDetector.onResume();
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		sensorDetector.onPause();
		selectPause();
	}
	
	/* Métodos de Selección de Estado */
	
	public void moveCharacter(TStateCharacter estado)
	{
		if (threadActive)
		{
			mRenderer.moveCharacter(estado);
		}
	}
	
	public void moveCharacter(float pixelY)
	{
		if (threadActive)
		{
			mRenderer.moveCharacter(pixelY, getWidth(), getHeight());
		}
	}
	
	public void playAnimation(TTypeMovement movimiento)
	{
		if (threadActive)
		{
			if (animationEnded)
			{
				if (mRenderer.startAnimation(movimiento))
				{
					if (GamePreferences.GET_ESTADO_GAME() == TStateGame.EnemiesPhase)
					{
						int sound = movimiento.getSound();
						if (sound != -1)
						{
							mListener.onGamePlaySoundEffect(sound, true);
						}
					}
					else
					{
						mListener.onGamePlaySoundEffect(R.raw.effect_game_shot, false);
					}
				}
				
				requestRender();
				
				animationEnded = false;
			}
		}
	}

	public void selectResume()
	{
		if (!threadActive)
		{
			task.run();
			threadActive = true;
			
			mListener.onGameScoreChanged(mRenderer.getScore());
		}
	}

	public void selectPause()
	{
		if (threadActive)
		{
			handler.removeCallbacks(task);
			threadActive = false;
		}
	}
	
	/* Métodos de Guardado de Información */

	public BackgroundDataSaved saveData()
	{
		return mRenderer.saveData();
	}
	
	public void restoreData(BackgroundDataSaved data)
	{
		mRenderer.restoreData(data);
		requestRender();
	}
}
