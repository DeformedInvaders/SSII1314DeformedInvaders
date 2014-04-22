package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.lib.buffer.Dimensiones;
import com.project.model.GamePreferences;

public abstract class Rectangulo extends Entidad
{
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensiones dim = renderer.cargarTexturaRectangulo(gl, texturaEntidad, tipoEntidad, idEntidad, TTipoSticker.Nada);
		if(dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipoEntidad, idEntidad, TTipoSticker.Nada);

		width = 0;
		height = 0;
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarTexturaRectangulo(gl, tipoEntidad, idEntidad, TTipoSticker.Nada, GamePreferences.GAME_SCALE_FACTOR(), GamePreferences.GAME_SCALE_FACTOR());
	}
}
