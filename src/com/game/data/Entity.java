package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.main.model.GamePreferences;

public abstract class Entity
{
	protected TTypeEntity tipoEntidad;
	protected int idEntidad;
	protected int texturaEntidad;

	protected float width = 0.0f;
	protected float height = 0.0f;

	/* Métodos abstractos a implementar */

	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context);

	public abstract void descargarTextura(OpenGLRenderer renderer);

	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);

	public boolean animar()
	{ 
		return false;
	}

	/* Métodos públicos */
	
	public int getId()
	{
		return idEntidad;
	}

	public TTypeEntity getTipo()
	{
		return tipoEntidad;
	}
	
	public int getIndiceTextura()
	{
		return texturaEntidad;
	}
	
	public float getWidth()
	{
		return width * GamePreferences.GAME_SCALE_FACTOR(tipoEntidad);
	}
	
	public float getHeight()
	{
		return height * GamePreferences.GAME_SCALE_FACTOR(tipoEntidad);
	}
}
