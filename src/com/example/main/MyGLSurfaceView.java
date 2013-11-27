package com.example.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView
{
    private final MyOpenGLRenderer renderer;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float mScaleFactor = 1.f;
    private float lastX;
	private float lastY;
    /*private RectF mCurrentViewport = 
            new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);*/

 
    public MyGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new MyOpenGLRenderer();
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
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
			if(renderer.getEstado() == TEstado.Dibujar)		
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
					
					if(dx > 30)
						dx = -5f;
					else if(dx < -30)
						dx = 5f;
					else
						dx = 0f;
					if(dy > 30)
						dy = -5f;
					else if(dy < -30)
						dy = 5f;
					else 
						dy = 0f;
					
					renderer.drag(getWidth(), getHeight(), dx, dy);
					
					requestRender();
				}
			}
		}
		gestureDetector.onTouchEvent(event);
		mScaleDetector.onTouchEvent(event);
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
	
	public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
	        mScaleFactor *= detector.getScaleFactor();
	        
	        if(mScaleFactor > 1)
	        {
	        	mScaleFactor = 0.97f;
	        }
	        else if(mScaleFactor < 1)
	        {
	        	mScaleFactor = 1.03f;
	        }
	        
	        zoom(mScaleFactor);
	        
	        invalidate();
	        return true;
	    }
	
	}
	private class GestureListener extends GestureDetector.SimpleOnGestureListener 
	{
        @Override
        public boolean onDown(MotionEvent e) 
        {
            return true;
        }
        
        @Override
        public boolean onDoubleTap(MotionEvent e) 
        {
        	restore();
            return true;
        }
	}
}
