package com.game.game;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.game.data.InstanciaNivel;
import com.game.data.Personaje;
import com.project.main.GamePreferences;
import com.project.main.R;

public class GameOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private GameOpenGLRenderer renderer;
	private OnGameListener listener;
	private Context mContext;

	private String nombrePersonaje;
	private boolean animacionFinalizada;

	private AudioPlayerManager audioManager;

	private Handler handler;
	private Runnable task;

	private boolean threadActivo;

	/* Constructora */

	public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TTouchEstado.GameDetectors);

		mContext = context;

		animacionFinalizada = true;
	}

	public void setParameters(Personaje p, ExternalStorageManager m, OnGameListener gl, InstanciaNivel l)
	{
		nombrePersonaje = p.getNombre();
		listener = gl;

		renderer = new GameOpenGLRenderer(getContext(), p, l);
		setRenderer(renderer);

		audioManager = new AudioPlayerManager(m) {
			@Override
			public void onPlayerCompletion() { }
		};

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
						listener.onLivesChanged(renderer.getVidas());
						listener.onScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION);
					break;
					case CambioPuntuacion:
						listener.onScoreChanged(renderer.getPuntuacion());
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION);
					break;
					case Nada:
						handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION);
					break;
					case FinJuegoVictoria:
						renderer.pararAnimacion();
						requestRender();
	
						listener.onGameFinished(renderer.getPuntuacion());
					break;
					case FinJuegoDerrota:
						renderer.pararAnimacion();
						requestRender();
	
						listener.onGameFailed();
					break;
				}
			}
		};

		renderer.seleccionarRun();
		animacionFinalizada = true;
		threadActivo = false;
	}

	/* Métodos abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
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

				audioManager.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_jump));

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

				audioManager.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_crouch));

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

				audioManager.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_attack));

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
			
			listener.onScoreChanged(renderer.getPuntuacion());
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

	public void saveData()
	{
		renderer.saveData();
	}
}
