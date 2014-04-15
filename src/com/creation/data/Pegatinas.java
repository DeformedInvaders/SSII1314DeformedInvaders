package com.creation.data;

import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.TTipoEntidad;
import com.lib.utils.FloatArray;
import com.project.model.GamePreferences;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;

	// ID Drawable
	private int[] indicePegatinas;

	// Vertice Asociado
	private int[] verticePegatinas;

	/* Constructora */

	public Pegatinas()
	{
		indicePegatinas = new int[GamePreferences.NUM_TEXTURE_STICKER];
		verticePegatinas = new int[GamePreferences.NUM_TEXTURE_STICKER];

		for (int i = 0; i < GamePreferences.NUM_TEXTURE_STICKER; i++)
		{
			indicePegatinas[i] = -1;
			verticePegatinas[i] = -1;
		}
	}
	
	public void cargarTexturas(GL10 gl, OpenGLRenderer renderer, Context context, TTipoEntidad tipo, int id)
	{
		for (int i = 0; i < GamePreferences.NUM_TEXTURE_STICKER; i++)
		{
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			
			if (isCargada(tipoPegatinas[i]))
			{
				renderer.cargarTexturaRectangulo(gl, getIndice(tipoPegatinas[i], context), tipo, id, tipoPegatinas[i]);
			}
		}
	}
	
	public void descargarTextura(OpenGLRenderer renderer, TTipoEntidad tipo, int id)
	{
		for (int i = 0; i < GamePreferences.NUM_TEXTURE_STICKER; i++)
		{
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			renderer.descargarTexturaRectangulo(tipo, id, tipoPegatinas[i]);
		}
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, FloatArray vertices, TTipoEntidad tipo, int id)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(0, 0, GamePreferences.DEEP_STICKERS);
			
			for (int i = 0; i < GamePreferences.NUM_TEXTURE_STICKER; i++)
			{
				TTipoSticker tipoPegatinas = TTipoSticker.values()[i];
				
				if (isCargada(tipoPegatinas))
				{
					int indice = getVertice(tipoPegatinas);
					renderer.dibujarTexturaRectangulo(gl, vertices.get(2 * indice), vertices.get(2 * indice + 1), tipo, id, tipoPegatinas);
				}
			}
			
		gl.glPopMatrix();
	}

	/* Métodos de Modificación de Información */

	public void setPegatina(int indice, int vertice, TTipoSticker tipo)
	{
		indicePegatinas[tipo.ordinal()] = indice;
		verticePegatinas[tipo.ordinal()] = vertice;
	}
	
	public void deletePegatina(TTipoSticker tipo)
	{
		indicePegatinas[tipo.ordinal()] = -1;
		verticePegatinas[tipo.ordinal()] = -1;	
	}

	/* Métodos de Obtención de Información */

	private boolean isCargada(TTipoSticker tipo)
	{
		return indicePegatinas[tipo.ordinal()] != -1;
	}

	private int getIndice(TTipoSticker tipo, Context context)
	{
		int tag = indicePegatinas[tipo.ordinal()];
		
		String nombrePegatina;
		
		switch (tipo)
		{
			case Eyes:
				nombrePegatina = GamePreferences.RESOURCE_ID_STICKER_EYES;
			break;
			case Mouth:
				nombrePegatina = GamePreferences.RESOURCE_ID_STICKER_MOUTH;
			break;
			case Weapon:
				nombrePegatina = GamePreferences.RESOURCE_ID_STICKER_WEAPON;
			break;
			case Trinket:
				nombrePegatina = GamePreferences.RESOURCE_ID_STICKER_TRINKET;
			break;
			case Helmet:
				nombrePegatina = GamePreferences.RESOURCE_ID_STICKER_HELMET;
			break;
			default:
				return -1;
		}
		
		return context.getResources().getIdentifier(nombrePegatina + tag, "drawable", context.getPackageName());
	}

	private int getVertice(TTipoSticker tipo)
	{
		return verticePegatinas[tipo.ordinal()];
	}

}
