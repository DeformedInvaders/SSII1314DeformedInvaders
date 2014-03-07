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
		indiceTexturaFondo = R.drawable.background_moon;
		
		// Protagonista
		personaje.cargarTextura(gl, this);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Escala del Juego
		gl.glPushMatrix();
		
			gl.glScalef(0.5f, 0.5f, 0.0f);
			
			// Protagonista
			personaje.dibujar(gl, this);
		
		gl.glPopMatrix();
	}
	
	/* SECTION M�todos abstractos de OpenGLRenderer */
	
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
	
	/* SECTION M�todos de Modificaci�n de Estado */

	public void reproducirAnimacion()
	{
		// FIXME
		personaje.animar();
		personaje.avanzar();
	}
	
	public void pararAnimacion()
	{
		personaje.reposo();
	}
	
	public void seleccionarRun() 
	{
		personaje.mover();
	}
	
	public void seleccionarJump() 
	{
		personaje.saltar();
	}
	
	public void seleccionarCrouch() 
	{
		personaje.agachar();
	}
	
	public void seleccionarAttack() 
	{
		personaje.atacar();
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
	public void saveData()
	{
		personaje.descargarTextura(this);
	}
}
