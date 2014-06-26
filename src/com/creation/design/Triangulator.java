package com.creation.design;

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
	private boolean mSimplex;
	
	/* Constructora */
	
	public Triangulator(VertexArray vertices)
	{
		mSimplex = false;
		
		if(vertices.getNumVertices() > 2)
		{
			FloatArray bsplineVertices = buildBSpline(vertices, 3, NUM_BSPLINE_VERTICES);
			
			mSimplex = isSimplexPolygon(bsplineVertices, false).size == 0;
			if(mSimplex)
			{
				bsplineVertices.removeIndex(bsplineVertices.size - 2);
				bsplineVertices.removeIndex(bsplineVertices.size - 1);
				
				DelaunayMesh m = buildDelaunayMesh(bsplineVertices);
				mVertices = m.getVertices();
				mTriangles = m.getTriangles();
				
				mHull = new HullArray();
				
				for(short i = 0; i < bsplineVertices.size / 2; i++)
				{
					mHull.addVertex(i);
				}
			}
		}
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean getSimplex()
	{
		return mSimplex;
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
	
	public static FloatArray buildConvexHull(FloatArray vertices, boolean ordenados)
	{
		ConvexHull convexHullCalculator = new ConvexHull();
		return convexHullCalculator.computePolygon(vertices, ordenados);
	}
	
	public static FloatArray buildBSpline(FloatArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	public static ShortArray buildDelaunayTriangulation(FloatArray vertices, boolean ordenados)
	{
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		return delaunayCalculator.computeTriangles(vertices, ordenados);
	}
	
	public static ShortArray buildEarClippingTriangulation(FloatArray vertices)
	{
		EarClippingTriangulator earClippingCalculator = new EarClippingTriangulator();
		return earClippingCalculator.computeTriangles(vertices);
	}
	
	public static DelaunayMesh buildDelaunayMesh(FloatArray vertices)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices);
	}
	
	public static ShortArray isSimplexPolygon(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
}
