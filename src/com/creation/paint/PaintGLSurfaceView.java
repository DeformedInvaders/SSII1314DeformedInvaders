package com.creation.paint;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTipoSticker;
import com.creation.data.Textura;
import com.game.data.Personaje;

public class PaintGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private PaintOpenGLRenderer renderer;

	/* Constructora */

	public PaintGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch, false);
	}

	public void setParameters(Personaje personaje)
	{
		// Asignar Renderer al GLSurfaceView
		renderer = new PaintOpenGLRenderer(getContext(), Color.WHITE, personaje);
		setRenderer(renderer);
	}

	/* Métodos Abstráctos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchDown(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchMove(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchMove(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchUp(x, y, width, height, pos);
	}

	/* Métodos de Selección de Estado */

	public void seleccionarMano()
	{
		renderer.seleccionarMano();
		setEstado(TEstadoDetector.CamaraDetectors);
		requestRender();
	}

	public void seleccionarPincel()
	{
		renderer.seleccionarPincel();
		setEstado(TEstadoDetector.SimpleTouch);
		requestRender();
	}

	public void seleccionarCubo()
	{
		renderer.seleccionarCubo();
		setEstado(TEstadoDetector.SimpleTouch);
		requestRender();
	}

	public void seleccionarColor(int color)
	{
		renderer.seleccionarColor(color);
		setEstado(TEstadoDetector.SimpleTouch);
	}

	public void seleccionarSize(TTipoSize size)
	{
		renderer.seleccionarSize(size);
	}

	public void seleccionarPegatina(int pegatina, TTipoSticker tipo)
	{
		renderer.seleccionarPegatina(pegatina, tipo);
		setEstado(TEstadoDetector.SimpleTouch);
		requestRender();
	}
	
	public void eliminarPegatina(TTipoSticker tipo)
	{
		renderer.eliminarPegatina(tipo);
		setEstado(TEstadoDetector.SimpleTouch);
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

	/* Métodos de Obtención de Información */

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

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
		return renderer.saveData();
	}

	public void restoreData(PaintDataSaved data)
	{
		renderer.restoreData(data);
		requestRender();
	}
}
