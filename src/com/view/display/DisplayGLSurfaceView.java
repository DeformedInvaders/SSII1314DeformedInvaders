package com.view.display;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.data.Esqueleto;
import com.example.data.Textura;

public class DisplayGLSurfaceView extends GLSurfaceView
{
	// Renderer
    private final DisplayOpenGLRenderer renderer;
    
    public DisplayGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);

        // Asignar Renderer al GLSurfaceView
        renderer = new DisplayOpenGLRenderer(context);
        setRenderer(renderer);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
	
	public void setEsqueleto(Esqueleto esqueleto, Textura textura)
	{
		renderer.setEsqueleto(esqueleto, textura);
	}
}
