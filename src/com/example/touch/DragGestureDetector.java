package com.example.touch;

import android.view.MotionEvent;

import com.example.main.OpenGLRenderer;

public class DragGestureDetector
{
	private OpenGLRenderer renderer;
	
    private float lastX;
	private float lastY;
	
	public DragGestureDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
	}
	
    public boolean onTouchEvent(MotionEvent event, float x, float y, float width, float height) 
    {
    	int action = event.getAction();
    	
		if(action == MotionEvent.ACTION_DOWN)
		{
			lastX = x;
			lastY = height - y;
		}
		else if(action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP)
		{
			float dx = x - lastX;
			float dy = height - y - lastY;
			
			if(dx > 30) {
				dx = -5f;
			}
			else if(dx < -30) {
				dx = 5f;
			}
			else {
				dx = 0f;
			}
			
			if(dy > 30) {
				dy = -5f;
			}
			else if(dy < -30) {
				dy = 5f;
			}
			else { 
				dy = 0f;
			}
			
			renderer.drag(width, height, dx, dy);
		}
    	
        return true;
    }
}
