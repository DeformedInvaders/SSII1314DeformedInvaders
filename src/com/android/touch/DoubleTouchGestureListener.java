package com.android.touch;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.project.main.OpenGLRenderer;

public class DoubleTouchGestureListener extends SimpleOnGestureListener 
{
	private OpenGLRenderer renderer;
	
	public DoubleTouchGestureListener(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
	}
	
    @Override
    public boolean onDown(MotionEvent e) 
    {
        return true;
    }
    
    @Override
    public boolean onDoubleTap(MotionEvent e) 
    {
    	renderer.restore();
        return true;
    }
}
