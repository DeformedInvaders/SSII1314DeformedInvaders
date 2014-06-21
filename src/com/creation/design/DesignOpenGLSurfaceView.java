package com.creation.design;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.Skeleton;

public class DesignOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private DesignOpenGLRenderer renderer;

	/* Constructora */

	public DesignOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);

		// Asignar Renderer al GLSurfaceView
		renderer = new DesignOpenGLRenderer(getContext(), Color.WHITE);
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

	protected void reiniciar()
	{
		renderer.onReset();
		requestRender();
	}

	/* Métodos de Selección de Estado */

	public void seleccionarTriangular()
	{
		renderer.seleccionarTriangular();
		requestRender();
	}

	public void seleccionarRetoque()
	{
		renderer.seleccionarRetoque();
		requestRender();
	}

	/* Métodos de Obtención de Información */

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

	public Skeleton getEsqueleto()
	{
		return renderer.getEsqueleto();
	}

	public boolean isPoligonoCompleto()
	{
		return renderer.isPoligonoCompleto();
	}
	
	public boolean isPoligonoSimple()
	{
		return renderer.isPoligonoSimple();
	}

	public boolean isPoligonoDentroMarco()
	{
		return renderer.isPoligonoDentroMarco();
	}

	/* Métodos de Guardado de Información */

	public DesignDataSaved saveData()
	{
		return renderer.saveData();
	}

	public void restoreData(DesignDataSaved data)
	{
		renderer.restoreData(data);
	}
}
