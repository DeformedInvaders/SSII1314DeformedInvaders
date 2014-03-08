package com.android.touch;

import android.view.MotionEvent;

import com.android.view.OpenGLSurfaceView;
import com.game.game.GameOpenGLSurfaceView;

public class GameDetector
{
	private static final long MAX_TAP_DURATION = 200;
	private static final long DRAG_DISTANCE = 80;
	
	private float lastPixelY;
	private long lastTap;
	
	public boolean onTouchEvent(MotionEvent event, OpenGLSurfaceView renderer) 
    {
		if(renderer instanceof GameOpenGLSurfaceView)
		{
			GameOpenGLSurfaceView gameRenderer = (GameOpenGLSurfaceView) renderer;
	    	
			int action = event.getActionMasked();
	    	
	    	long time = System.currentTimeMillis();
	    	float pixelY = event.getY();
	    	
	    	switch(action)
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
		if(pixelY - lastPixelY > DRAG_DISTANCE)
		{
			renderer.seleccionarCrouch();
		}
		else if(lastPixelY - pixelY > DRAG_DISTANCE)
		{
			renderer.seleccionarJump();
		}
		else if(Math.abs(lastTap - time) < MAX_TAP_DURATION)
		{
			renderer.seleccionarAttack();
		}
    }

}
