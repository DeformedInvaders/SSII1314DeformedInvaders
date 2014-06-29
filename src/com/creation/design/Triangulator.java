package com.creation.design;

import com.creation.deform.Deformator;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.BSpline;
import com.lib.math.ConvexHull;
import com.lib.math.DelaunayTriangulator;
import com.lib.math.EarClippingTriangulator;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.math.Polygon;
import com.lib.math.Vector2;
import com.lib.utils.DelaunayMesh;

public class Triangulator
{
	private final static int NUM_BSPLINE_VERTICES = 60;
	
	private VertexArray mVertices;
	private TriangleArray mTriangles;
	private HullArray mHull;
	private boolean mSimplex, mSingular;
	
	/* Constructora */
	
	public Triangulator(VertexArray vertices)
	{
		mSimplex = false;
		mSingular = false;
		
		if(vertices.getNumVertices() > 2)
		{
			VertexArray bsplineVertices = buildBSpline(vertices, 3, NUM_BSPLINE_VERTICES);
			
			mSimplex = isSimplexPolygon(bsplineVertices, false);
			if(mSimplex)
			{
				bsplineVertices.removeVertex(bsplineVertices.getNumVertices() - 1);
				
				DelaunayMesh m = buildDelaunayMesh(bsplineVertices);
				mVertices = m.getVertices();
				mTriangles = m.getTriangles();
				mSingular = m.getSingular();
				
				mHull = new HullArray();
				
				for (short i = 0; i < bsplineVertices.getNumVertices(); i++)
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
	
	public boolean getSingular()
	{
		return mSingular;
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
	
	public static VertexArray buildConvexHull(VertexArray vertices, boolean ordenados)
	{
		ConvexHull convexHullCalculator = new ConvexHull();
		return new VertexArray(convexHullCalculator.computePolygon(vertices, ordenados));
	}
	
	public static VertexArray buildBSpline(VertexArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return new VertexArray(bsplineCalculator.computeBSpline(0.0f, iter));
	}
	
	public static TriangleArray buildDelaunayTriangulation(VertexArray vertices, boolean ordenados)
	{
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		return new TriangleArray(delaunayCalculator.computeTriangles(vertices, ordenados));
	}
	
	public static TriangleArray buildEarClippingTriangulation(VertexArray vertices)
	{
		EarClippingTriangulator earClippingCalculator = new EarClippingTriangulator();
		return new TriangleArray(earClippingCalculator.computeTriangles(vertices));
	}
	
	public static DelaunayMesh buildDelaunayMesh(VertexArray vertices)
	{
		Polygon poligonoBSpline = new Polygon(vertices.items);
		float areaPoligonoBSpline = Math.abs(poligonoBSpline.area());
		
		VertexArray delaunayPuntos = vertices.clone();
		TriangleArray delaunayTriangulos = buildDelaunayTriangulation(delaunayPuntos, false);

		boolean meshChanged = true;

		while (meshChanged)
		{
			meshChanged = false;
			
			Polygon poligono = new Polygon();
			float[] vert = new float[6];
			
			for (short j = 0; j < delaunayTriangulos.getNumTriangles(); j++)
			{
			
				short a = delaunayTriangulos.getAVertex(j);
				short b = delaunayTriangulos.getBVertex(j);
				short c = delaunayTriangulos.getCVertex(j);
				
				vert[0] = delaunayPuntos.getXVertex(a);
				vert[1] = delaunayPuntos.getYVertex(a);
				vert[2] = delaunayPuntos.getXVertex(b);
				vert[3] = delaunayPuntos.getYVertex(b);
				vert[4] = delaunayPuntos.getXVertex(c);
				vert[5] = delaunayPuntos.getYVertex(c);
				
				poligono.setVertices(vert);
				
				if (Math.abs(poligono.area()) > areaPoligonoBSpline / 40)
				{
					Vector2 centroGravedad = GeometryUtils.triangleCentroid(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5], new Vector2());
					
					if (Intersector.isPointInTriangle(centroGravedad.x, centroGravedad.y, vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]))
					{
						delaunayPuntos.addVertex(centroGravedad.x, centroGravedad.y);						
						meshChanged = true;
					}
				}
			}
			
			delaunayTriangulos = buildDelaunayTriangulation(delaunayPuntos, false);
		}
		
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);
		
		Deformator deformator = new Deformator(delaunayPuntos, delaunayTriangulos);
		
		return new DelaunayMesh(delaunayPuntos, delaunayTriangulos, deformator.isSingular());
	}
	
	public static boolean isSimplexPolygon(VertexArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo).size == 0;
	}
}
