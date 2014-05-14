package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Disparo extends Entidad {

	private Malla entidad;
	private boolean activado;
	private int indiceAnimacion;
	private float posicionX, posicionY;
	
	public Disparo(Malla malla)
	{
		entidad = malla;
		
		activado = false;
		indiceAnimacion = 0;
		posicionY = 0.0f;
		
		if (malla.getTipo() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.DisparoPersonaje;
			posicionX = 0.0f;
		}
		else if (malla.getTipo() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.DisparoBoss;
			posicionX = 1000.0f;
		}
		else
		{
			tipoEntidad = TTipoEntidad.Nada;
		}
	}
	private int indiceDisparo(int indice)
	{
		if (tipoEntidad == TTipoEntidad.DisparoPersonaje)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.shot_character;
			}
		}
		else if (tipoEntidad == TTipoEntidad.DisparoBoss)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.shot_boss;
			}
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_SHOTS; i++)
		{
			renderer.cargarTexturaRectangulo(gl, indiceDisparo(i), tipoEntidad, i, TTipoSticker.Nada);
		}
		
		width = entidad.getWidth();
		height = entidad.getHeight();
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		// Burbuja
		for (int i = 0; i < GamePreferences.NUM_TYPE_SHOTS; i++)
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
			
				gl.glTranslatef(posicionX, posicionY, 0.0f);
				
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
	
	public float getPosicionX() 
	{
		return posicionX;
	}
	
	public float getPosicionY() 
	{
		return posicionY;
	}
	
	public boolean isActivado() 
	{
		return activado;
	}
	
	/* Métodos de modificación de Información */
	
	public boolean animar()
	{
		indiceAnimacion = (indiceAnimacion + 1) % GamePreferences.NUM_TYPE_SHOTS;
		return true;
	}
	
	public void activarDisparo()
	{
		activado = true;
		posicionY = entidad.getPosicionY() - getWidth() / 2.0f;
		//posicionX = entidad.getPosicionX() + entidad.getWidth() / 2.0f;
		
	}
	
	public void desactivarDisparo()
	{
		activado = false;
	}
	
	public void mover() 
	{
		if(activado)
		{
			if(entidad.getTipo() == TTipoEntidad.DisparoBoss)
			{
				posicionX -= GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
			else if(entidad.getTipo() == TTipoEntidad.DisparoPersonaje)
			{
				posicionX += GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
		}
	}
	
}
