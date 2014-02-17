package com.project.main;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.android.touch.MoveGestureDetector;
import com.android.touch.ScaleGestureListener;

public abstract class OpenGLSurfaceView extends GLSurfaceView
{
	protected static final int NUM_HANDLES = 10;
	
	private Context mContext;
	private TTouchEstado estado;
	
	// Detectores de Gestos
	private ScaleGestureDetector scaleDetector;
	private MoveGestureDetector dragDetector;
    
    public OpenGLSurfaceView(Context context, AttributeSet attrs, TTouchEstado estado)
    {
    	 super(context, attrs);
         
         // Tipo Multitouch
         this.estado = estado;
         this.mContext = context;
         
         // Activar Formato Texturas transparentes
         setEGLConfigChooser(8, 8, 8, 8, 0, 0); 
         getHolder().setFormat(PixelFormat.RGBA_8888);
         
         // Crear Contexto OpenGL ES 1.0
         setEGLContextClientVersion(1);
    }
    
    /* Métodos abstractos a implementar */
	
	public abstract void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	public abstract void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	public abstract void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	public abstract void onMultiTouchEvent();
    
    public void setRenderer(OpenGLRenderer renderer)
    {	
    	// Renderer
    	super.setRenderer(renderer);
    	super.setRenderMode(RENDERMODE_WHEN_DIRTY);
    	
        // Detectors
    	scaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureListener(renderer));
        dragDetector = new MoveGestureDetector(renderer);
    }
    
    public void setEstado(TTouchEstado estado)
    {
    	this.estado = estado;
    }
    
    /* Método abstractos de OnTouchListener */
    
    public boolean onTouch(View v, MotionEvent event)
    {
    	switch(estado)
    	{
    		case SimpleTouch:
    			return onSingleTouch(v, event);
    		case Detectors:
    			return onDetectorsTouch(v, event);
    		case MultiTouch:
    			return onMultiTouch(v, event);
    	}
    	
    	return false;
    }
    
    private boolean onSingleTouch(View v, MotionEvent event)
	{		
		if(event != null)
		{
			int action = event.getActionMasked();
			
			float pixelX = event.getX();
			float pixelY = event.getY();
			
			float screenWidth = getWidth();
			float screenHeight = getHeight();
			
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					onTouchDown(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
				case MotionEvent.ACTION_MOVE:
					onTouchMove(pixelX, pixelY, screenWidth, screenHeight, 0);	
				break;
				case MotionEvent.ACTION_UP:
					onTouchUp(pixelX, pixelY, screenWidth, screenHeight, 0);
				break;
			}
			
			requestRender();	
			return true;
		}
		
		return false;
	}
    
    private boolean onDetectorsTouch(View v, MotionEvent event)
    {
    	if(event != null)
    	{			
			if(event.getPointerCount() == 1)
			{
	    		float pixelX = event.getX();
				float pixelY = event.getY();
				
				float screenWidth = getWidth();
				float screenHeight = getHeight();
				
				dragDetector.onTouchEvent(event, pixelX, pixelY, screenWidth, screenHeight);
			}
			else
			{
				scaleDetector.onTouchEvent(event);
				dragDetector.onStopEvent(event);
			}
			
			requestRender();
			return true;
    	}
    	
    	return false;
    }
    
	private boolean onMultiTouch(View v, MotionEvent event)
	{
		if(event != null)
		{
			int pointCount = event.getPointerCount();
			int action = event.getActionMasked();
			
			float screenWidth = getWidth();
			float screenHeight = getHeight();
			
			if(pointCount > NUM_HANDLES) pointCount = NUM_HANDLES;
			
			for(int i = 0; i < pointCount; i++)
			{
				float pixelX = event.getX(i);
				float pixelY = event.getY(i);
				
				int pointer = event.getPointerId(i);
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
					case MotionEvent.ACTION_MOVE:
						onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);	
					break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						onTouchUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
					break;
				}
			}
			
			onMultiTouchEvent();
			requestRender();
			return true;
		}
		
		return false;
	}
}
