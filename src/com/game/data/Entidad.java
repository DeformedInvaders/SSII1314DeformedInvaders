package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public abstract class Entidad
{
	public static final float DIST_AVANCE = 20.0f;
	
	protected TTipoEntidad tipo;
	protected int id;
	
	protected float posicionX = 0.0f;
	protected float posicionY = 0.0f;
	
	protected float width = 0.0f;
	protected boolean activo = true;
	protected boolean dibujar = true;
	
	/* SECTION Métodos abstractos a implementar */
	
	public abstract void cargarTextura(GL10 gl, OpenGLRenderer renderer);
	public abstract void descargarTextura(OpenGLRenderer renderer);
	public abstract void dibujar(GL10 gl, OpenGLRenderer renderer);
	
	public abstract void avanzar(OpenGLRenderer renderer, boolean primerosCiclos);
	
	public int getId()
	{
		return id;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public float getPosicion()
	{
		return posicionX;
	}
	
	public TTipoEntidad getTipo()
	{
		return tipo;
	}
	
	public boolean isActivo()
	{
		return activo;
	}
	
	public void setInactivo()
	{
		activo = false;
	}
}
