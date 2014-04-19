package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.lib.buffer.Dimensiones;

public abstract class Rectangulo extends Entidad
{
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensiones dim = renderer.cargarTexturaRectangulo(gl, textura, tipo, id, TTipoSticker.Nada);
		if(dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipo, id, TTipoSticker.Nada);

		width = 0;
		height = 0;
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarTexturaRectangulo(gl, tipo, id, TTipoSticker.Nada, 0.5f, 0.5f);
	}
}
