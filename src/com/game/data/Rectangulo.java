package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public abstract class Rectangulo extends Entidad
{
	protected int textura;
	
	/* SECTION M�todos abstractos de Entidad */
	
	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer)
	{
		width = renderer.cargarTexturaRectangulo(gl, textura, tipo, id, 0);
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipo, id, 0);
		
		width = 0;
	}
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarTexturaRectangulo(gl, tipo, id, 0, 0.5f, 0.5f);
	}
}
