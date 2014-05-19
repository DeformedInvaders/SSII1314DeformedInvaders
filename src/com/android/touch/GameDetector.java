package com.android.touch;

import android.view.MotionEvent;

import com.main.model.GamePreferences;

public abstract class GameDetector
{
	private float lastPixelY;
	private long lastTap;
	
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getActionMasked();

		long time = System.currentTimeMillis();
		float pixelY = event.getY();
		
		switch (action)
		{
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				onGameDown(time, pixelY);
			break;
			case MotionEvent.ACTION_MOVE:
				onGameMove(time, pixelY);
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				onGameUp(time, pixelY);
			break;
		}

		return true;
	}

	private void onGameDown(long time, float pixelY)
	{
		lastPixelY = pixelY;
		lastTap = time;
	}
	
	private void onGameMove(long time, float pixelY)
	{
		if (Math.abs(lastTap - time) >= GamePreferences.MAX_DURATION_TAP)
		{
			onTouchMove(pixelY);
		}
	}

	private void onGameUp(long time, float pixelY)
	{
		if (pixelY - lastPixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			onDragDown();
		}
		else if (lastPixelY - pixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			onDragUp();
		}
		else if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			onTap();
		}
	}
	
	/* Métodos abstractos */
	
	public abstract void onDragUp();
	public abstract void onTouchMove(float pixelY);
	public abstract void onDragDown();
	public abstract void onTap();
}
