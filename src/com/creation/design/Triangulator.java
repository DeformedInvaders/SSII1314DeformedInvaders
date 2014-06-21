package com.creation.design;

import com.android.storage.ExternalStorageManager;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.BSpline;
import com.lib.math.ConvexHull;
import com.lib.math.DelaunayMeshGenerator;
import com.lib.math.DelaunayTriangulator;
import com.lib.math.EarClippingTriangulator;
import com.lib.math.GeometryUtils;
import com.lib.math.Vector2;
import com.lib.utils.DelaunayMesh;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Triangulator
{
	private final static int NUM_BSPLINE_VERTICES = 60;
	
	private VertexArray mVertices;
	private TriangleArray mTriangles;
	private HullArray mHull;
	private boolean simplex;
	
	/* Constructora */
	
	public Triangulator(VertexArray vertices)
	{
ExternalStorageManager.writeLogcat("TEST", "New Triangulator Object.");
		simplex = false;
		
		if(vertices.getNumVertices() > 2)
		{
ExternalStorageManager.writeLogcat("TEST", "Polygon with more of 2 vertices.");
			FloatArray bsplineVertices = calcularBSpline(vertices, 3, NUM_BSPLINE_VERTICES);
			
			simplex = calcularPoligonoSimple(bsplineVertices, false).size == 0;
			if(simplex)
			{
ExternalStorageManager.writeLogcat("TEST", "Polygon is simple.");
				DelaunayMesh m = calcularMeshGenerator(bsplineVertices);
				mVertices = m.getVertices();
				mTriangles = m.getTriangles();
				
				mHull = new HullArray(NUM_BSPLINE_VERTICES);
				
				for(short i = 0; i < NUM_BSPLINE_VERTICES; i++)
				{
					mHull.addVertex(i);
				}
			}
			else
			{
ExternalStorageManager.writeLogcat("TEST", "Polygon is complex.");
			}
		}
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean getSimplex()
	{
		return simplex;
	}
	
	public VertexArray getVertices()
	{
		return mVertices;
	}
	
	public TriangleArray getTriangles()
	{
		return mTriangles;
	}
	
	public HullArray getHull()
	{
		return mHull;
	}
	
	/* Métodos Públicos Estáticos */
	
	public static FloatArray calcularConvexHull(FloatArray vertices, boolean ordenados)
	{
		ConvexHull convexHullCalculator = new ConvexHull();
		return convexHullCalculator.computePolygon(vertices, ordenados);
	}
	
	public static FloatArray calcularBSpline(FloatArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	public static ShortArray calcularDelaunay(FloatArray vertices, boolean ordenados)
	{
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		return delaunayCalculator.computeTriangles(vertices, ordenados);
	}
	
	public static ShortArray calcularEarClipping(FloatArray vertices)
	{
		EarClippingTriangulator earClippingCalculator = new EarClippingTriangulator();
		return earClippingCalculator.computeTriangles(vertices);
	}
	
	public static DelaunayMesh calcularMeshGenerator(FloatArray vertices)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices);
	}
	
	public static ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
}
