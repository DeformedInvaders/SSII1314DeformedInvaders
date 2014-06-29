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
	private boolean simplex, singular;

	/* Constructora */

	public DesignDataSaved(VertexArray points, VertexArray vertices, TriangleArray triangles, HullArray hull, TSatateDesign state, boolean simplex, boolean singular)
	{
		this.points = points;
		this.vertices = vertices;
		this.triangles = triangles;
		this.hull = hull;
		this.state = state;
		this.simplex = simplex;
		this.singular = singular;
	}

	/* Métodos de Obtención de Información */

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
	
	public boolean getSingular()
	{
		return singular;
	}
}
