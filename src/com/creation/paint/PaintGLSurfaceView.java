package com.creation.paint;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.Esqueleto;
import com.creation.data.Textura;

public class PaintGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private PaintOpenGLRenderer renderer;

    /* SECTION Constructora */
    
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
	
	/* SECTION Métodos de Selección de Estado */
	
	public void seleccionarMano()
	{
		renderer.seleccionarMano();
		setEstado(TTouchEstado.CamaraDetectors);
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
	
	/* SECTION Métodos de Obtención de Información */
	
	public Textura getTextura()
	{
		renderer.seleccionarCaptura();
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
	
	/* SECTION Métodos de Guardado de Información */
	
	public PaintDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(PaintDataSaved data)
	{
		renderer.restoreData(data);
	}
}
