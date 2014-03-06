package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public abstract class Entidad
{
	protected float posicion = 0.0f;
	
	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer);
	public abstract void descargarTextura(OpenGLRenderer renderer);
	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);
}
