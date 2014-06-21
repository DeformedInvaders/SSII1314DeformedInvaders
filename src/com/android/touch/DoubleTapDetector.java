package com.android.touch;

import android.view.MotionEvent;

import com.main.model.GamePreferences;

public abstract class DoubleTapDetector
{
	private boolean locked;
	private long lastTap;

	/* Constructora */

	public DoubleTapDetector()
	{
		locked = false;
		lastTap = System.currentTimeMillis();
	}
	
	/* M�todos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getActionMasked();
		
		if (!locked)
		{
			if (action == MotionEvent.ACTION_UP)
			{
				return onDoubleTapUp();
			}
		}
		else
		{
			if (action == MotionEvent.ACTION_UP)
			{
				locked = false;
			}
		}

		return false;
	}

	public boolean onStopEvent()
	{
		locked = true;
		return true;
	}

	private boolean onDoubleTapUp()
	{
		long time = System.currentTimeMillis();

		if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{			
			onDoubleTap();
			return true;
		}

		lastTap = time;
		
		return false;
	}
	
	/* M�todos Abstractos */
	
	public abstract void onDoubleTap();
}
