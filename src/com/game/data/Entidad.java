package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;

public abstract class Entidad
{
	protected TTipoEntidad tipo;
	protected int id;
	
	protected int textura;

	protected float width = 0.0f;

	/* Métodos abstractos a implementar */

	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context);

	public abstract void descargarTextura(OpenGLRenderer renderer);

	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);

	public int getId()
	{
		return id;
	}

	public float getWidth()
	{
		return width;
	}

	public TTipoEntidad getTipo()
	{
		return tipo;
	}
	
	public int getIndiceTextura()
	{
		return textura;
	}
}
