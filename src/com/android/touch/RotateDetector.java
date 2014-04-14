package com.android.touch;

import android.view.MotionEvent;

import com.android.view.OpenGLRenderer;
import com.lib.math.Intersector;
import com.lib.math.Vector2;
import com.project.model.GamePreferences;

public class RotateDetector
{
	private OpenGLRenderer renderer;

	private boolean camara, started;
	private float fijoPixelX, fijoPixelY, lastPixelX, lastPixelY;

	/* Constructora */

	public RotateDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.camara = true;
		this.started = false;
	}

	/* Métodos de Modificación de Estado */

	public void setEstado(boolean camara)
	{
		this.camara = camara;
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event, float screenWidth, float screenHeight)
	{
		int action = event.getActionMasked();

		float pixelX1 = event.getX(0);
		float pixelY1 = event.getY(0);
		float pixelX2 = event.getX(1);
		float pixelY2 = event.getY(1);

		if (!camara)
		{
			switch (action)
			{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				return rotateOnCoordDown(pixelX1, pixelY1, pixelX2, pixelY2, screenWidth, screenHeight);
			case MotionEvent.ACTION_MOVE:
				return rotateOnCoordMove(pixelX1, pixelY1, pixelX2, pixelY2, screenWidth, screenHeight);
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				return rotateOnCoordUp();
			}
		}

		return false;
	}

	private boolean rotateOnCoordDown(float pixelX1, float pixelY1, float pixelX2, float pixelY2, float screenWith, float screenHeight)
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

	private boolean rotateOnCoordMove(float pixelX1, float pixelY1, float pixelX2, float pixelY2, float screenWidth, float screenHeight)
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

			renderer.coordsRotate(ang1 - ang2, fijoPixelX, fijoPixelY, screenWidth, screenHeight);

			lastPixelX = pixelX2;
			lastPixelY = pixelY2;

			return true;
		}

		return false;
	}

	private boolean rotateOnCoordUp()
	{
		started = false;
		return true;
	}
}
