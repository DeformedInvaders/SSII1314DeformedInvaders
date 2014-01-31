package com.example.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Esqueleto implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private ShortArray contorno;
	private FloatArray vertices;
	private ShortArray triangulos;
	
	public Esqueleto()
	{
		
	}
	
	public Esqueleto(ShortArray contorno, FloatArray vertices, ShortArray triangulos)
	{
		this.contorno = contorno;
		this.vertices = vertices;
		this.triangulos = triangulos;
	}

	public ShortArray getContorno()
	{
		return contorno;
	}

	public void setContorno(ShortArray contorno)
	{
		this.contorno = contorno;
	}

	public FloatArray getVertices()
	{
		return vertices;
	}

	public void setVertices(FloatArray vertices)
	{
		this.vertices = vertices;
	}

	public ShortArray getTriangulos()
	{
		return triangulos;
	}

	public void setTriangulos(ShortArray triangulos)
	{
		this.triangulos = triangulos;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}
}
