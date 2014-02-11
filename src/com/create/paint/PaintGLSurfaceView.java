package com.create.paint;

import android.content.Context;
import android.util.AttributeSet;

import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class PaintGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private PaintOpenGLRenderer renderer;

    public PaintGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.Detectors);
    }
    
	public void setParameters(Esqueleto esqueleto)
	{
		// Asignar Renderer al GLSurfaceView
        renderer = new PaintOpenGLRenderer(getContext(), esqueleto);
        setRenderer(renderer);
	}
    
    /* Métodos abstractos OpenGLSurfaceView */
	
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
	
	/* Métodos de modifiación del Renderer */
	
	public void seleccionarMano()
	{
		renderer.seleccionarMano();
		setEstado(TTouchEstado.Detectors);
	}
	
	public void seleccionarPincel()
	{
		renderer.seleccionarPincel();
		setEstado(TTouchEstado.SimpleTouch);
	}
	
	public void seleccionarCubo()
	{
		renderer.seleccionarCubo();
		setEstado(TTouchEstado.SimpleTouch);
	}
	
	public int getColorPaleta()
	{
		return renderer.getColorPaleta();
	}
	
	public void seleccionarColor(int color)
	{
		renderer.seleccionarColor(color);
		setEstado(TTouchEstado.SimpleTouch);
	}
	
	public void seleccionarSize(int pos)
	{
		renderer.seleccionarSize(pos);
	}
	
	public void anteriorAccion()
	{
		renderer.anteriorAccion();
		requestRender();
	}
	
	public void siguienteAccion()
	{
		renderer.siguienteAccion();
		requestRender();
	}
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	/* Métodos de Obtención de Información */
	
	public Textura getTextura()
	{
		return renderer.getTextura();
	}
	
	public void capturaPantalla()
	{
		renderer.capturaPantalla(getHeight(), getWidth());
		requestRender();
	}
	
	public boolean bufferSiguienteVacio()
	{
		return renderer.bufferSiguienteVacio();
	}
	
	public boolean bufferAnteriorVacio()
	{
		return renderer.bufferAnteriorVacio();
	}
}
