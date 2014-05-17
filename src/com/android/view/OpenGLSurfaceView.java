package com.android.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.opengl.OpenGLRenderer;
import com.android.touch.DoubleTapDetector;
import com.android.touch.MoveDetector;
import com.android.touch.RotateDetector;
import com.android.touch.ScaleDetector;
import com.android.touch.TEstadoDetector;
import com.main.model.GamePreferences;

public class OpenGLSurfaceView extends GLSurfaceView
{
	private Context mContext;
	private TEstadoDetector estadoDetector;

	private OpenGLRenderer renderer;

	// Detectores de Gestos
	private ScaleDetector scaleDetector;
	private MoveDetector moveDetector;
	private RotateDetector rotateDetector;
	private DoubleTapDetector doubleTapDetector;

	/* Constructora */
	
	public OpenGLSurfaceView(Context context, AttributeSet attrs, boolean transparente)
	{
		this(context, attrs, TEstadoDetector.Nada, transparente);
	}
	
	public OpenGLSurfaceView(Context context, AttributeSet attrs, TEstadoDetector estado, boolean transparente)
	{
		super(context, attrs);

		// Tipo Multitouch
		estadoDetector = estado;
		mContext = context;

		// Activar Formato Texturas transparentes
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		getHolder().setFormat(PixelFormat.RGBA_8888);
		
		// Activar Formato fondo transparente
		if (transparente)
		{
			setZOrderOnTop(true);
		}
		
		// Crear Contexto OpenGL ES 1.0
		setEGLContextClientVersion(1);
	}

	/* Métodos Abstractos */
	
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchPointerDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}

	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchPointerUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onMultiTouchPre(int action, int countPointers)
	{
		return false;
	}
	
	protected boolean onMultiTouchPost(int action)
	{
		return false;
	}
	
	/* Métodos de Modificación del Renderer */

	protected void setRenderer(OpenGLRenderer r)
	{
		renderer = r;

		super.setRenderer(renderer);
		super.setRenderMode(RENDERMODE_WHEN_DIRTY);

		setEstado(estadoDetector);
	}

	public void setEstado(TEstadoDetector e)
	{
		estadoDetector = e;

		if (estadoDetector == TEstadoDetector.CamaraDetectors || estadoDetector == TEstadoDetector.CoordDetectors)
		{
			if (scaleDetector == null)
			{
				scaleDetector = new ScaleDetector(mContext) {

					@Override
					public void onScale(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY)
					{
						if (estadoDetector == TEstadoDetector.CamaraDetectors)
						{
							renderer.camaraZoom(2 * GamePreferences.NULL_SCALE_FACTOR - factor);
						}
						else if (estadoDetector == TEstadoDetector.CoordDetectors)
						{
							renderer.pointsZoom(factor, pixelX, pixelY, lastPixelX, lastPixelY, getWidth(), getHeight());
						}
					}
				};
			}

			if (moveDetector == null)
			{
				moveDetector = new MoveDetector() {

					@Override
					public void onDragDown(float pixelX, float pixelY, float lastPixelX, float lastPixelY)
					{
						if (estadoDetector == TEstadoDetector.CamaraDetectors)
						{
							renderer.salvarCamara();
						}
					}

					@Override
					public void onDragMove(float pixelX, float pixelY, float lastPixelX, float lastPixelY)
					{
						if (estadoDetector == TEstadoDetector.CamaraDetectors)
						{
							renderer.recuperarCamara();
							renderer.camaraDrag(pixelX, pixelY, lastPixelX, lastPixelY, getWidth(), getHeight());
						}
						else if (estadoDetector == TEstadoDetector.CoordDetectors)
						{
							renderer.pointsDrag(pixelX, pixelY, lastPixelX, lastPixelY, getWidth(), getHeight());
						}
					}};
			}

			if (rotateDetector == null)
			{
				rotateDetector = new RotateDetector() {

					@Override
					public void onRotate(float ang, float pixelX, float pixelY)
					{
						if (estadoDetector == TEstadoDetector.CoordDetectors)
						{
							renderer.pointsRotate(ang, pixelX, pixelY, getWidth(), getHeight());
						}
					}
				};
			}
			
			if (doubleTapDetector == null)
			{
				doubleTapDetector = new DoubleTapDetector() {

					@Override
					public void onDoubleTap()
					{
						if (estadoDetector == TEstadoDetector.CamaraDetectors)
						{
							renderer.camaraRestore();
						}
						else if (estadoDetector == TEstadoDetector.CoordDetectors)
						{
							renderer.pointsRestore();
						}
					}
				};
			}
		}
	}

	/* Métodos Listener onTouch */

	public boolean onTouch(View v, MotionEvent event)
	{
		switch (estadoDetector)
		{
			case SimpleTouch:
				return onSingleTouch(v, event);
			case MultiTouch:
				return onMultiTouch(v, event);
			case CamaraDetectors:
				return onDetectorsTouch(v, event);
			case CoordDetectors:
				return onDetectorsTouch(v, event);
			default:
				return false;
		}
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
				case MotionEvent.ACTION_POINTER_DOWN:
					onTouchDown(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_MOVE:
					onTouchMove(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
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
			if (event.getPointerCount() == 1)
			{
				if (!doubleTapDetector.onTouchEvent(event))
				{
					moveDetector.onTouchEvent(event);
				}
			}
			else if (event.getPointerCount() == 2)
			{
				if (rotateDetector.onTouchEvent(event))
				{
					doubleTapDetector.onStopEvent();
					moveDetector.onStopEvent();
				}
				else
				{
					scaleDetector.onTouchEvent(event);
					moveDetector.onStopEvent();
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
			
			onMultiTouchPre(action, pointCount);
			
			for (int i = 0; i < pointCount; i++)
			{
				float pixelX = event.getX(i);
				float pixelY = event.getY(i);

				int pointer = event.getPointerId(i);

				switch (action)
				{
					case MotionEvent.ACTION_DOWN:
						activo |= onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_POINTER_DOWN:
						activo |= onTouchPointerDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_MOVE:
						activo |= onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_UP:
						activo |= onTouchUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_POINTER_UP:
						activo |= onTouchPointerUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
				}
			}

			if (activo)
			{
				onMultiTouchPost(action);
			}

			requestRender();
			return true;
		}

		return false;
	}
}
