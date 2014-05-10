package com.character.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTipoMovimiento;
import com.game.data.Personaje;
import com.main.model.GamePreferences;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private OnDisplayListener mListener;
	private DisplayOpenGLRenderer renderer;

	private boolean personajeCargado, movimientoAleatorio;

	private Handler handler;
	private Runnable task;

	private boolean threadActivo;

	/* Constructora */

	public DisplayGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch, true);

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

	public void setParameters(OnDisplayListener listener, Personaje personaje, boolean random)
	{
		mListener = listener;
		movimientoAleatorio = random;
		personajeCargado = true;

		renderer = new DisplayOpenGLRenderer(getContext(), personaje);
		setRenderer(renderer);
	}

	public void setParameters()
	{
		movimientoAleatorio = false;
		personajeCargado = false;

		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}

	/* Métodos abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (movimientoAleatorio && personajeCargado)
		{
			int animacion = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_MOVIMIENTOS);
			seleccionarAnimacion(TTipoMovimiento.values()[animacion]);
			return true;
		}

		return false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		if (!threadActivo)
		{
			renderer.seleccionarAnimacion(movimiento);
			requestRender();
			mListener.onDisplayPlaySound(movimiento);

			task.run();
			threadActivo = true;
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
