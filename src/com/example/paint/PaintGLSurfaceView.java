package com.example.paint;

import com.example.touch.DoubleTouchGestureListener;
import com.example.touch.ScaleGestureListener;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class PaintGLSurfaceView extends GLSurfaceView {

    private final PaintOpenGLRenderer renderer;
    
    private ScaleGestureDetector scaleDectector;
    private GestureDetector gestureDetector;

    public PaintGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new PaintOpenGLRenderer();
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
		
		if(action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_UP) {
			renderer.anyadirPunto(x, y, width, height);			
			requestRender();
		}
		
		scaleDectector.onTouchEvent(event);
		gestureDetector.onTouchEvent(event);
		return true;
	}
}
