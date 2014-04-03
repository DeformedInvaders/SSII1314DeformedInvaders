package com.android.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.android.view.OpenGLRenderer;

public class ScaleDetector extends SimpleOnScaleGestureListener
{
	private static final float MAX_SCALE_FACTOR = 1.03f;
	private static final float MIN_SCALE_FACTOR = 0.97f;
	private static final float NULL_SCALE_FACTOR = 1.0f;

	private OpenGLRenderer renderer;
	private ScaleGestureDetector detector;

	private float lastPixelX, lastPixelY;
	private float screenWidth, screenHeight;

	private boolean camara;

	/* SECTION Contructora */

	public ScaleDetector(Context context, OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.detector = new ScaleGestureDetector(context, this);
		this.camara = true;
	}

	/* SECTION Métodos de Modificación de Estado */

	public void setEstado(boolean camara)
	{
		this.camara = camara;
	}

	/* SECTION Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event, float screenWidth, float screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		return detector.onTouchEvent(event);
	}

	/* SECTION Métodos Listener onScale */

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector)
	{
		lastPixelX = detector.getFocusX();
		lastPixelY = detector.getFocusY();

		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		float factor = detector.getScaleFactor();

		if (factor >= MAX_SCALE_FACTOR)
		{
			factor = MAX_SCALE_FACTOR;
		}
		else if (factor <= MIN_SCALE_FACTOR)
		{
			factor = MIN_SCALE_FACTOR;
		}

		if (camara)
		{
			factor = 2 * NULL_SCALE_FACTOR - factor;

			renderer.camaraZoom(factor);
		}
		else
		{
			float pixelX = detector.getFocusX();
			float pixelY = detector.getFocusY();

			renderer.coordsZoom(factor, pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
		}

		return true;
	}

}
