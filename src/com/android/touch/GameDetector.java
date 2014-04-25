package com.android.touch;

import android.view.MotionEvent;

import com.android.view.OpenGLSurfaceView;
import com.game.game.GameOpenGLSurfaceView;
import com.main.model.GamePreferences;

public class GameDetector
{
	private float lastPixelY;
	private long lastTap;

	public boolean onTouchEvent(MotionEvent event, OpenGLSurfaceView renderer)
	{
		if (renderer instanceof GameOpenGLSurfaceView)
		{
			GameOpenGLSurfaceView gameRenderer = (GameOpenGLSurfaceView) renderer;

			int action = event.getActionMasked();

			long time = System.currentTimeMillis();
			float pixelY = event.getY();

			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					onGameDown(time, pixelY, gameRenderer);
				break;
				case MotionEvent.ACTION_UP:
					onGameUp(time, pixelY, gameRenderer);
				break;
			}

			return true;
		}

		return false;
	}

	private void onGameDown(long time, float pixelY, GameOpenGLSurfaceView renderer)
	{
		lastPixelY = pixelY;
		lastTap = time;
	}

	private void onGameUp(long time, float pixelY, GameOpenGLSurfaceView renderer)
	{
		if (pixelY - lastPixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			renderer.seleccionarCrouch();
		}
		else if (lastPixelY - pixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			renderer.seleccionarJump();
		}
		else if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			renderer.seleccionarAttack();
		}
	}

}
