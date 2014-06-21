package com.creation.data;

import java.io.Serializable;

import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class Skeleton implements Serializable
{
	private static final long serialVersionUID = 1L;

	private HullArray hull;
	private VertexArray vertices;
	private TriangleArray triangles;

	/* Constructora */

	public Skeleton()
	{

	}

	public Skeleton(HullArray hull, VertexArray vertices, TriangleArray triangles)
	{
		this.hull = hull;
		this.vertices = vertices;
		this.triangles = triangles;
	}

	/* Métodos de Obtención de Información */

	public HullArray getHull()
	{
		return hull;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public TriangleArray getTriangles()
	{
		return triangles;
	}
}
