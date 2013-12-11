package com.example.deform;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.main.Esqueleto;

public class DeformGLSurfaceView extends GLSurfaceView
{
    private final DeformOpenGLRenderer renderer;

    public DeformGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new DeformOpenGLRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		float width = getWidth();
		float height = getHeight();
		
		switch(action)
		{
			case MotionEvent.ACTION_DOWN:
				renderer.onTouchDown(x, y, width, height);
			break;
			case MotionEvent.ACTION_MOVE:
				renderer.onTouchMove(x, y, width, height);	
			break;
			case MotionEvent.ACTION_UP:
				renderer.onTouchUp(x, y, width, height);
			break;
		}

		requestRender();
		return true;
	}
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		renderer.setEsqueleto(esqueleto);
	}

	public void seleccionarAnyadir()
	{
		renderer.seleccionarAnyadir();
	}

	public void seleccionarEliminar()
	{
		renderer.seleccionarEliminar();
	}

	public void seleccionarMover()
	{
		renderer.seleccionarMover();
	}
}
