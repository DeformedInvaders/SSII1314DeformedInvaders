package com.creation.data;

import java.io.Serializable;

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

	public void setPegatina(int indice, int vertice, int tipo)
	{
		if (tipo >= 0 && tipo < GamePreferences.MAX_TEXTURE_STICKER)
		{
			indicePegatinas[tipo] = indice;
			verticePegatinas[tipo] = vertice;
		}
	}

	/* SECTION Métodos de Obtención de Información */

	public boolean isCargada(int tipo)
	{
		return indicePegatinas[tipo] != -1;
	}

	public int getIndice(int tipo)
	{
		return indicePegatinas[tipo];
	}

	public int getVertice(int tipo)
	{
		return verticePegatinas[tipo];
	}

	public int getNumPegatinas()
	{
		return GamePreferences.MAX_TEXTURE_STICKER;
	}
}
