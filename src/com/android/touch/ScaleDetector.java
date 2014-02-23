package com.android.touch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.project.main.OpenGLRenderer;

public class ScaleDetector extends SimpleOnScaleGestureListener
{
	private static final float MAX_SCALE_FACTOR = 1.03f;
	private static final float MIN_SCALE_FACTOR = 0.97f;
	private static final float NULL_SCALE_FACTOR = 1.0f;
	
	private OpenGLRenderer renderer;
	private ScaleGestureDetector detector;
	
	private boolean camara;
    private float factor;
	
    /* SECTION Contructora */
    
	public ScaleDetector(Context context, OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.detector = new ScaleGestureDetector(context, this);
		this.camara = true;
		this.factor = NULL_SCALE_FACTOR;
	}
	
	/* SECTION Métodos de Modificación de Estado */
	
	public void setEstado(boolean camara)
	{
		this.camara = camara;
	}
	
	/* SECTION Métodos Listener onTouch */
	
	public boolean onTouchEvent(MotionEvent event) 
    {
		return detector.onTouchEvent(event);
    }
	
	/* SECTION Métodos Listener onScale */
	
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
        factor = detector.getScaleFactor();
        
        if(factor > MAX_SCALE_FACTOR)
        {
        	factor = MIN_SCALE_FACTOR;
        }
        else if(factor < MIN_SCALE_FACTOR)
        {
        	factor = MAX_SCALE_FACTOR;
        }
        
    	if(camara)
    	{
    		renderer.camaraZoom(factor);
    	}
    	else
    	{
    		renderer.coordZoom(factor);
    	}
        
    	factor = NULL_SCALE_FACTOR;
        
        return true;
    }

}
