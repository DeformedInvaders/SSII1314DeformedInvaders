package com.example.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;

public class Textura implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private MapaBits textura;
	private FloatArray coordTextura;
	
	public Textura(MapaBits textura, FloatArray coordTextura)
	{
		this.textura = textura;
		this.coordTextura = coordTextura;
	}

	public MapaBits getTextura()
	{
		return textura;
	}

	public void setTextura(MapaBits textura)
	{
		this.textura = textura;
	}

	public FloatArray getCoordTextura()
	{
		return coordTextura;
	}

	public void setCoordTextura(FloatArray coordTextura)
	{
		this.coordTextura = coordTextura;
	}

}
