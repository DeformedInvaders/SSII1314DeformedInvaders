package com.example.main;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView
{
    private final MyOpenGLRenderer renderer;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private Rect mContentRect;
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
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{		
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		float width = getWidth();
		float height = getHeight();
				
		if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP)
		{
			renderer.anyadirPunto(x, y, width, height);			
			requestRender();
		}
		
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
	/*TODO*/
	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
	    // Scrolling uses math based on the viewport (as opposed to math using pixels).
	    
	    // Pixel offset is the offset in screen pixels, while viewport offset is the
	    // offset within the current viewport. 
	    float viewportOffsetX = distanceX * renderer.getwidth()
	            / mContentRect.width();
	    float viewportOffsetY = -distanceY * renderer.getheight() 
	            / mContentRect.height();
	    
	    // Updates the viewport, refreshes the display. 
	    setViewportBottomLeft(
	    		renderer.getxLeft() + viewportOffsetX,
	    		renderer.getyBot() + viewportOffsetY);
	    
	    return true;
	}
	
	
	private void setViewportBottomLeft(float x, float y)
	{
	    /*
	     * Constrains within the scroll range. The scroll range is simply the viewport 
	     * extremes (AXIS_X_MAX, etc.) minus the viewport size. For example, if the 
	     * extremes were 0 and 10, and the viewport size was 2, the scroll range would 
	     * be 0 to 8.
	     */

	    float curWidth = renderer.getwidth();
	    float curHeight = renderer.getheight();
	    x = Math.max(renderer.getxLeft(), Math.min(x, renderer.getxRight() - curWidth));
	    y = Math.max(renderer.getyBot() + curHeight, Math.min(y, renderer.getyTop()));

	    renderer.drag(x + curWidth, y - curHeight, x, y);

	    // Invalidates the View to update the display.
	    ViewCompat.postInvalidateOnAnimation(this);
	}
	
	
}
