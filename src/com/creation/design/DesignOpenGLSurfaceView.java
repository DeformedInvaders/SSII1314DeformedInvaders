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
	private DesignOpenGLRenderer mRenderer;

	/* Constructora */

	public DesignOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);

		// Asignar Renderer al GLSurfaceView
		mRenderer = new DesignOpenGLRenderer(getContext(), Color.WHITE);
		setRenderer(mRenderer);
	}

	/* Métodos Abstráctos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchDown(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchMove(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchMove(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchUp(x, y, width, height, pos);
	}

	protected void reiniciar()
	{
		mRenderer.onReset();
		requestRender();
	}

	/* Métodos de Selección de Estado */

	public void selectTriangulate()
	{
		mRenderer.selectTriangulate();
		requestRender();
	}

	public void selectPreparing()
	{
		mRenderer.selectPreparing();
		requestRender();
	}

	/* Métodos de Obtención de Información */

	public boolean isStateDrawing()
	{
		return mRenderer.isStateDrawing();
	}

	public boolean isStateTriangulate()
	{
		return mRenderer.isStateTriangulate();
	}

	public boolean isStatePreparing()
	{
		return mRenderer.isStatePreparing();
	}

	public Skeleton getSkeleton()
	{
		return mRenderer.getSkeleton();
	}

	public boolean isPolygonComplete()
	{
		return mRenderer.isPolygonComplete();
	}
	
	public boolean isPolygonSimplex()
	{
		return mRenderer.isPolygonSimplex();
	}

	public boolean isPolygonReady()
	{
		return mRenderer.isPolygonReady();
	}

	/* Métodos de Guardado de Información */

	public DesignDataSaved saveData()
	{
		return mRenderer.saveData();
	}

	public void restoreData(DesignDataSaved data)
	{
		mRenderer.restoreData(data);
	}
}
