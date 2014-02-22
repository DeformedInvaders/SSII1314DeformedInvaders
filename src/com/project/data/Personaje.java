package com.project.data;

import java.io.Serializable;

public class Personaje implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Esqueleto esqueleto;
	private Textura textura;
	private Movimientos movimientos;
	private String nombre;
	
	/* SECTION Constructora */
	
	public Personaje()
	{
		
	}
	
	/* SECTION Métodos de Modificación de Información */
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.esqueleto = esqueleto;
	}
	
	public void setTextura(Textura textura)
	{
		this.textura = textura;
	}
	
	public void setMovimientos(Movimientos movimientos)
	{
		this.movimientos = movimientos;
	}
	
	public void setNombre(String nombre)
	{
		this.nombre = nombre;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public Esqueleto getEsqueleto()
	{
		return esqueleto;
	}

	public Textura getTextura()
	{
		return textura;
	}

	public Movimientos getMovimientos()
	{
		return movimientos;
	}

	public String getNombre()
	{
		return nombre;
	}
}
