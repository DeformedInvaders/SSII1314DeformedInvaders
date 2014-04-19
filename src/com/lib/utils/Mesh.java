package com.lib.utils;

import com.lib.opengl.TriangleArray;
import com.lib.opengl.VertexArray;

public class Mesh
{
	private VertexArray vertices;
	private TriangleArray triangulos;

	public Mesh()
	{
		this.vertices = new VertexArray();
		this.triangulos = new TriangleArray();
	}

	public Mesh(VertexArray vertices, TriangleArray triangulos)
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
