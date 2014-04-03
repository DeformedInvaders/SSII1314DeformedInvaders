package com.game.data;

public class Nivel
{
	private int fondoNivel, nombreNivel, descripcionNivel, colorTextoNivel, numeroNivel;

	public Nivel(int numero, int fondo, int nombre, int descripcion, int color)
	{
		fondoNivel = fondo;
		nombreNivel = nombre;
		descripcionNivel = descripcion;
		colorTextoNivel = color;
		numeroNivel = numero;
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
}
