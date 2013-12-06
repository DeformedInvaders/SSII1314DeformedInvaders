package com.example.animation;

import com.example.main.Esqueleto;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class DownGLSurfaceView extends GLSurfaceView {

	private final DownOpenGLRenderer renderer;
	
	public DownGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);
        
        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new DownOpenGLRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

	public void setEsqueleto(Esqueleto e) {
		// TODO Auto-generated method stub
		renderer.setEsqueleto(e);
		requestRender();
	}
}
