package com.creation.design;

import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.main.controller.DataSaved;

public class DesignDataSaved extends DataSaved
{
	private VertexArray points;
	private VertexArray vertices;
	private TriangleArray triangles;
	private HullArray hull;

	private TSatateDesign state;
	private boolean simplex;

	/* Constructora */

	public DesignDataSaved(VertexArray points, VertexArray vertices, TriangleArray triangles, HullArray hull, TSatateDesign state, boolean simplex)
	{
		this.points = points;
		this.vertices = vertices;
		this.triangles = triangles;
		this.hull = hull;
		this.state = state;
		this.simplex = simplex;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public VertexArray getPoints()
	{
		return points;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public TriangleArray getTriangles()
	{
		return triangles;
	}

	public HullArray getHull()
	{
		return hull;
	}

	public TSatateDesign getState()
	{
		return state;
	}

	public boolean getSimplex()
	{
		return simplex;
	}
}
