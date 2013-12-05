package com.example.design;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.main.Esqueleto;

public class DesignGLSurfaceView extends GLSurfaceView
{
    private final DesignOpenGLRenderer renderer;
 
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);
        
        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new DesignOpenGLRenderer(context);
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
		
		if(renderer.getEstado() == TDesignEstado.Dibujar)
		{
			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP)
			{
				renderer.anyadirPunto(x, y, width, height);			
				requestRender();
			}
		}
		
		return true;
	}
	
	public void calcularBSpline()
	{
		renderer.bSpline();
		requestRender();
	}
	
	public void calcularConvexHull()
	{
		renderer.convexHull();
		requestRender();
	}
	
	public void calcularDelaunay()
	{
		renderer.delaunay();
		requestRender();
	}
	
	public void calcularEarClipping()
	{
		renderer.earClipping();
		requestRender();
	}
	
	public void calcularMeshTriangles()
	{
		renderer.meshGenerator();
		requestRender();
	}
	
	public boolean calcularTestSimple()
	{
		boolean b = renderer.testSimple();
		requestRender();
		return b;
	}
	
	public boolean pruebaCompleta()
	{
		Esqueleto e = renderer.test();
		requestRender();
		return e != null;
	}
	
	public Esqueleto getPruebaCompleta()
	{
		return renderer.test();
	}
	
	public void reiniciarPuntos()
	{
		renderer.reiniciarPuntos();
		requestRender();
	}
	
	public void zoom(float factor)
	{
		renderer.zoom(factor);
		requestRender();
	}
	
	public void drag(float dx, float dy)
	{
		float width = getWidth();
		float height = getHeight();
		
		renderer.drag(width, height, dx, dy);
		requestRender();
	}
	
	public void restore()
	{		
		renderer.restore();
		requestRender();
	}
}
