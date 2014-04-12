package com.character.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.InternalStorageManager;
import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.project.main.GamePreferences;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private DisplayOpenGLRenderer renderer;

	private TTipoDisplay tipoDisplay;

	private String nombre;
	private boolean personajeCargado;

	private InternalStorageManager internalManager;
	private AudioPlayerManager player;

	private Handler handler;
	private Runnable task;

	private boolean threadActivo;

	/* Constructora */

	public DisplayGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch);

		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (!renderer.reproducirAnimacion())
				{
					requestRender();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION());
				}
				else
				{
					renderer.seleccionarReposo();
					threadActivo = false;
				}
			}
		};

		threadActivo = false;
	}

	public void setParameters(Personaje p, InternalStorageManager m, TTipoDisplay e)
	{
		nombre = p.getNombre();
		internalManager = m;
		tipoDisplay = e;
		personajeCargado = true;

		renderer = new DisplayOpenGLRenderer(getContext(), p);
		setRenderer(renderer);

		player = new AudioPlayerManager(internalManager) {
			@Override
			public void onPlayerCompletion() { }
		};
	}

	public void setParameters(TTipoDisplay e)
	{
		personajeCargado = false;
		tipoDisplay = e;

		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}

	/* Métodos abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (tipoDisplay == TTipoDisplay.Main)
		{
			if (personajeCargado)
			{
				int animacion = (int) Math.floor(Math.random() * 4);

				switch (animacion)
				{
					case 0:
						seleccionarRun();
					break;
					case 1:
						seleccionarJump();
					break;
					case 2:
						seleccionarCrouch();
					break;
					case 3:
						seleccionarAttack();
					break;
				}

				return true;
			}
		}

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

	public void seleccionarRun()
	{
		if (!threadActivo)
		{
			renderer.seleccionarRun();
			requestRender();

			task.run();
			threadActivo = true;
			player.startPlaying(nombre, TTipoMovimiento.Run);
		}
	}

	public void seleccionarJump()
	{
		if (!threadActivo)
		{
			renderer.seleccionarJump();
			requestRender();

			task.run();
			threadActivo = true;
			player.startPlaying(nombre, TTipoMovimiento.Jump);
		}
	}

	public void seleccionarCrouch()
	{
		if (!threadActivo)
		{
			renderer.seleccionarCrouch();
			requestRender();

			task.run();
			threadActivo = true;
			player.startPlaying(nombre, TTipoMovimiento.Crouch);
		}
	}

	public void seleccionarAttack()
	{
		if (!threadActivo)
		{
			renderer.seleccionarAttack();
			requestRender();

			task.run();
			threadActivo = true;
			player.startPlaying(nombre, TTipoMovimiento.Attack);
		}
	}

	public void seleccionarRetoque()
	{
		renderer.seleccionarRetoque(getHeight(), getWidth());
		setEstado(TEstadoDetector.CamaraDetectors);

		requestRender();
	}

	public void seleccionarTerminado()
	{
		renderer.seleccionarTerminado();
		requestRender();
	}

	/* Métodos de Obtención de Información */

	public boolean isEstadoReposo()
	{
		return renderer.isEstadoReposo();
	}

	public boolean isEstadoRetoque()
	{
		return renderer.isEstadoRetoque();
	}

	public boolean isEstadoTerminado()
	{
		return renderer.isEstadoTerminado();
	}

	public boolean isEstadoAnimacion()
	{
		return renderer.isEstadoAnimacion();
	}

	public Bitmap getCapturaPantalla()
	{
		renderer.seleccionarCaptura();
		setEstado(TEstadoDetector.SimpleTouch);

		requestRender();
		return renderer.getCapturaPantalla();
	}

	/* Métodos de Guardado de Información */

	public void saveData()
	{
		renderer.saveData();
	}
}
