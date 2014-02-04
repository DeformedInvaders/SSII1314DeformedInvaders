package com.view.display;

import android.content.Context;
import android.util.AttributeSet;

import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private DisplayOpenGLRenderer renderer;
    
    public DisplayGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new DisplayOpenGLRenderer(context);
        setRenderer(renderer);
    }
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{
		this.renderer.setParameters(esqueleto, textura);
	}
}
