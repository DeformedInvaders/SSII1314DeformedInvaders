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
	
	/* SECTION M�todos de Modificaci�n de Informaci�n */
	
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
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */

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
