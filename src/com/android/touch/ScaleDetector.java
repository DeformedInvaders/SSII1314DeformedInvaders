package com.android.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.main.model.GamePreferences;

public abstract class ScaleDetector extends SimpleOnScaleGestureListener
{
	private ScaleGestureDetector detector;

	private float lastPixelX, lastPixelY;

	/* Contructora */

	public ScaleDetector(Context context)
	{
		detector = new ScaleGestureDetector(context, this);
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event)
	{
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
		float pixelX = detector.getFocusX();
		float pixelY = detector.getFocusY();
		
		if (factor >= GamePreferences.MAX_SCALE_FACTOR)
		{
			factor = GamePreferences.MAX_SCALE_FACTOR;
		}
		else if (factor <= GamePreferences.MIN_SCALE_FACTOR)
		{
			factor = GamePreferences.MIN_SCALE_FACTOR;
		}
		
		onScale(factor, pixelX, pixelY, lastPixelX, lastPixelY);

		return true;
	}
	
	/* Métodos Abstráctos */
	
	public abstract void onScale(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY);
}
