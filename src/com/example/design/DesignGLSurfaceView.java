package com.example.design;

import com.example.touch.DoubleTouchGestureListener;
import com.example.touch.ScaleGestureListener;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;


public class DesignGLSurfaceView extends GLSurfaceView
{
    private final DesignOpenGLRenderer renderer;
    
    private ScaleGestureDetector scaleDectector;
    private GestureDetector gestureDetector;
    
    private float lastX;
	private float lastY;
 
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
        gestureDetector = new GestureDetector(context, new DoubleTouchGestureListener(renderer));
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{		
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		float width = getWidth();
		float height = getHeight();
		
		if(event.getPointerCount() == 1)
		{
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
					
					renderer.drag(getWidth(), getHeight(), dx, dy);
					
					requestRender();
				}
			}
		}
		scaleDectector.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);
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
		boolean b = renderer.test();
		requestRender();
		return b;
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
