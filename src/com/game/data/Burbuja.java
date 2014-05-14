package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Burbuja extends Entidad
{
	private Malla entidad;
	
	private boolean activado;
	private int numVidas;
	
	public Burbuja(Malla malla, int vidas)
	{
		entidad = malla;
		activado = false;
		numVidas = vidas;
		
		if (malla.getTipo() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.PlataformaPersonaje;
		}
		else if (malla.getTipo() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.PlataformaBoss;
		}
		else
		{
			tipoEntidad = TTipoEntidad.Nada;
		}
	}
	
	private int indiceBurbuja(int vidas)
	{
		switch (vidas)
		{
			case 0:
				return R.drawable.lives_bubble_1;
			case 1:
				return R.drawable.lives_bubble_2;
			default:
				return R.drawable.lives_bubble_3;
		}
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		// Burbuja
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
		// Burbuja
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
			
				gl.glTranslatef(entidad.getPosicionX(), entidad.posicionY, 0.0f);
				
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
	
	/* Métodos de Modificación  */
	
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
