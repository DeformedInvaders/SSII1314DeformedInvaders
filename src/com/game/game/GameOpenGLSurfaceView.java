package com.game.game;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.opengl.BackgroundDataSaved;
import com.android.touch.GameDetector;
import com.android.touch.SensorDetector;
import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTipoMovimiento;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.main.model.GamePreferences;
import com.project.main.R;

public class GameOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private GameOpenGLRenderer renderer;
	private OnGameListener mListener;

	private boolean animacionFinalizada;
	private int contadorCiclos;

	private Handler handler;
	private Runnable task;

	private boolean threadActivo;
	
	private GameDetector gameDetector;
	
	private SensorDetector sensorDetector;

	/* Constructora */

	public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, true);

		animacionFinalizada = true;
		contadorCiclos = 0;
	}

	public void setParameters(OnGameListener listener, Personaje personaje, InstanciaNivel nivel)
	{
		mListener = listener;

		renderer = new GameOpenGLRenderer(getContext(), personaje, nivel);
		setRenderer(renderer);
				
		handler = new Handler();
		
		task = new Runnable() {
			@Override
			public void run()
			{
				if (renderer.playAnimation())
				{
					renderer.seleccionarAnimacion(TTipoMovimiento.Run);
					animacionFinalizada = true;
				}

				requestRender();

				switch (renderer.isGameEnded())
				{
					case VidaPerdidaPersonaje:
						mListener.onGamePlaySoundEffect(R.raw.effect_game_loselife, false);
						mListener.onGameLivesChanged(renderer.getVidasPersonaje());
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						postDelayed(this);
					break;
					case VidaPerdidaBoss:
						mListener.onGamePlaySoundEffect(R.raw.effect_game_loselife, false);
						mListener.onGameLivesChanged(renderer.getVidasPersonaje(), renderer.getVidasBoss());
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						postDelayed(this);
					break;
					case CambioPuntuacion:
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						postDelayed(this);
					break;
					case FinFaseEnemigos:
						mListener.onGameEnemiesFinished(renderer.getPuntuacion(), renderer.getVidasPersonaje(), renderer.getVidasBoss());
						sensorDetector.onSensorCalibrated();
						postDelayed(this);
					break;
					case FinFaseBoss:
						mListener.onGameBossFinished(renderer.getPuntuacion(), renderer.getVidasPersonaje(), renderer.getVidasBoss());
					break;
					case FinJuegoDerrota:	
						mListener.onGameFailed(renderer.getPuntuacion(), renderer.getVidasPersonaje());
					break;
					case Nada:
						postDelayed(this);
					break;
				}
				
				contadorCiclos++;
				requestRender();
			}
		};

		renderer.seleccionarAnimacion(TTipoMovimiento.Run);
		animacionFinalizada = true;
		threadActivo = false;
	}
	
	private void postDelayed(Runnable r)
	{
		if (GamePreferences.GET_ESTADO_GAME() == TEstadoGame.FaseEnemies)
		{
			handler.postDelayed(r, GamePreferences.TIME_INTERVAL_ANIMATION(contadorCiclos));
		}
		else
		{
			handler.postDelayed(r, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getVidasBoss()));
		}
	}

	@Override
	public void setEstado(TEstadoDetector e)
	{
		super.setEstado(e);
		
		if (gameDetector == null)
		{
			gameDetector = new GameDetector() {

				@Override
				public void onDragUp()
				{
					if (GamePreferences.GET_ESTADO_GAME() == TEstadoGame.FaseEnemies)
					{
						seleccionarAnimacion(TTipoMovimiento.Jump);
					}
				}

				@Override
				public void onDragDown()
				{
					if (GamePreferences.GET_ESTADO_GAME() == TEstadoGame.FaseEnemies)
					{
						seleccionarAnimacion(TTipoMovimiento.Crouch);
					}
				}
				
				@Override
				public void onTouchMove(float pixelY)
				{
					if (GamePreferences.GET_ESTADO_GAME() == TEstadoGame.FaseBoss && !GamePreferences.IS_SENSOR_ENABLED())
					{
						seleccionarPosicion(pixelY);
					}
				}

				@Override
				public void onTap()
				{
					seleccionarAnimacion(TTipoMovimiento.Attack);
				}
				
			};
		}
		
		if (sensorDetector == null)
		{
			sensorDetector = new SensorDetector(getContext()) {
				
				@Override
				public void onIncreaseXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						seleccionarEstado(TEstadoPersonaje.Subir);
					}
				}

				@Override
				public void onDecreaseXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						seleccionarEstado(TEstadoPersonaje.Bajar);
					}
				}

				@Override
				public void onStabilizeXAngle(double angle)
				{
					if (GamePreferences.IS_SENSOR_ENABLED())
					{
						seleccionarEstado(TEstadoPersonaje.Nada);
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
	}
	
	/* Métodos de Selección de Estado */
	
	public void seleccionarEstado(TEstadoPersonaje estado)
	{
		if (threadActivo)
		{
			renderer.seleccionarEstado(estado);
		}
	}
	
	public void seleccionarPosicion(float pixelY)
	{
		if (threadActivo)
		{
			renderer.seleccionarPosicion(pixelY, getWidth(), getHeight());
		}
	}
	
	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		if (threadActivo)
		{
			if (animacionFinalizada)
			{
				renderer.seleccionarAnimacion(movimiento);
				requestRender();
				
				if (GamePreferences.GET_ESTADO_GAME() == TEstadoGame.FaseEnemies)
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
				
				animacionFinalizada = false;
			}
		}
	}

	public void seleccionarResume()
	{
		if (!threadActivo)
		{
			task.run();
			threadActivo = true;
			
			mListener.onGameScoreChanged(renderer.getPuntuacion());
		}
	}

	public void seleccionarPause()
	{
		if (threadActivo)
		{
			handler.removeCallbacks(task);
			threadActivo = false;
		}
	}
	
	/* Métodos de Guardado de Información */

	public BackgroundDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(BackgroundDataSaved data)
	{
		renderer.restoreData(data);
		requestRender();
	}
}
