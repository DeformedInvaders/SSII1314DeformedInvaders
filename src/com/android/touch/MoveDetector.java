package com.android.touch;

import android.view.MotionEvent;

import com.project.main.OpenGLRenderer;

public class MoveDetector
{
	private static final long MAX_TAP_DURATION = 300;
	
	private OpenGLRenderer renderer;
	
	private boolean camara;
	private boolean bloqueado;
	
    private float lastPixelX, lastPixelY;
	private long lastTap;
	
	/* SECTION Constructora */
	
	public MoveDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
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
    	bloqueado = true;
    	return true;
    }
    
    private void dragOnCamaraDown(float pixelX, float pixelY, float screenWith, float screenHeight)
    {
    	long time = System.currentTimeMillis();
		
		if(Math.abs(lastTap - time) < MAX_TAP_DURATION)
		{
			// Double Touch
			renderer.camaraRestore();
		}
		else
		{
			// Drag
	    	lastPixelX = pixelX;
	    	lastPixelY = pixelY;
	    	
			renderer.salvarCamara();
		}
		
		lastTap = time;
    }
    
    private void dragOnCamaraMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
    {
    	renderer.recuperarCamara();
		renderer.camaradrag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
    }
    
    private void dragOnCoordDown(float pixelX, float pixelY, float screenWith, float screenHeight)
    {
    	lastPixelX = pixelX;
    	lastPixelY = pixelY;
    }
    
    private void dragOnCoordMove(float pixelX, float pixelY, float screenWidth, float screenHeight)
    {
    	renderer.coordsDrag(pixelX, pixelY, lastPixelX, lastPixelY, screenWidth, screenHeight);
		
		lastPixelX = pixelX;
    	lastPixelY = pixelY;
    }
}
