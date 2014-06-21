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
	private PaintOpenGLRenderer mRenderer;

	/* Constructora */

	public PaintOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);
	}

	public void setParameters(Character personaje)
	{		
		// Asignar Renderer al GLSurfaceView
		mRenderer = new PaintOpenGLRenderer(getContext(), Color.WHITE, personaje);
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

	/* Métodos de Selección de Estado */

	public void selectNothing()
	{
		mRenderer.selectNothing();
		setDetectorState(TStateDetector.SimpleTouch);
		requestRender();
	}
	
	public void selectHand()
	{
		mRenderer.selectHand();
		setDetectorState(TStateDetector.CamaraDetectors);
		requestRender();
	}

	public void selectPencil()
	{
		mRenderer.selectPencil();
		setDetectorState(TStateDetector.SimpleTouch);
		requestRender();
	}

	public void selectBucket()
	{
		mRenderer.selectBucket();
		setDetectorState(TStateDetector.SimpleTouch);
		requestRender();
	}

	public void selectColor(int color)
	{
		mRenderer.selectColor(color);
		setDetectorState(TStateDetector.SimpleTouch);
	}

	public void selectSize(TTypeSize size)
	{
		mRenderer.selectSize(size);
	}

	public void addSticker(int sticker, TTypeSticker type)
	{
		mRenderer.addSticker(sticker, type);
		setDetectorState(TStateDetector.SimpleTouch);
		requestRender();
	}
	
	public void deleteSticker(TTypeSticker type)
	{
		mRenderer.deleteSticker(type);
		setDetectorState(TStateDetector.SimpleTouch);
		requestRender();		
	}
	
	public void editSticker(TTypeSticker type)
	{
		mRenderer.editSticker(type);
		setDetectorState(TStateDetector.CoordDetectors);
		requestRender();
	}

	public void prevAction()
	{
		mRenderer.prevAction();
		requestRender();
	}

	public void nextAction()
	{
		mRenderer.nextAction();
		requestRender();
	}

	public void selectReset()
	{
		mRenderer.onReset();
		requestRender();
	}

	/* Métodos de Obtención de Información */

	public boolean isNextBufferEmpty()
	{
		return mRenderer.isNextBufferEmpty();
	}

	public boolean isPrevBufferEmpty()
	{
		return mRenderer.isPrevBufferEmpty();
	}

	public boolean isStickerAdded()
	{
		return mRenderer.isStickerAdded();
	}

	public boolean isStatePencil()
	{
		return mRenderer.isStatePencil();
	}

	public boolean isStateBucket()
	{
		return mRenderer.isStateBucket();
	}

	public boolean isStateHand()
	{
		return mRenderer.isStateHand();
	}

	public boolean isStateSticker()
	{
		return mRenderer.isStateSticker();
	}
	
	public Texture getTexture()
	{
		mRenderer.seleccionarCaptura();
		requestRender();
		return mRenderer.getTexture();
	}

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
		return mRenderer.saveData();
	}

	public void restoreData(PaintDataSaved data)
	{
		mRenderer.restoreData(data);
		requestRender();
	}
}
