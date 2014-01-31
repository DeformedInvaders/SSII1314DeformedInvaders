package com.example.data;

import java.io.Serializable;

public class Personaje implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Esqueleto esqueleto;
	private Textura textura;
	private Movimientos movimientos;
	private String nombre;
	
	public Personaje()
	{
		
	}

	public Esqueleto getEsqueleto()
	{
		return esqueleto;
	}

	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.esqueleto = esqueleto;
	}

	public Textura getTextura()
	{
		return textura;
	}

	public void setTextura(Textura textura)
	{
		this.textura = textura;
	}

	public Movimientos getMovimientos()
	{
		return movimientos;
	}

	public void setMovimientos(Movimientos movimientos)
	{
		this.movimientos = movimientos;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}
}
