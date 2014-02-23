package com.android.touch;

import android.view.MotionEvent;

import com.project.main.OpenGLRenderer;

public class MoveDetector
{
	private static final long MAX_TAP_DURATION = 300;
	
	private OpenGLRenderer renderer;
	
	private boolean started;
	private boolean bloqueado;
	
    private float lastPixelX;
	private float lastPixelY;
	
	private long lastTap;
	
	private boolean camara;
	
	/* SECTION Constructora */
	
	public MoveDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.started = false;
		this.bloqueado = false;
		this.lastTap = System.currentTimeMillis();
		this.camara = true;
	}
	
	/* SECTION Métodos de Modificación de Estado */
	
	public void setEstado(boolean camara)
	{
		this.camara = camara;
	}
	
	/* SECTION Métodos Listener onTouch */
	
    public boolean onTouchEvent(MotionEvent event, float pixelX, float pixelY, float screenWidth, float screenHeight) 
    {
    	int action = event.getActionMasked();
    	
    	if(!bloqueado)
    	{	    
    		switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					if(camara)
		    		{
						dragOnCamaraDown(pixelX, pixelY, screenWidth, screenHeight);
		    		}
					else
					{
						dragOnCoordDown(pixelX, pixelY, screenWidth, screenHeight);
					}
				break;
				case MotionEvent.ACTION_MOVE:
					if(camara)
		    		{
						dragOnCamaraMove(pixelX, pixelY, screenWidth, screenHeight);
		    		}
					else
					{
						dragOnCoordMove(pixelX, pixelY, screenWidth, screenHeight);
					}
				break;
				case MotionEvent.ACTION_UP:
					if(camara)
		    		{
						dragOnCamaraUp();
		    		}
					else
					{
						dragOnCoordUp();
					}
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
    	dragOnCamaraUp();
    	bloqueado = true;
    	
    	return true;
    }
    
    private void dragOnCamaraDown(float pixelX, float pixelY, float screenWith, float screenHeight)
    {
    	if(!started)
    	{	    	
    		long time = System.currentTimeMillis();
    		
    		if(Math.abs(lastTap - time) < MAX_TAP_DURATION)
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
    
    private void dragOnCamaraMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
    {
    	if(started)
    	{
	    	renderer.recuperarCamara();
			renderer.drag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
    	}
    }
    
    private void dragOnCamaraUp()
    {
    	if(started)
    	{	    	
	    	started = false;
    	}
    }
    
    private void dragOnCoordDown(float pixelX, float pixelY, float screenWith, float screenHeight)
    {
    	if(!started)
    	{	    	
			// Drag
	    	lastPixelX = pixelX;
	    	lastPixelY = pixelY;
	    	
			started = true;
    	}
    }
    
    private void dragOnCoordMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
    {
    	if(started)
    	{
			renderer.drag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
    	}
    }
    
    private void dragOnCoordUp()
    {
    	if(started)
    	{	    	
	    	started = false;
    	}
    }

}
