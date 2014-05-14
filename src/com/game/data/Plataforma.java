package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Plataforma extends Entidad
{
	private Malla entidad;
	
	private boolean activado;
	private int indiceAnimacion;
	
	public Plataforma(Malla malla)
	{
		entidad = malla;
		activado = false;
		indiceAnimacion = 0;
		
		if (malla.getTipo() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.BurbujaPersonaje;
		}
		else if (malla.getTipo() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.BurbujaBoss;
		}
		else
		{
			tipoEntidad = TTipoEntidad.Nada;
		}
	}
	
	private int indicePlataforma(int indice)
	{
		switch (indice)
		{
			case 0:
				return R.drawable.platform_1;
			case 1:
				return R.drawable.platform_2;
			default:
				return R.drawable.platform_3;
		}
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
		{
			renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indicePlataforma(i), tipoEntidad, i, TTipoSticker.Nada);
		}
		
		width = entidad.getWidth();
		height = entidad.getHeight();
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
		{
			renderer.descargarTexturaRectangulo(tipoEntidad, i, TTipoSticker.Nada);
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (activado)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - 2.0f * getWidth() / 3.0f, 0.0f);
				
				renderer.dibujarTexturaRectangulo(gl, tipoEntidad, indiceAnimacion, TTipoSticker.Nada);
			
			gl.glPopMatrix();
		}
	}

	@Override
	public float getWidth()
	{
		return width;
	}

	@Override
	public float getHeight()
	{
		return height;
	}
	
	/* Métodos de modificación de Información */
	
	public boolean animar()
	{
		indiceAnimacion = (indiceAnimacion + 1) % GamePreferences.NUM_TYPE_PLATFORMS;
		return true;
	}
	
	public void activarPlataforma()
	{
		activado = true;
	}
	
	public void desactivarPlataforma()
	{
		activado = false;
	}
}
