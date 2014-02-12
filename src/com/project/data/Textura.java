package com.project.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;

public class Textura implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private MapaBits textura;
	private FloatArray coordTextura;
	private Pegatinas pegatinas;
	
	public Textura(MapaBits textura, FloatArray coordTextura, Pegatinas pegatinas)
	{
		this.textura = textura;
		this.coordTextura = coordTextura;
		this.pegatinas = pegatinas;
	}

	public MapaBits getTextura()
	{
		return textura;
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
