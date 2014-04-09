package com.creation.paint;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.Esqueleto;
import com.creation.data.Textura;
import com.game.data.TTipoSticker;

public class PaintGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private PaintOpenGLRenderer renderer;

	/* Constructora */

	public PaintGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch);
	}

	public void setParameters(Esqueleto esqueleto)
	{
		// Asignar Renderer al GLSurfaceView
		renderer = new PaintOpenGLRenderer(getContext(), esqueleto);
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

	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarMano()
	{
		renderer.seleccionarMano();
		setEstado(TEstadoDetector.CamaraDetectors);
	}

	public void seleccionarPincel()
	{
		renderer.seleccionarPincel();
		setEstado(TEstadoDetector.SimpleTouch);
	}

	public void seleccionarCubo()
	{
		renderer.seleccionarCubo();
		setEstado(TEstadoDetector.SimpleTouch);
	}

	public int getColorPaleta()
	{
		return renderer.getColorPaleta();
	}

	public void seleccionarColor(int color)
	{
		renderer.seleccionarColor(color);
		setEstado(TEstadoDetector.SimpleTouch);
	}

	public void seleccionarSize(int pos)
	{
		renderer.seleccionarSize(pos);
	}

	public void seleccionarPegatina(int pegatina, TTipoSticker tipo)
	{
		renderer.seleccionarPegatina(pegatina, tipo);
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
	}
}
