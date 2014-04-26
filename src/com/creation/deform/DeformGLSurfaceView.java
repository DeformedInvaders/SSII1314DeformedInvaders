package com.creation.deform;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.game.data.Personaje;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class DeformGLSurfaceView extends OpenGLSurfaceView
{
	private OnDeformListener mListener;
	private DeformOpenGLRenderer renderer;

	private Handler handler;
	private Runnable task;

	private boolean threadActivo;

	/* Constructora */

	public DeformGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.MultiTouch, false);
	}

	public void setParameters(OnDeformListener listener, Personaje personaje)
	{
		mListener = listener;
		
		renderer = new DeformOpenGLRenderer(getContext(), Color.WHITE, mListener, personaje);
		setRenderer(renderer);

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
					mListener.onAnimationFinished();
					threadActivo = false;
				}
			}
		};

		threadActivo = false;
	}

	/* Métodos Abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchDown(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchPointerDown(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchDown(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchMove(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchMove(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchUp(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchPointerUp(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchPointerUp(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onMultiTouchPre(int action, int countPounter)
	{
		if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_MOVE)
		{
			return renderer.onMultiTouchPreAction(countPounter);
		}
		
		return false;
	}

	@Override
	protected boolean onMultiTouchPost(int action)
	{
		if (action == MotionEvent.ACTION_MOVE)
		{
			return renderer.onMultiTouchPostAction();
		}
		
		return false;
	}

	/* Métodos de modifiación del Renderer */

	public void seleccionarAnyadir()
	{
		renderer.seleccionarAnyadir();
	}

	public void seleccionarEliminar()
	{
		renderer.seleccionarEliminar();
	}

	public void seleccionarMover()
	{
		renderer.seleccionarMover();
	}

	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}

	public void seleccionarGrabado()
	{
		renderer.seleccionarGrabado();
		requestRender();
	}

	public void seleccionarPlay()
	{
		if (!threadActivo)
		{
			renderer.selecionarPlay();
			requestRender();

			mListener.onPlaySound();
			task.run();
			threadActivo = true;
		}
	}

	public void seleccionarAudio()
	{
		renderer.seleccionarAudio();
	}

	public void seleccionarReposo()
	{
		renderer.seleccionarReposo();
	}

	/* Métodos de Obtención de Información */

	public boolean isHandlesVacio()
	{
		return renderer.isHandlesVacio();
	}

	public boolean isEstadoAnyadir()
	{
		return renderer.isEstadoAnyadir();
	}

	public boolean isEstadoEliminar()
	{
		return renderer.isEstadoEliminar();
	}

	public boolean isEstadoDeformar()
	{
		return renderer.isEstadoDeformar();
	}

	public boolean isEstadoGrabacion()
	{
		return renderer.isEstadoGrabacion();
	}

	public boolean isGrabacionReady()
	{
		return renderer.isGrabacionReady();
	}

	public boolean isEstadoAudio()
	{
		return renderer.isEstadoAudio();
	}

	public boolean isEstadoReproduccion()
	{
		return renderer.isEstadoReproduccion();
	}

	public List<VertexArray> getMovimientos()
	{
		return renderer.getMovimientos();
	}

	/* Métodos de Guardado de Información */

	public DeformDataSaved saveData()
	{
		return renderer.saveData();
	}

	public void restoreData(DeformDataSaved data)
	{
		renderer.restoreData(data);
	}
}
