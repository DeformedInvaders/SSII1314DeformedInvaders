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
	
	/* SECTION M�todos Renderer */
	
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
	
	/* SECTION M�todos abstractos de OpenGLRenderer */
	
	@Override
	protected void reiniciar() { }
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION M�todos de Modificaci�n de Estado */

	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
}
