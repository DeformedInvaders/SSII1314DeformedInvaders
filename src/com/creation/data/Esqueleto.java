package com.creation.data;

import java.io.Serializable;

import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class Esqueleto implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HullArray contorno;
	private VertexArray vertices;
	private TriangleArray triangulos;

	/* Constructora */

	public Esqueleto()
	{

	}

	public Esqueleto(HullArray contorno, VertexArray vertices, TriangleArray triangulos)
	{
		this.contorno = contorno;
		this.vertices = vertices;
		this.triangulos = triangulos;
	}

	/* Métodos de Obtención de Información */

	public HullArray getContorno()
	{
		return contorno;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public TriangleArray getTriangulos()
	{
		return triangulos;
	}
}
