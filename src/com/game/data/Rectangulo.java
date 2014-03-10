package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public abstract class Rectangulo extends Entidad
{
	protected int textura;
	
	/* SECTION Métodos abstractos de Entidad */
	
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
		if(dibujar)
		{
			renderer.dibujarTexturaRectangulo(gl, posicionX, posicionY, tipo, id, 0, 0.5f, 0.5f);
		}
	}
	
	@Override
	public void avanzar(OpenGLRenderer renderer, boolean primerosCiclos)
	{
		posicionX -= DIST_AVANCE;

		if(posicionX < -width)
		{
			dibujar = false;
		}
		else if(posicionX < renderer.getScreenWidth())
		{
			dibujar = true;
		}
		else
		{
			dibujar = false;
		}
	}
}
