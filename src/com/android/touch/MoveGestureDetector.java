package com.android.touch;

import android.view.MotionEvent;

import com.project.main.OpenGLRenderer;

public class MoveGestureDetector
{
	private OpenGLRenderer renderer;
	
	private boolean started;
	private boolean bloqueado;
	
    private float lastPixelX;
	private float lastPixelY;
	
	private long lastTap;
	
	public MoveGestureDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.started = false;
		this.bloqueado = false;
		this.lastTap = System.currentTimeMillis();
	}
	
    public boolean onTouchEvent(MotionEvent event, float pixelX, float pixelY, float screenWidth, float screenHeight) 
    {
    	int action = event.getActionMasked();
    	
    	if(!bloqueado)
    	{	    
    		switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					dragOnDown(pixelX, pixelY, screenWidth, screenHeight);
				break;
				case MotionEvent.ACTION_MOVE:
					dragOnMove(pixelX, pixelY, screenWidth, screenHeight);
				break;
				case MotionEvent.ACTION_UP:
					dragOnUp();
				break;
			}
    	}
    	else
    	{
    		if(action == MotionEvent.ACTION_UP)
			{
				bloqueado = false;
			}
    	}
		
        return true;
    }
    
    public boolean onStopEvent(MotionEvent event)
    {
    	dragOnUp();
    	bloqueado = true;
    	
    	return true;
    }
    
    private void dragOnDown(float pixelX, float pixelY, float screenWith, float screenHeight)
    {
    	if(!started)
    	{	    	
    		long time = System.currentTimeMillis();
    		
    		if(Math.abs(lastTap - time) < 500)
    		{
    			// Double Touch
    			renderer.restore();
    		}
    		else
    		{
    			// Drag
    	    	lastPixelX = pixelX;
    	    	lastPixelY = pixelY;
    	    	
    			started = true;
    			renderer.salvarCamara();
    		}
    		
    		lastTap = time;
    	}
    }
    
    private void dragOnMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
    {
    	if(started)
    	{
	    	renderer.recuperarCamara();
			renderer.drag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
    	}
    }
    
    private void dragOnUp()
    {
    	if(started)
    	{	    	
	    	started = false;
    	}
    }

}
