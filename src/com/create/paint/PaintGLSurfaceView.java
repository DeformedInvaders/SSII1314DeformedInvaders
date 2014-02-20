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
        super(context, attrs, TTouchEstado.SimpleTouch);
    }
    
	public void setParameters(Esqueleto esqueleto)
	{
		// Asignar Renderer al GLSurfaceView
        renderer = new PaintOpenGLRenderer(getContext(), esqueleto);
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
	
	public void seleccionarPegatina(int pegatina, int tipo)
	{
		renderer.seleccionarPegatina(pegatina, tipo);
		setEstado(TTouchEstado.SimpleTouch);
		requestRender();
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
	
	/* M�todos de Obtenci�n de Informaci�n */
	
	public Textura getTextura()
	{
		renderer.seleccionarCaptura(getHeight(), getWidth());
		requestRender();
		return renderer.getTextura();
	}
	
	public boolean isBufferSiguienteVacio()
	{
		return renderer.isBufferSiguienteVacio();
	}
	
	public boolean isBufferAnteriorVacio()
	{
		return renderer.isBufferAnteriorVacio();
	}
	
	public boolean isPegatinaAnyadida()
	{
		return renderer.isPegatinaAnyadida();
	}
	
	public boolean isEstadoPincel()
	{
		return renderer.isEstadoPincel();
	}
	
	public boolean isEstadoCubo()
	{
		return renderer.isEstadoCubo();
	}
	
	public boolean isEstadoMover()
	{
		return renderer.isEstadoMover();
	}
	
	public boolean isEstadoPegatinas()
	{
		return renderer.isEstadoPegatinas();
	}
	
	/* M�todos de Guardado de Informaci�n */
	
	public PaintDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(PaintDataSaved data)
	{
		renderer.restoreData(data);
	}
}
