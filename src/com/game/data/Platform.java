package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Platform extends Entity
{	
	private InstanceEntity entidad;
	private TTypeEntity tipoEntidadArma;
	
	private boolean activado;
	
	private int indiceAnimacionFuego;
	private int indiceAnimacionDisparo;
	private int indiceArma;
	
	public Platform(InstanceEntity instancia)
	{
		entidad = instancia;
		activado = false;
		
		indiceAnimacionFuego = 0;
		indiceAnimacionDisparo = 0;
		indiceArma = 0;
		
		if (entidad.getTipoEntidad() == TTypeEntity.Character)
		{
			tipoEntidad = TTypeEntity.CharacterPlatform;
			tipoEntidadArma = TTypeEntity.CharacterWeapon;
		}
		else if (entidad.getTipoEntidad() == TTypeEntity.Boss)
		{
			tipoEntidad = TTypeEntity.BossPlatform;
			tipoEntidadArma = TTypeEntity.BossWeapon;
		}
		else
		{
			tipoEntidad = TTypeEntity.Nothing;
			tipoEntidadArma = TTypeEntity.Nothing;
		}
	}
	
	private int indicePlataforma(int indice)
	{
		if (tipoEntidad == TTypeEntity.CharacterPlatform)
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
		else if (tipoEntidad == TTypeEntity.BossPlatform)
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
		if (tipoEntidadArma == TTypeEntity.CharacterWeapon)
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
		else if (tipoEntidadArma == TTypeEntity.BossWeapon)
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
	
	private int indiceArma()
	{
		if (indiceAnimacionDisparo == 0)
		{
			return indiceArma;
		}
		else
		{
			return indiceArma + GamePreferences.NUM_TYPE_TEXTURE_WEAPONS;
		}
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
			{
				renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indiceArma(i), tipoEntidadArma, i, TTypeSticker.Nothing);
			}
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
			{
				renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indicePlataforma(i), tipoEntidad, i, TTypeSticker.Nothing);
			}
			
			width = entidad.getWidth();
			height = entidad.getHeight();
		}
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
			{
				renderer.descargarTexturaRectangulo(tipoEntidadArma, i, TTypeSticker.Nothing);
			}
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
			{
				renderer.descargarTexturaRectangulo(tipoEntidad, i, TTypeSticker.Nothing);
			}
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			if (activado)
			{
				if (indiceArma == 0)
				{		
					// Plataforma	
					gl.glPushMatrix();
						
						gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - 5.0f * entidad.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
						renderer.dibujarTexturaRectangulo(gl, tipoEntidad, indiceAnimacionFuego, TTypeSticker.Nothing);
					
					gl.glPopMatrix();	
					
					// Arma Trasera
					gl.glPushMatrix();
						
						gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - entidad.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
						renderer.dibujarTexturaRectangulo(gl, tipoEntidadArma, indiceArma(), TTypeSticker.Nothing);
					
					gl.glPopMatrix();
				}
				else
				{
					// Arma Frontal
					gl.glPushMatrix();
						
						gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY() - entidad.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
						renderer.dibujarTexturaRectangulo(gl, tipoEntidadArma, indiceArma(), TTypeSticker.Nothing);
						
					gl.glPopMatrix();
				}
				
				indiceArma = (indiceArma + 1) % 2;
			}
		}
	}
	
	/* Métodos de modificación de Información */
	
	public boolean animar()
	{
		indiceAnimacionFuego = (indiceAnimacionFuego + 1) % GamePreferences.NUM_TYPE_PLATFORMS;
		
		if (indiceAnimacionDisparo > 0)
		{
			indiceAnimacionDisparo--;
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
