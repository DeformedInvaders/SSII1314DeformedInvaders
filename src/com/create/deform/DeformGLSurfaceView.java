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
        super(context, attrs, TTouchEstado.MultiTouch);
        
        // Asignar Renderer al GLSurfaceView
        renderer = new DeformOpenGLRenderer(context, NUM_HANDLES);
        setRenderer(renderer);
    }
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{
		renderer.setParameters(esqueleto, textura);
	}
	
    /* M�todos abstractos OpenGLSurfaceView */
	
	public void onTouchDown(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchDown(x, y, width, height, pos);
	}
	
	public void onTouchMove(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchMove(x, y, width, height, pos);
	}
	
	public void onTouchUp(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchUp(x, y, width, height, pos);
	}
	
	public void onMultiTouchEvent()
	{
		renderer.onMultiTouchEvent();
	}
	
	/* M�todos de modifiaci�n del Renderer */

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
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}

	/* M�todos de Obtenci�n de Informaci�n */
	
	public boolean handlesVacio()
	{
		return renderer.handlesVacio();
	}
	
	/* M�todos de Guardado de Informaci�n */
	
	public DeformDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(DeformDataSaved data)
	{
		renderer.restoreData(data);
	}
}
