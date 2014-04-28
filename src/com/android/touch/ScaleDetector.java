package com.android.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.android.view.OpenGLRenderer;
import com.main.model.GamePreferences;

public class ScaleDetector extends SimpleOnScaleGestureListener
{
	private OpenGLRenderer renderer;
	private ScaleGestureDetector detector;

	private float lastPixelX, lastPixelY;
	private float screenWidth, screenHeight;

	private boolean modoCamara;

	/* Contructora */

	public ScaleDetector(Context context, OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.detector = new ScaleGestureDetector(context, this);
		this.modoCamara = true;
	}

	/* Métodos de Modificación de Estado */

	public void setEstado(boolean camara)
	{
		this.modoCamara = camara;
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event, float screenWidth, float screenHeight)
	{
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		return detector.onTouchEvent(event);
	}

	/* Métodos Listener onScale */

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

		if (factor >= GamePreferences.MAX_SCALE_FACTOR)
		{
			factor = GamePreferences.MAX_SCALE_FACTOR;
		}
		else if (factor <= GamePreferences.MIN_SCALE_FACTOR)
		{
			factor = GamePreferences.MIN_SCALE_FACTOR;
		}

		if (modoCamara)
		{
			factor = 2 * GamePreferences.NULL_SCALE_FACTOR - factor;

			renderer.camaraZoom(factor);
		}
		else
		{
			float pixelX = detector.getFocusX();
			float pixelY = detector.getFocusY();

			renderer.pointsZoom(factor, pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
		}

		return true;
	}

}
