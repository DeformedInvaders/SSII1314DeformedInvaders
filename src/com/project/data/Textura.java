package com.project.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;

public class Textura implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private MapaBits mapaBits;
	private FloatArray coordTextura;
	private Pegatinas pegatinas;
	
	/* SECTION Constructora */
	
	public Textura(MapaBits mapaBits, FloatArray coordTextura, Pegatinas pegatinas)
	{
		this.mapaBits = mapaBits;
		this.coordTextura = coordTextura;
		this.pegatinas = pegatinas;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public MapaBits getMapaBits()
	{
		return mapaBits;
	}

	public FloatArray getCoordTextura()
	{
		return coordTextura;
	}
	
	public Pegatinas getPegatinas()
	{
		return pegatinas;
	}

}
