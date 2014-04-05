package com.creation.data;

import java.io.Serializable;

import com.game.data.TTipoSticker;
import com.project.main.GamePreferences;
import com.project.main.R;

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

	public boolean isCargada(int tipo)
	{
		return indicePegatinas[tipo] != -1;
	}

	public int getIndice(int tipo)
	{
		int tag = indicePegatinas[tipo];
		
		TTipoSticker[] listaPegatinas = TTipoSticker.values();
		TTipoSticker tipoPegatina = listaPegatinas[tipo];
		
		switch (tipoPegatina)
		{
			case Eyes:
				switch(tag)
				{
					case 0:
						return R.drawable.sticker_eyes_1;
					case 1:
						return R.drawable.sticker_eyes_2;
					case 2:
						return R.drawable.sticker_eyes_3;
					case 3:
						return R.drawable.sticker_eyes_4;
					default:
						return R.drawable.sticker_eyes_5;
				}
			case Mouth:
				switch(tag)
				{
					case 0:
						return R.drawable.sticker_mouth_1;
					case 1:
						return R.drawable.sticker_mouth_2;
					case 2:
						return R.drawable.sticker_mouth_3;
					case 3:
						return R.drawable.sticker_mouth_4;
					default:
						return R.drawable.sticker_mouth_5;
				}
			case Weapon:
				switch(tag)
				{
					case 0:
						return R.drawable.sticker_weapon_1;
					case 1:
						return R.drawable.sticker_weapon_2;
					case 2:
						return R.drawable.sticker_weapon_3;
					case 3:
						return R.drawable.sticker_weapon_4;
					default:
						return R.drawable.sticker_weapon_5;
				}
			default:
				return -1;
		}
	}

	public int getVertice(int tipo)
	{
		return verticePegatinas[tipo];
	}

}
