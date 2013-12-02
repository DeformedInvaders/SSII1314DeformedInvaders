package com.example.design;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.main.Esqueleto;
import com.example.touch.DoubleTouchGestureListener;
import com.example.touch.DragGestureDetector;
import com.example.touch.ScaleGestureListener;

public class DesignGLSurfaceView extends GLSurfaceView
{
    private final DesignOpenGLRenderer renderer;
    
    private ScaleGestureDetector scaleDectector;
    private GestureDetector doubleTouchDetector;
    private DragGestureDetector dragDetector;
 
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new DesignOpenGLRenderer();
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        scaleDectector = new ScaleGestureDetector(context, new ScaleGestureListener(renderer));
        doubleTouchDetector = new GestureDetector(context, new DoubleTouchGestureListener(renderer));
        dragDetector = new DragGestureDetector(renderer);
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
		else 
		{
			if(event.getPointerCount() == 1)
			{		
				dragDetector.onTouchEvent(event, x, y, width, height);
				doubleTouchDetector.onTouchEvent(event);
			}
			else
			{
				scaleDectector.onTouchEvent(event);
			}
		}
		
		requestRender();
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
