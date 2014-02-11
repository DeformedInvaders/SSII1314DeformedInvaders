package com.create.paint;

public class Accion
{
	private int tipo;
	
	// Color
	private int color;
	
	// Pegatina
	private int pegatina, vertice;
	
	// Polilinea
	private Polilinea linea;
	
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
	
	public Accion(int pegatina, int vertice)
	{
		this.pegatina = pegatina;
		this.vertice = vertice;
		this.tipo = 2;
	}
	
	public int getColor()
	{
		return color;
	}
	
	public Polilinea getLinea()
	{
		return linea;
	}
	
	public int getPegatina()
	{
		return pegatina;
	}
	
	public int getVertice()
	{
		return vertice;
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
