package com.game.data;

import android.graphics.Typeface;

public class Nivel
{
	private Typeface fuenteNivel;
	private int fondoNivel, nombreNivel, descripcionNivel, colorTextoNivel, numeroNivel;

	public Nivel(int numero, int fondo, int nombre, int descripcion, int color, Typeface fuente)
	{
		fondoNivel = fondo;
		nombreNivel = nombre;
		descripcionNivel = descripcion;
		colorTextoNivel = color;
		numeroNivel = numero;
		fuenteNivel = fuente;
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

	public int getNumeroNivel()
	{
		return numeroNivel;
	}
	
	public Typeface getFuenteNivel()
	{
		return fuenteNivel;
	}
}
