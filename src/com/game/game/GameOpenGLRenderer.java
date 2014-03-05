package com.game.game;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.Personaje;
import com.project.main.R;

public class GameOpenGLRenderer extends OpenGLRenderer
{	
	// Protagonista
	private Personaje personaje;
	
	/* SECTION Constructura */
	
	public GameOpenGLRenderer(Context context, Personaje p)
	{
        super(context);
        
        personaje = p;
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// BackGround
		indiceTexturaFondo = R.drawable.background_egypt;
		
		// Protagonista
		personaje.cargar(gl, this);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Background
		dibujarTexturaFondo(gl);	
		
		// Protagonista
		personaje.dibujar(gl, this);
	}
	
	/* SECTION Métodos abstractos de OpenGLRenderer */
	
	@Override
	protected boolean reiniciar()
	{
		return false;
	}
	
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
	
	/* SECTION Métodos de Modificación de Estado */

	/* SECTION Métodos de Obtención de Información */
	
	/* SECTION Métodos de Guardado de Información */
	
}
