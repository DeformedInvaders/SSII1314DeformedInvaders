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
		else if(action == MotionEvent.ACTION_MOVE)
		{
			float dx = x - lastX;
			float dy = height - y - lastY;
			
			//Desplazamiento a la izquierda
			if(dx > 7) {
				dx = -0.02f;
			}
			//Desplazamiento a la derecha
			else if(dx < -7) {
				dx = 0.02f;
			}
			//Desplazamiento vertical
			else {
				dx = 0f;
			}
			//Desplazamineto abajo
			if(dy > 7) {
				dy = -0.02f;
			}
			//Desplazamiento arriba
			else if(dy < -7) {
				dy = 0.02f;
			}
			//Desplazamiento horizontal
			else { 
				dy = 0f;
			}
			
			renderer.drag(width, height, dx, dy);
			
			lastX = x;
			lastY = height - y;
		}
		else if (action == MotionEvent.ACTION_UP)
		{
			
		}
    	
        return true;
    }
}
