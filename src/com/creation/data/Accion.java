package com.creation.data;


public class Accion
{
	// Tipo de Acción
	private int tipo;

	// Color
	private int color;

	// Pegatina
	private int indice, vertice;
	private TTipoSticker pegatina;
	
	// Polilinea
	private Polilinea linea;

	/* Constructora */
	public Accion(int color)
	{
		this.color = color;
		this.tipo = 0;
	}

	public Accion(Polilinea linea)
	{
		this.linea = linea;
		this.tipo = 1;
	}

	public Accion(int indice, int vertice, TTipoSticker pegatina)
	{
		this.indice = indice;
		this.pegatina = pegatina;
		this.vertice = vertice;
		this.tipo = 2;
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

	public int getVerticePegatina()
	{
		return vertice;
	}

	public int getIndicePegatina()
	{
		return indice;
	}

	public boolean isTipoColor()
	{
		return tipo == 0;
	}

	public boolean isTipoPolilinea()
	{
		return tipo == 1;
	}

	public boolean isTipoPegatina()
	{
		return tipo == 2;
	}
}
