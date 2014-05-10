package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.lib.math.Circle;

public abstract class Entidad
{
	protected TTipoEntidad tipoEntidad;
	protected int idEntidad;
	protected int texturaEntidad;

	protected float width = 0.0f;
	protected float height = 0.0f;
	
	protected Circle area;
	protected Handle handle;

	/* Métodos abstractos a implementar */

	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context);

	public abstract void descargarTextura(OpenGLRenderer renderer);

	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);
	
	public abstract float getWidth();
	
	public abstract float getHeight();

	public boolean animar()
	{ 
		return false;
	}

	/* Métodos públicos */
	
	public int getId()
	{
		return idEntidad;
	}

	public TTipoEntidad getTipo()
	{
		return tipoEntidad;
	}
	
	public int getIndiceTextura()
	{
		return texturaEntidad;
	}
	
	public Circle getArea()
	{
		return area;
	}
	
	public void moverArea(float x, float y)
	{
		area.setPosition(getWidth() / 2.0f + x, getHeight() / 2.0f + y);
	}
	
	public void restaurarArea()
	{
		moverArea(0.0f, 0.0f);
	}
}
