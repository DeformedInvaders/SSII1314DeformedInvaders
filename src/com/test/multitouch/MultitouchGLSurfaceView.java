package com.test.multitouch;

import android.content.Context;
import android.util.AttributeSet;

import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class MultitouchGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private final MultitouchOpenGLRenderer renderer;

    public MultitouchGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.MultiTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new MultitouchOpenGLRenderer(getContext(), NUM_HANDLES);
        setRenderer(renderer);
    }
    
    /* Métodos abstractos OpenGLSurfaceView */
    
    @Override
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		renderer.onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
	}
    
    @Override
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		renderer.onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
	}
	
    @Override
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		renderer.onTouchUp(pixelX, pixelY, screenWidth, screenHeight, pointer);
	}
	
    @Override
	public void onMultiTouchEvent() { }
}
