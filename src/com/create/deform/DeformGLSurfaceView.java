package com.create.deform;

import android.content.Context;
import android.util.AttributeSet;

import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class DeformGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private final DeformOpenGLRenderer renderer;

    public DeformGLSurfaceView(Context context, AttributeSet attrs)
    {
    	//TODO
        super(context, attrs, TTouchEstado.SimpleTouch);
        
        // Asignar Renderer al GLSurfaceView
        renderer = new DeformOpenGLRenderer(context);
        setRenderer(renderer);
    }
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{
		renderer.setParameters(esqueleto, textura);
	}

	public void seleccionarAnyadir()
	{
		renderer.seleccionarAnyadir();
	}

	public void seleccionarEliminar()
	{
		renderer.seleccionarEliminar();
	}

	public void seleccionarMover()
	{
		renderer.seleccionarMover();
	}

	public boolean handlesVacio()
	{
		return renderer.handlesVacio();
	}

	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	public DeformDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(DeformDataSaved data)
	{
		renderer.restoreData(data);
	}
}
