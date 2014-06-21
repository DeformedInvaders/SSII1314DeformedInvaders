package com.creation.paint;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTypeSticker;
import com.creation.data.Texture;
import com.game.data.Character;

public class PaintOpenGLSurfaceView extends OpenGLSurfaceView
{	
	// Renderer
	private PaintOpenGLRenderer renderer;

	/* Constructora */

	public PaintOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);
	}

	public void setParameters(Character personaje)
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

	public void seleccionarNada()
	{
		renderer.seleccionarNada();
		setEstado(TStateDetector.SimpleTouch);
		requestRender();
	}
	
	public void seleccionarMano()
	{
		renderer.seleccionarMano();
		setEstado(TStateDetector.CamaraDetectors);
		requestRender();
	}

	public void seleccionarPincel()
	{
		renderer.seleccionarPincel();
		setEstado(TStateDetector.SimpleTouch);
		requestRender();
	}

	public void seleccionarCubo()
	{
		renderer.seleccionarCubo();
		setEstado(TStateDetector.SimpleTouch);
		requestRender();
	}

	public void seleccionarColor(int color)
	{
		renderer.seleccionarColor(color);
		setEstado(TStateDetector.SimpleTouch);
	}

	public void seleccionarSize(TTypeSize size)
	{
		renderer.seleccionarSize(size);
	}

	public void anyadirPegatina(int pegatina, TTypeSticker tipo)
	{
		renderer.seleccionarPegatina(pegatina, tipo);
		setEstado(TStateDetector.SimpleTouch);
		requestRender();
	}
	
	public void eliminarPegatina(TTypeSticker tipo)
	{
		renderer.eliminarPegatina(tipo);
		setEstado(TStateDetector.SimpleTouch);
		requestRender();		
	}
	
	public void editarPegatina(TTypeSticker tipo)
	{
		renderer.editarPegatina(tipo);
		setEstado(TStateDetector.CoordDetectors);
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
	
	public Texture getTextura()
	{
		renderer.seleccionarCaptura();
		requestRender();
		return renderer.getTextura();
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
