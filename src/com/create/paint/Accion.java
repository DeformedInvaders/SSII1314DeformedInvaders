package com.create.paint;

public class Accion
{
	private int tipo;
	private int color;
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
	
	public int getColor()
	{
		return color;
	}
	
	public Polilinea getLinea()
	{
		return linea;
	}
	
	public int getTipo()
	{
		return tipo;
	}
}
