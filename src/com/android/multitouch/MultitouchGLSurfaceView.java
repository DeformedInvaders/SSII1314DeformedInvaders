package com.android.multitouch;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MultitouchGLSurfaceView extends GLSurfaceView implements OnTouchListener
{
	// Renderer
    private final MultitouchOpenGLRenderer renderer;
    
    private static final int NUM_HANDLES = 10;

    public MultitouchGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);

        // Asignar Renderer al GLSurfaceView
        renderer = new MultitouchOpenGLRenderer(context, NUM_HANDLES);
        setRenderer(renderer);
        setOnTouchListener(this);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if(event != null)
		{
			int pointCount = event.getPointerCount();
			int action = event.getAction();
			
			float width = getWidth();
			float height = getHeight();
			
			if(pointCount > NUM_HANDLES) pointCount = NUM_HANDLES;
			
			for(int i = 0; i < pointCount; i++)
			{
				float x = event.getX(i);
				float y = event.getY(i);
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
						renderer.onTouchDown(x, y, width, height, i);
					break;
					case MotionEvent.ACTION_MOVE:
						renderer.onTouchMove(x, y, width, height, i);	
					break;
					case MotionEvent.ACTION_UP:
						renderer.onTouchUp(x, y, width, height, i);
					break;
					default:
						return false;
				}
			}
			
			requestRender();
		}
		
		return true;
	}
}
