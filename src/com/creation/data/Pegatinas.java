package com.creation.data;

import java.io.Serializable;

import android.content.Context;

import com.game.data.TTipoSticker;
import com.project.main.GamePreferences;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;

	// ID Drawable
	private int[] indicePegatinas;

	// Vertice Asociado
	private int[] verticePegatinas;

	/* SECTION Constructora */

	public Pegatinas()
	{
		indicePegatinas = new int[GamePreferences.MAX_TEXTURE_STICKER];
		verticePegatinas = new int[GamePreferences.MAX_TEXTURE_STICKER];

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			indicePegatinas[i] = -1;
			verticePegatinas[i] = -1;
		}
	}

	/* SECTION Métodos de Modificación de Información */

	public void setPegatina(int indice, int vertice, TTipoSticker tipo)
	{
		indicePegatinas[tipo.ordinal()] = indice;
		verticePegatinas[tipo.ordinal()] = vertice;
	}

	/* SECTION Métodos de Obtención de Información */

	public boolean isCargada(TTipoSticker tipo)
	{
		return indicePegatinas[tipo.ordinal()] != -1;
	}

	public int getIndice(TTipoSticker tipo, Context context)
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
		
		return context.getResources().getIdentifier(nombrePegatina + (tag + 1), "drawable", context.getPackageName());
	}

	public int getVertice(TTipoSticker tipo)
	{
		return verticePegatinas[tipo.ordinal()];
	}

}
