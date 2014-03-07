package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public abstract class Entidad
{
	public static final float DIST_AVANCE = 10.0f;
	
	protected TTipoEntidad tipo;
	protected int id;
	
	protected float posicion = 0.0f;
	
	/* SECTION Métodos abstractos a implementar */
	
	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer);
	public abstract void descargarTextura(OpenGLRenderer renderer);
	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);
	
	public abstract void avanzar();
}
