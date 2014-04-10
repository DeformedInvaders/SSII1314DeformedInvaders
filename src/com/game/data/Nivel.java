package com.game.data;

import com.game.select.TTipoLevel;

import android.graphics.Typeface;

public class Nivel
{
	private Typeface fuenteNivel;
	private TTipoLevel tipoNivel;
	private int fondoNivel, nombreNivel, descripcionNivel, colorTextoNivel, musicaNivel;

	public Nivel(TTipoLevel numero, int fondo, int nombre, int descripcion, int color, Typeface fuente, int musica)
	{
		fondoNivel = fondo;
		nombreNivel = nombre;
		descripcionNivel = descripcion;
		colorTextoNivel = color;
		tipoNivel = numero;
		fuenteNivel = fuente;
		musicaNivel = musica;
	}

	public int getFondoNivel()
	{
		return fondoNivel;
	}

	public int getNombreNivel()
	{
		return nombreNivel;
	}

	public int getDescripcionNivel()
	{
		return descripcionNivel;
	}

	public int getColorTextoNivel()
	{
		return colorTextoNivel;
	}

	public TTipoLevel getTipoNivel()
	{
		return tipoNivel;
	}
	
	public Typeface getFuenteNivel()
	{
		return fuenteNivel;
	}
	
	public int getMusicaNivel()
	{
		return musicaNivel;
	}
}
