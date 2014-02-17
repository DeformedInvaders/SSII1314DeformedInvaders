package com.view.display;

import android.content.Context;
import android.graphics.Bitmap;
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
    }
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{
		renderer = new DisplayOpenGLRenderer(getContext(), esqueleto, textura);
        setRenderer(renderer);
	}
	
	public void setParameters()
	{
		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}
	
	/* Métodos abstractos OpenGLSurfaceView */
	
	public void onTouchDown(float x, float y, float width, float height, int pos) { }
	
	public void onTouchMove(float x, float y, float width, float height, int pos) { }
	
	public void onTouchUp(float x, float y, float width, float height, int pos) { }
	
	public void onMultiTouchEvent() { }
	
	/* Métodos de Obtención de Información */
	
	public void retoquePantalla()
	{
		renderer.retoquePantalla(getHeight(), getWidth());
		setEstado(TTouchEstado.Detectors);
		
		requestRender();
	}
	
	public Bitmap capturaPantalla()
	{
		renderer.capturaPantalla(getHeight(), getWidth());
		setEstado(TTouchEstado.SimpleTouch);
		
		requestRender();
		return renderer.getCapturaPantalla();
	}
	
	/* Métodos de Guardado de Información */
	
	public void saveData()
	{
		renderer.saveData();
	}
}
