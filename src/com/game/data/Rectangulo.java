package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.lib.buffer.Dimensiones;
import com.main.model.GamePreferences;

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
		gl.glPushMatrix();
		
			gl.glScalef(GamePreferences.SCREEN_SCALE_FACTOR(), GamePreferences.SCREEN_SCALE_FACTOR(), 0.0f);
			
			renderer.dibujarTexturaRectangulo(gl, tipoEntidad, idEntidad, TTipoSticker.Nada);
			
		gl.glPopMatrix();
	}
	
	@Override
	public float getWidth()
	{
		return width * GamePreferences.SCREEN_SCALE_FACTOR();
	}
	
	@Override
	public float getHeight()
	{
		return height * GamePreferences.SCREEN_SCALE_FACTOR();
	}
}
