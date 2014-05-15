package com.game.game;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.opengl.BackgroundDataSaved;
import com.android.touch.GameDetector;
import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTipoMovimiento;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.main.model.GamePreferences;

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
					renderer.seleccionarReposo();
					renderer.seleccionarAnimacion(TTipoMovimiento.Run);
					animacionFinalizada = true;
				}

				requestRender();

				switch (renderer.isGameEnded())
				{
					case VidaPerdidaPersonaje:
						mListener.onGameLivesChanged(renderer.getVidasPersonaje());
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getEstado(), contadorCiclos));
					break;
					case VidaPerdidaBoss:
						mListener.onGameLivesChanged(renderer.getVidasPersonaje(), renderer.getVidasBoss());
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getEstado(), contadorCiclos));
					break;
					case CambioPuntuacion:
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getEstado(), contadorCiclos));
					break;
					case FinFaseEnemigos:
						mListener.onGameEnemiesFinished(renderer.getPuntuacion(), renderer.getVidasPersonaje(), renderer.getVidasBoss());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getEstado(), contadorCiclos));
					break;
					case FinFaseBoss:
						mListener.onGameBossFinished(renderer.getPuntuacion(), renderer.getVidasPersonaje(), renderer.getVidasBoss());
					break;
					case FinJuegoDerrota:	
						mListener.onGameFailed(renderer.getPuntuacion(), renderer.getVidasPersonaje());
					break;
					case Nada:
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(renderer.getEstado(), contadorCiclos));
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

	@Override
	public void setEstado(TEstadoDetector e)
	{
		super.setEstado(e);
		
		if (gameDetector == null)
		{
			gameDetector = new GameDetector();
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event != null)
		{
			gameDetector.onTouchEvent(event, renderer.getEstado(), this);
			requestRender();
	
			return true;
		}
	
		return false;

	}
	
	/* Métodos de Selección de Estado */

	public boolean seleccionarPosicion(float pixelX, float pixelY)
	{
		return renderer.onTouchMove(pixelX, pixelY, getWidth(), getHeight(), 0);
	}

	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		if (threadActivo)
		{
			if (animacionFinalizada)
			{
				renderer.seleccionarAnimacion(movimiento);
				requestRender();
				mListener.onGamePlaySound(movimiento);

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

	public void seleccionarAtacar() 
	{
		renderer.selecionarAtacar();
	}
}
