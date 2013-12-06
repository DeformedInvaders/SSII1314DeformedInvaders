package com.example.animation;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.main.Esqueleto;

public class JumpGLSurfaceView extends GLSurfaceView{

private final JumpOpenGLRenderer renderer;
	
	public JumpGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);
        
        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new JumpOpenGLRenderer(context);
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
