package com.creation.design;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.Esqueleto;

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

	/* SECTION M�todos Abstr�ctos OpenGLSurfaceView */

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

	protected void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}

	/* SECTION M�todos de Selecci�n de Estado */

	public boolean seleccionarTriangular()
	{
		boolean triangulado = renderer.seleccionarTriangular();
		requestRender();
		return triangulado;
	}

	public void seleccionarRetoque()
	{
		renderer.seleccionarRetoque();
	}

	/* SECTION M�todos de Obtenci�n de Informaci�n */

	public boolean isEstadoDibujando()
	{
		return renderer.isEstadoDibujando();
	}

	public boolean isEstadoTriangulando()
	{
		return renderer.isEstadoTriangulando();
	}

	public boolean isEstadoRetocando()
	{
		return renderer.isEstadoRetocando();
	}

	public Esqueleto getEsqueleto()
	{
		return renderer.getEsqueleto();
	}

	public boolean isPoligonoCompleto()
	{
		return renderer.isPoligonoCompleto();
	}

	public boolean isPoligonoDentroMarco()
	{
		return renderer.isPoligonoDentroMarco();
	}

	/* SECTION M�todos de Guardado de Informaci�n */

	public DesignDataSaved saveData()
	{
		return renderer.saveData();
	}

	public void restoreData(DesignDataSaved data)
	{
		renderer.restoreData(data);
	}
}
