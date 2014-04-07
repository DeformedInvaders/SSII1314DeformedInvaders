package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;

public abstract class Rectangulo extends Entidad
{
	/* SECTION Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		width = renderer.cargarTexturaRectangulo(gl, textura, tipo, id, TTipoSticker.Nada);
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipo, id, TTipoSticker.Nada);

		width = 0;
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarTexturaRectangulo(gl, tipo, id, TTipoSticker.Nada, 0.5f, 0.5f);
	}
}
