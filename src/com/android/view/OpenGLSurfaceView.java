package com.android.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.touch.MoveDetector;
import com.android.touch.RotateDetector;
import com.android.touch.ScaleDetector;
import com.android.touch.TTouchEstado;
import com.android.touch.GameDetector;
import com.project.main.GamePreferences;

public abstract class OpenGLSurfaceView extends GLSurfaceView
{
	private Context mContext;
	private TTouchEstado estado;

	private OpenGLRenderer renderer;

	// Detectores de Gestos
	private ScaleDetector scaleDetector;
	private MoveDetector moveDetector;
	private RotateDetector rotateDetector;
	private GameDetector gameDetector;

	/* SECTION Constructora */

	public OpenGLSurfaceView(Context context, AttributeSet attrs, TTouchEstado estado)
	{
		super(context, attrs);

		// Tipo Multitouch
		this.estado = estado;
		this.mContext = context;

		// Activar Formato Texturas transparentes
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		getHolder().setFormat(PixelFormat.RGBA_8888);
		
		// Activar Formato fondo transparente
		setZOrderOnTop(true);

		// Crear Contexto OpenGL ES 1.0
		setEGLContextClientVersion(1);
	}

	/* SECTION Métodos Abstractos */

	protected abstract boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onMultiTouchEvent();

	protected void setRenderer(OpenGLRenderer r)
	{
		renderer = r;

		super.setRenderer(renderer);
		super.setRenderMode(RENDERMODE_WHEN_DIRTY);

		setEstado(estado);
	}

	public void setEstado(TTouchEstado e)
	{
		estado = e;

		if (estado == TTouchEstado.CamaraDetectors || estado == TTouchEstado.CoordDetectors)
		{
			if (scaleDetector == null)
			{
				scaleDetector = new ScaleDetector(mContext, renderer);
			}

			if (moveDetector == null)
			{
				moveDetector = new MoveDetector(renderer);
			}

			if (rotateDetector == null)
			{
				rotateDetector = new RotateDetector(renderer);
			}

			boolean estadoCamara = estado == TTouchEstado.CamaraDetectors;

			scaleDetector.setEstado(estadoCamara);
			moveDetector.setEstado(estadoCamara);
			rotateDetector.setEstado(estadoCamara);
		}
		else if (estado == TTouchEstado.GameDetectors)
		{
			gameDetector = new GameDetector();
		}
	}

	/* SECTION Métodos Listener onTouch */

	public boolean onTouch(View v, MotionEvent event)
	{
		switch (estado)
		{
			case SimpleTouch:
				return onSingleTouch(v, event);
			case MultiTouch:
				return onMultiTouch(v, event);
			case CamaraDetectors:
				return onDetectorsTouch(v, event);
			case CoordDetectors:
				return onDetectorsTouch(v, event);
			case GameDetectors:
				return onGameDetectors(v, event);
		}

		return false;
	}

	private boolean onSingleTouch(View v, MotionEvent event)
	{
		if (event != null)
		{
			int action = event.getActionMasked();

			float pixelX = event.getX();
			float pixelY = event.getY();

			float screenWidth = getWidth();
			float screenHeight = getHeight();
			
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					onTouchDown(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_MOVE:
					onTouchMove(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_UP:
					onTouchUp(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
			}

			requestRender();
			return true;
		}

		return false;
	}

	private boolean onDetectorsTouch(View v, MotionEvent event)
	{
		if (event != null)
		{
			float screenWidth = getWidth();
			float screenHeight = getHeight();

			if (event.getPointerCount() == 1)
			{
				moveDetector.onTouchEvent(event, screenWidth, screenHeight);
			}
			else if (event.getPointerCount() == 2)
			{
				if (rotateDetector.onTouchEvent(event, screenWidth, screenHeight))
				{
					moveDetector.onStopEvent(event);
				}
				else
				{
					scaleDetector.onTouchEvent(event, screenWidth, screenHeight);
					moveDetector.onStopEvent(event);
				}
			}

			requestRender();
			return true;
		}

		return false;
	}

	private boolean onMultiTouch(View v, MotionEvent event)
	{
		if (event != null)
		{
			boolean activo = false;
			int pointCount = event.getPointerCount();
			int action = event.getActionMasked();

			float screenWidth = getWidth();
			float screenHeight = getHeight();

			if (pointCount > GamePreferences.NUM_HANDLES)
			{
				pointCount = GamePreferences.NUM_HANDLES;
			}

			for (int i = 0; i < pointCount; i++)
			{
				float pixelX = event.getX(i);
				float pixelY = event.getY(i);

				int pointer = event.getPointerId(i);

				switch (action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						activo |= onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_MOVE:
						activo |= onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						activo |= onTouchUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
				}
			}

			if (action == MotionEvent.ACTION_MOVE && activo)
			{
				onMultiTouchEvent();
			}

			requestRender();
			return true;
		}

		return false;
	}

	private boolean onGameDetectors(View v, MotionEvent event)
	{
		if (event != null)
		{
			gameDetector.onTouchEvent(event, this);
			requestRender();

			return true;
		}

		return false;
	}
}
