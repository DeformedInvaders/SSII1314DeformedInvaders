package com.lib.utils;

import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class DelaunayMesh
{
	private VertexArray vertices;
	private TriangleArray triangulos;

	public DelaunayMesh()
	{
		this.vertices = new VertexArray();
		this.triangulos = new TriangleArray();
	}

	public DelaunayMesh(VertexArray vertices, TriangleArray triangulos)
	{
		this.vertices = vertices;
		this.triangulos = triangulos;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public void setVertices(VertexArray vertices)
	{
		this.vertices = vertices;
	}

	public TriangleArray getTriangulos()
	{
		return triangulos;
	}

	public void setTriangulos(TriangleArray triangulos)
	{
		this.triangulos = triangulos;
	}

}
