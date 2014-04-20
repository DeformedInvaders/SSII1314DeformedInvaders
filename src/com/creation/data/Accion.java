package com.creation.data;

public class Accion
{
	// Tipo de Acción
	private TTipoAccion tipo;

	// Color
	private int color;

	// Pegatina
	private TTipoSticker pegatina;
	
	// Polilinea
	private Polilinea linea;

	/* Constructora */
	public Accion(int color)
	{
		this.color = color;
		this.tipo = TTipoAccion.Color;
	}

	public Accion(Polilinea linea)
	{
		this.linea = linea;
		this.tipo = TTipoAccion.Polilinea;
	}

	public Accion(TTipoSticker pegatina)
	{
		this.pegatina = pegatina;
		this.tipo = TTipoAccion.Pegatina;
	}

	/* Métodos de Obtención de Información */

	public int getColor()
	{
		return color;
	}

	public Polilinea getLinea()
	{
		return linea;
	}

	public TTipoSticker getTipoPegatina()
	{
		return pegatina;
	}

	public boolean isTipoColor()
	{
		return tipo == TTipoAccion.Color;
	}

	public boolean isTipoPolilinea()
	{
		return tipo == TTipoAccion.Polilinea;
	}

	public boolean isTipoPegatina()
	{
		return tipo == TTipoAccion.Pegatina;
	}
}
