package com.android.touch;

import android.view.MotionEvent;

import com.lib.math.Intersector;
import com.lib.math.Vector2;
import com.main.model.GamePreferences;

public abstract class RotateDetector
{
	private boolean started;
	private float fijoPixelX, fijoPixelY, lastPixelX, lastPixelY;

	/* Constructora */

	public RotateDetector()
	{
		started = false;
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getActionMasked();

		float pixelX1 = event.getX(0);
		float pixelY1 = event.getY(0);
		
		float pixelX2 = event.getX(1);
		float pixelY2 = event.getY(1);

		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				return onRotateDown(pixelX1, pixelY1, pixelX2, pixelY2);
			case MotionEvent.ACTION_MOVE:
				return onRotateMove(pixelX1, pixelY1, pixelX2, pixelY2);
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				return onRotateUp();
		}

		return false;
	}

	private boolean onRotateDown(float pixelX1, float pixelY1, float pixelX2, float pixelY2)
	{
		fijoPixelX = pixelX1;
		fijoPixelY = pixelY1;

		if (Intersector.distancePoints(pixelX1, pixelY1, fijoPixelX, fijoPixelY) > GamePreferences.MAX_DRIFT_ROTATION)
		{
			return false;
		}

		lastPixelX = pixelX2;
		lastPixelY = pixelY2;

		started = true;
		return true;
	}

	private boolean onRotateMove(float pixelX1, float pixelY1, float pixelX2, float pixelY2)
	{
		if (Intersector.distancePoints(pixelX1, pixelY1, fijoPixelX, fijoPixelY) > GamePreferences.MAX_DRIFT_ROTATION)
		{
			return false;
		}

		if (started)
		{
			Vector2 v1 = new Vector2(fijoPixelX - lastPixelX, fijoPixelY - lastPixelY);
			Vector2 v2 = new Vector2(fijoPixelX - pixelX2, fijoPixelY - pixelY2);

			float ang1 = v1.angleRad();
			float ang2 = v2.angleRad();

			onRotate(ang1 - ang2, fijoPixelX, fijoPixelY);

			lastPixelX = pixelX2;
			lastPixelY = pixelY2;

			return true;
		}

		return false;
	}

	private boolean onRotateUp()
	{
		started = false;
		return true;
	}
	
	/* Métodos Abtráctos */
	
	public abstract void onRotate(float ang, float pixelX, float pixelY);
}
