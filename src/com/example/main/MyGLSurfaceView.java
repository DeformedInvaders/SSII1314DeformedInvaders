package com.example.main;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyOpenGLRenderer renderer;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    
    public MyGLSurfaceView(Context context, AttributeSet attrs) {
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
	public boolean onTouchEvent(MotionEvent event) {	
		
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		float width = getWidth();
		float height = getHeight();
				
		if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
			renderer.anyadirPunto(x/width, (height - y)/height);			
			requestRender();
		}
		
		mScaleDetector.onTouchEvent(event);
		
		return true;
	}
	
	public void calcularBSpline() {
		renderer.calcularBSpline();
		requestRender();
	}
	
	public void calcularConvexHull() {
		renderer.calcularConvexHull();
		requestRender();
	}
	
	public void calcularDelaunay() {
		renderer.calcularDelaunay();
		requestRender();
	}
	
	public void calcularEarClipping() {
		renderer.calcularEarClipping();
		requestRender();
	}
	
	public void calcularMeshTriangles() {
		renderer.calcularMeshTriangles();
		requestRender();
	}
	
	public void reiniciarPuntos() {
		renderer.reiniciarPuntos();
		requestRender();
	}
	
	public void zoom(float factor) {
		renderer.zoom(factor);
		requestRender();
	}
	
	public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
	        mScaleFactor *= detector.getScaleFactor();

	        // Don't let the object get too small or too large.
	        //mScaleFactor = Math.max(0.95f, Math.min(mScaleFactor, 1.05f));
	        
	        if(mScaleFactor > 1) {
	        	mScaleFactor = 0.97f;
	        }
	        else if(mScaleFactor < 1) {
	        	mScaleFactor = 1.03f;
	        }
	        
	        zoom(mScaleFactor);
	        
	        invalidate();
	        return true;
	    }
	}
}


