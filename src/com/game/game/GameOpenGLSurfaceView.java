package com.game.game;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.BackgroundDataSaved;
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

	/* Constructora */

	public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.GameDetectors, true);

		animacionFinalizada = true;
		contadorCiclos = 0;
	}

	public void setParameters(OnGameListener listener, Personaje personaje, InstanciaNivel nivel)
	{
		mListener = listener;

		renderer = new GameOpenGLRenderer(getContext(), Color.argb(0, 0, 0, 0), personaje, nivel);
		setRenderer(renderer);

		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (renderer.reproducirAnimacion())
				{
					renderer.seleccionarRun();
					animacionFinalizada = true;
				}

				requestRender();

				switch (renderer.isJuegoFinalizado())
				{
					case VidaPerdida:
						mListener.onGameLivesChanged(renderer.getVidas());
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(contadorCiclos));
					break;
					case CambioPuntuacion:
						mListener.onGameScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(contadorCiclos));
					break;
					case Nada:
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION(contadorCiclos));
					break;
					case FinJuegoVictoria:
						renderer.pararAnimacion();
						requestRender();
	
						mListener.onGameFinished(renderer.getPuntuacion(), renderer.getVidas());
					break;
					case FinJuegoDerrota:
						renderer.pararAnimacion();
						requestRender();
	
						mListener.onGameFailed(renderer.getPuntuacion(), renderer.getVidas());
					break;
				}
				
				contadorCiclos++;
			}
		};

		renderer.seleccionarRun();
		animacionFinalizada = true;
		threadActivo = false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarJump()
	{
		if (threadActivo)
		{
			if (animacionFinalizada)
			{
				renderer.seleccionarJump();
				requestRender();
				mListener.onGamePlaySound(TTipoMovimiento.Jump);

				animacionFinalizada = false;
			}
		}
	}

	public void seleccionarCrouch()
	{
		if (threadActivo)
		{
			if (animacionFinalizada)
			{
				renderer.seleccionarCrouch();
				requestRender();
				mListener.onGamePlaySound(TTipoMovimiento.Crouch);

				animacionFinalizada = false;
			}
		}
	}

	public void seleccionarAttack()
	{
		if (threadActivo)
		{
			if (animacionFinalizada)
			{
				renderer.seleccionarAttack();
				requestRender();
				mListener.onGamePlaySound(TTipoMovimiento.Attack);

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
