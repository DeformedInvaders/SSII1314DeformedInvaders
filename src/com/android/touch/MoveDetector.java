package com.android.touch;

import android.view.MotionEvent;

public abstract class MoveDetector
{
	private boolean locked;

	private float lastPixelX, lastPixelY;

	/* Constructora */

	public MoveDetector()
	{
		locked = false;
	}
	
	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getActionMasked();
		
		if (!locked)
		{
			float pixelX = event.getX();
			float pixelY = event.getY();

			switch (action) 
			{
				case MotionEvent.ACTION_DOWN:
					onDragDown(pixelX, pixelY);
				break;
				case MotionEvent.ACTION_MOVE:
					onDragMove(pixelX, pixelY);
				break;
			}
		}
		else
		{
			if (action == MotionEvent.ACTION_UP)
			{
				locked = false;
			}
		}

		return true;
	}

	public boolean onStopEvent()
	{
		locked = true;
		return true;
	}

	private void onDragDown(float pixelX, float pixelY)
	{
		onDragDown(pixelX, pixelY, lastPixelX, lastPixelY);
		
		lastPixelX = pixelX;
		lastPixelY = pixelY;
	}

	private void onDragMove(float pixelX, float pixelY)
	{
		onDragMove(pixelX, pixelY, lastPixelX, lastPixelY);

		lastPixelX = pixelX;
		lastPixelY = pixelY;
	}
	
	/* Métodos Abstráctos */
	
	public abstract void onDragDown(float pixelX, float pixelY, float lastPixelX, float lastPixelY);
	public abstract void onDragMove(float pixelX, float pixelY, float lastPixelX, float lastPixelY);

}
