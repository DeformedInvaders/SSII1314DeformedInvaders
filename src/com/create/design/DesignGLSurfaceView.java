package com.create.design;

import android.content.Context;
import android.util.AttributeSet;

import com.project.data.Esqueleto;
import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class DesignGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private final DesignOpenGLRenderer renderer;
 
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new DesignOpenGLRenderer(getContext());
        setRenderer(renderer);
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
		
	}
	
	/* M�todos de modifiaci�n del Renderer */
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}

	/* M�todos de Obtenci�n de Informaci�n */
	
	public boolean pruebaCompleta()
	{
		Esqueleto e = renderer.getEsqueleto();
		requestRender();
		return e != null;
	}
	
	public Esqueleto getEsqueleto()
	{
		return renderer.getEsqueleto();
	}

	public boolean poligonoCompleto()
	{
		return renderer.poligonoCompleto();
	}
}
