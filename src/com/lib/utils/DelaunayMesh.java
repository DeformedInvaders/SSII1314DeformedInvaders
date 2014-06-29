package com.lib.utils;

import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;

public class DelaunayMesh
{
	private VertexArray vertices;
	private TriangleArray triangles;
	private boolean singular;

	public DelaunayMesh()
	{
		this(new VertexArray(), new TriangleArray(), false);
	}

	public DelaunayMesh(VertexArray vertices, TriangleArray triangles, boolean singular)
	{
		this.vertices = vertices;
		this.triangles = triangles;
		this.singular = singular;
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
	
	public boolean getSingular()
	{
		return singular;
	}
	
	public void setSingular(boolean singular)
	{
		this.singular = singular;
	}

}
