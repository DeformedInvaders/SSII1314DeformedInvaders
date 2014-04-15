package com.game.data;

import android.graphics.Typeface;

import com.game.select.TTipoLevel;

public class Nivel
{
	private Typeface fuenteNivel;
	private TTipoLevel tipoNivel;
	private int fondoNivel, imagenCompleted, imagenPerfected;
	private int nombreNivel, descripcionNivel, colorTextoNivel, musicaNivel;

	public Nivel(TTipoLevel numero, int fondo, int completed, int perfected, int nombre, int descripcion, int color, Typeface fuente, int musica)
	{
		fondoNivel = fondo;
		imagenCompleted = completed;
		imagenPerfected = perfected;
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
	
	public int getImagenCompleted()
	{
		return imagenCompleted;
	}
	
	public int getImagenPerfected()
	{
		return imagenPerfected;
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
