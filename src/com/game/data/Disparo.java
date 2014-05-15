package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Disparo extends Entidad
{
	private InstanciaEntidad entidad;
	
	private boolean activado;
	private int indiceAnimacion;
	
	private float posicionX, posicionY;
	
	private Rectangle area;
	
	public Disparo(InstanciaEntidad instancia)
	{
		entidad = instancia;
		
		activado = false;
		indiceAnimacion = 0;
		
		posicionX = 0.0f;
		posicionY = 0.0f;
		
		if (entidad.getTipoEntidad() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.DisparoPersonaje;
		}
		else if (entidad.getTipoEntidad() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.DisparoBoss;
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
			return R.drawable.shot_character;
		}
		else if (tipoEntidad == TTipoEntidad.DisparoBoss)
		{
			return R.drawable.shot_boss;
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_SHOTS; i++)
		{
			renderer.cargarTexturaRectangulo(gl, indiceDisparo(i), tipoEntidad, i, TTipoSticker.Nada);
		}
		
		width = entidad.getWidth();
		height = entidad.getHeight();
		
		area = new Rectangle(posicionX, posicionY, width, height);
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
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
	
	public Rectangle getArea()
	{
		return area;
	}
	
	private void moverArea(float x, float y)
	{
		area.setPosition(x, y);
	}
	
	/* Métodos de modificación de Información */
	
	@Override
	public boolean animar()
	{
		if (activado)
		{
			indiceAnimacion = (indiceAnimacion + 1) % GamePreferences.NUM_TYPE_SHOTS;
			return true;
		}
		
		return false;
	}
	
	public void activarDisparo()
	{
		if (!activado)
		{
			activado = true;
			posicionX = entidad.getPosicionX() + entidad.getWidth() / 2.0f;
			posicionY = entidad.getPosicionY() - entidad.getWidth() / 2.0f;
			
			moverArea(posicionX, posicionY);
		}
	}
	
	public void desactivarDisparo()
	{
		activado = false;
	}
	
	public void mover() 
	{
		if (activado)
		{
			if (entidad.getTipoEntidad() == TTipoEntidad.Enemigo)
			{
				posicionX -= GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
			else if (entidad.getTipoEntidad() == TTipoEntidad.Personaje)
			{
				posicionX += GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
			
			moverArea(posicionX, posicionY);
		}
	}
	
}
