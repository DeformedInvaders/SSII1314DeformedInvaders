package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.lib.buffer.Dimensions;
import com.main.model.GamePreferences;

public abstract class Rectangle extends Entity
{
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensions dim = renderer.cargarTexturaRectangulo(gl, texturaEntidad, tipoEntidad, idEntidad, TTypeSticker.Nothing);
		if(dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipoEntidad, idEntidad, TTypeSticker.Nothing);
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();
		
			gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 0.0f);
			
			renderer.dibujarTexturaRectangulo(gl, tipoEntidad, idEntidad, TTypeSticker.Nothing);
			
		gl.glPopMatrix();
	}
}
