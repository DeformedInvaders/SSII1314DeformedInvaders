package com.game.data;

import android.graphics.Typeface;

import com.game.select.TTypeLevel;

public class Level
{
	private Typeface fuenteNivel;
	private TTypeLevel tipoNivel;
	private int fondoNivel, imagenCompleted, imagenPerfected, imagenMastered;
	private int nombreNivel, descripcionNivel, colorTextoNivel, musicaNivel;

	public Level(TTypeLevel numero, int fondo, int completed, int perfected, int mastered, int nombre, int descripcion, int color, Typeface fuente, int musica)
	{
		fondoNivel = fondo;
		imagenCompleted = completed;
		imagenPerfected = perfected;
		imagenMastered = mastered;
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

	public int getImagenMastered()
	{
		return imagenMastered;
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

	public TTypeLevel getTipoNivel()
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
