package com.android.touch;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

import com.project.main.OpenGLRenderer;

public class ScaleGestureListener extends SimpleOnScaleGestureListener
{
	private OpenGLRenderer renderer;

    private float mScaleFactor = 1.0f;
	
    /* SECTION Contructora */
    
	public ScaleGestureListener(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
	}
	
	/* SECTION Métodos Listener onScale */
	
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
        mScaleFactor = detector.getScaleFactor();
        
        if(mScaleFactor > 1.02)
        {
        	mScaleFactor = 0.97f;
        	renderer.zoom(mScaleFactor);
        }
        else if(mScaleFactor < 0.98)
        {
        	mScaleFactor = 1.03f;
        	renderer.zoom(mScaleFactor);
        }
        
        mScaleFactor = 1.0f;
        
        return true;
    }

}
