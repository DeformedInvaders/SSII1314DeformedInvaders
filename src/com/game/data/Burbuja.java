package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Burbuja extends Entidad
{
	private InstanciaEntidad entidad;
	
	private boolean activado;
	private int numVidas;
	
	public Burbuja(InstanciaEntidad instancia, int vidas)
	{
		entidad = instancia;
		activado = false;
		numVidas = vidas;
		
		if (entidad.getTipoEntidad() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.BurbujaPersonaje;
		}
		else if (entidad.getTipoEntidad() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.BurbujaBoss;
		}
		else
		{
			tipoEntidad = TTipoEntidad.Nada;
		}
	}
	
	private int indiceBurbuja(int vidas)
	{
		if (tipoEntidad == TTipoEntidad.BurbujaPersonaje)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_character_1;
				case 1:
					return R.drawable.bubble_character_2;
				default:
					return R.drawable.bubble_character_3;
			}
		}
		else if (tipoEntidad == TTipoEntidad.BurbujaBoss)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_boss_1;
				case 1:
					return R.drawable.bubble_boss_2;
				default:
					return R.drawable.bubble_boss_3;
			}
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
		{
			renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indiceBurbuja(i), tipoEntidad, i, TTipoSticker.Nada);
		}
		
		width = entidad.getWidth();
		height = entidad.getHeight();
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
		{
			renderer.descargarTexturaRectangulo(tipoEntidad, i, TTipoSticker.Nada);
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (activado && numVidas > 0)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY(), 0.0f);
				
				renderer.dibujarTexturaRectangulo(gl, tipoEntidad, numVidas - 1, TTipoSticker.Nada);
			
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
	
	/* Métodos de Modificación de Información */
	
	public void activarBurbuja()
	{
		activado = true;
	}
	
	public void desactivarBurbuja()
	{
		activado = false;
	}
	
	public void reiniciarVidas(int vidas)
	{
		numVidas = vidas;
	}
	
	public void quitarVida()
	{
		numVidas--;
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean isAlive()
	{
		return numVidas > 0;
	}
	
	public int getVidas()
	{
		return numVidas;
	}
}
