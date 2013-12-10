package com.example.touch;

import com.example.main.OpenGLRenderer;

import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;

public class ScaleGestureListener extends SimpleOnScaleGestureListener
{
	private OpenGLRenderer renderer;

    private float mScaleFactor = 1.0f;
	
	public ScaleGestureListener(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
	}
	
	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		// TODO Eliminar multiplicación para no acumular los factores de zoom.
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
