package com.lib.utils;

import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class DelaunayMesh
{
	private VertexArray vertices;
	private TriangleArray triangles;

	public DelaunayMesh()
	{
		this.vertices = new VertexArray();
		this.triangles = new TriangleArray();
	}

	public DelaunayMesh(VertexArray vertices, TriangleArray triangles)
	{
		this.vertices = vertices;
		this.triangles = triangles;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public void setVertices(VertexArray vertices)
	{
		this.vertices = vertices;
	}

	public TriangleArray getTriangles()
	{
		return triangles;
	}

	public void setTriangles(TriangleArray triangles)
	{
		this.triangles = triangles;
	}

}
