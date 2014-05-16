package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTipoSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Plataforma extends Entidad
{	
	private InstanciaEntidad entidad;
	private TTipoEntidad tipoEntidadArma;
	
	private boolean activado;
	
	private int indiceAnimacionFuego;
	private int indiceAnimacionDisparo;
	private int indiceArma;
	
	public Plataforma(InstanciaEntidad instancia)
	{
		entidad = instancia;
		activado = false;
		
		indiceAnimacionFuego = 0;
		indiceAnimacionDisparo = 0;
		indiceArma = 0;
		
		if (entidad.getTipoEntidad() == TTipoEntidad.Personaje)
		{
			tipoEntidad = TTipoEntidad.PlataformaPersonaje;
			tipoEntidadArma = TTipoEntidad.ArmaPersonaje;
		}
		else if (entidad.getTipoEntidad() == TTipoEntidad.Enemigo)
		{
			tipoEntidad = TTipoEntidad.PlataformaBoss;
			tipoEntidadArma = TTipoEntidad.ArmaBoss;
		}
		else
		{
			tipoEntidad = TTipoEntidad.Nada;
			tipoEntidadArma = TTipoEntidad.Nada;
		}
	}
	
	private int indicePlataforma(int indice)
	{
		if (tipoEntidad == TTipoEntidad.PlataformaPersonaje)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.platform_character_1;
				case 1:
					return R.drawable.platform_character_2;
				default:
					return R.drawable.platform_character_3;
			}
		}
		else if (tipoEntidad == TTipoEntidad.PlataformaBoss)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.platform_boss_1;
				case 1:
					return R.drawable.platform_boss_2;
				default:
					return R.drawable.platform_boss_3;
			}
		}
		
		return -1;
	}
	
	private int indiceArma(int indice)
	{
		if (tipoEntidadArma == TTipoEntidad.ArmaPersonaje)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.weapon_character_1;
				case 1:
					return R.drawable.weapon_character_2;
				case 2:
					return R.drawable.weapon_character_3;
				default:
					return R.drawable.weapon_character_4;
			}
		}
		else if (tipoEntidadArma == TTipoEntidad.ArmaBoss)
		{
			switch (indice)
			{
				case 0:
					return R.drawable.weapon_boss_1;
				case 1:
					return R.drawable.weapon_boss_2;
				case 2:
					return R.drawable.weapon_boss_3;
				default:
					return R.drawable.weapon_boss_4;
			}
		}
		
		return -1;
	}
	
	private int indiceArma(int indice, int arma)
	{
		if (indice == 0)
		{
			return arma;
		}
		else
		{
			return arma + 2;
		}
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
		{
			renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indiceArma(i), tipoEntidadArma, i, TTipoSticker.Nada);
		}
		
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
		for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
		{
			renderer.descargarTexturaRectangulo(tipoEntidadArma, i, TTipoSticker.Nada);
		}
		
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
			if (tipoEntidad == TTipoEntidad.PlataformaPersonaje) android.util.Log.d("TEST", "INDICEARMA: "+indiceArma+" INDICEDISPARO: "+indiceAnimacionDisparo+" INDICE: "+indiceArma(indiceAnimacionDisparo, indiceArma));
			
			if (indiceArma == 0)
			{		
				// Plataforma	
				gl.glPushMatrix();
					
					gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - 5.0f * getHeight() / 8.0f, 0.0f);
					
					renderer.dibujarTexturaRectangulo(gl, tipoEntidad, indiceAnimacionFuego, TTipoSticker.Nada);
				
				gl.glPopMatrix();	
				
				// Arma Trasera
				gl.glPushMatrix();
					
					gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - getHeight() / 8.0f, 0.0f);
					
					renderer.dibujarTexturaRectangulo(gl, tipoEntidadArma, indiceArma(indiceAnimacionDisparo, indiceArma), TTipoSticker.Nada);
				
				gl.glPopMatrix();
			}
			else
			{
				// Arma Frontal
				gl.glPushMatrix();
					
					gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - getHeight() / 8.0f, 0.0f);
					
					renderer.dibujarTexturaRectangulo(gl, tipoEntidadArma, indiceArma(indiceAnimacionDisparo, indiceArma), TTipoSticker.Nada);
					
				gl.glPopMatrix();
			}
			
			indiceArma = (indiceArma + 1) % 2;
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
		indiceAnimacionFuego = (indiceAnimacionFuego + 1) % GamePreferences.NUM_TYPE_PLATFORMS;
		
		if (indiceAnimacionDisparo > 0)
		{
			indiceAnimacionDisparo--;
			
			if (tipoEntidad == TTipoEntidad.PlataformaPersonaje) android.util.Log.d("TEST", "INDICE DISPARO: "+indiceAnimacionDisparo);
		}
		
		return true;
	}
	
	public void activarDisparo()
	{
		indiceAnimacionDisparo = GamePreferences.NUM_FRAMES_DISPARO;
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
