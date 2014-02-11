package com.project.data;

import java.io.Serializable;
import java.util.List;

import com.lib.utils.FloatArray;

public class Textura implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private MapaBits textura;
	private FloatArray coordTextura;
	private List<Integer> pegatinas;
	
	public Textura(MapaBits textura, FloatArray coordTextura, List<Integer> pegatinas)
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
	
	public List<Integer> getPegatinas()
	{
		return pegatinas;
	}

}
