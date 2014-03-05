package com.game.game;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.game.data.Personaje;

public class GameGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private GameOpenGLRenderer renderer;
    
	/* SECTION Constructora */
	
    public GameGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);
    }
	
	public void setParameters(Personaje p)
	{
		renderer = new GameOpenGLRenderer(getContext(), p);
        setRenderer(renderer);
	}
	
	/* SECTION Métodos abstractos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	/* SECTION Métodos de Obtención de Información */
	
	/* SECTION Métodos de Guardado de Información */
	
}
