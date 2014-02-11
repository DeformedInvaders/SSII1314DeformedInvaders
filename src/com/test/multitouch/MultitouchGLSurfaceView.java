package com.test.multitouch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.OnTouchListener;

import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class MultitouchGLSurfaceView extends OpenGLSurfaceView implements OnTouchListener
{
	// Renderer
    private final MultitouchOpenGLRenderer renderer;

    public MultitouchGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.MultiTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new MultitouchOpenGLRenderer(getContext(), NUM_HANDLES);
        setRenderer(renderer);
        setOnTouchListener(this);
    }
    
    /* Métodos abstractos OpenGLSurfaceView */
	
	public void onTouchDown(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchDown(x, y, width, height, pos);
	}
	
	public void onTouchMove(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchMove(x, y, width, height, pos);
	}
	
	public void onTouchUp(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchUp(x, y, width, height, pos);
	}
	
	public void onMultiTouchEvent() { }
}
