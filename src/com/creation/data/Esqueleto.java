package com.creation.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Esqueleto implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private ShortArray contorno;
	private FloatArray vertices;
	private ShortArray triangulos;
	
	/* SECTION Constructora */
	
	public Esqueleto()
	{
		
	}
	
	public Esqueleto(ShortArray contorno, FloatArray vertices, ShortArray triangulos)
	{
		this.contorno = contorno;
		this.vertices = vertices;
		this.triangulos = triangulos;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public ShortArray getContorno()
	{
		return contorno;
	}

	public FloatArray getVertices()
	{
		return vertices;
	}

	public ShortArray getTriangulos()
	{
		return triangulos;
	}
}
