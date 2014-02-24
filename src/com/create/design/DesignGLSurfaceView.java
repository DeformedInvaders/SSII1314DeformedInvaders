package com.create.design;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TTouchEstado;
import com.project.data.Esqueleto;
import com.project.main.OpenGLSurfaceView;

public class DesignGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private DesignOpenGLRenderer renderer;
 
    /* SECTION Constructora */
    
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new DesignOpenGLRenderer(getContext());
        setRenderer(renderer);
    }
    
    /* SECTION Métodos Abstráctos OpenGLSurfaceView */
	
    @Override
	protected void onTouchDown(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchDown(x, y, width, height, pos);
	}
	
    @Override
	protected void onTouchMove(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchMove(x, y, width, height, pos);
	}
	
    @Override
	protected void onTouchUp(float x, float y, float width, float height, int pos)
	{
		renderer.onTouchUp(x, y, width, height, pos);
	}
	
    @Override
	protected void onMultiTouchEvent()
	{
		
	}
	
    protected void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}

	/* SECTION Métodos de Selección de Estado */
	
	public boolean seleccionarTriangular()
	{
		boolean triangulado = renderer.seleccionarTriangular();
		requestRender();
		return triangulado;
	}
	
	public boolean seleccionarRetoque()
	{
		return renderer.seleccionarRetoque();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		return renderer.getEsqueleto();
	}

	public boolean isPoligonoCompleto()
	{
		return renderer.isPoligonoCompleto();
	}
	
	/* SECTION Métodos de Guardado de Información */
	
	public DesignDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(DesignDataSaved data)
	{
		renderer.restoreData(data);
	}
}
