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
import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;

public class Triangulator
{
	private final static int NUM_BSPLINE_VERTICES = 60;
	
	private VertexArray vertices;
	private TriangleArray triangulos;
	private HullArray contorno;
	private boolean poligonoSimple;
	
	/* Constructora */
	
	public Triangulator(VertexArray puntos)
	{
ExternalStorageManager.writeLogcat("TEST", "New Triangulator Object.");
		poligonoSimple = false;
		
		if(puntos.getNumVertices() > 2)
		{
ExternalStorageManager.writeLogcat("TEST", "Polygon with more of 2 vertices.");
			FloatArray bsplineVertices = calcularBSpline(puntos, 3, NUM_BSPLINE_VERTICES);
			
			poligonoSimple = calcularPoligonoSimple(bsplineVertices, false).size == 0;
			if(poligonoSimple)
			{
ExternalStorageManager.writeLogcat("TEST", "Polygon is simple.");
				Mesh m = calcularMeshGenerator(bsplineVertices);
				vertices = m.getVertices();
				triangulos = m.getTriangulos();
				
				contorno = new HullArray(NUM_BSPLINE_VERTICES);
				
				for(short i = 0; i < NUM_BSPLINE_VERTICES; i++)
				{
					contorno.addVertex(i);
				}
			}
ExternalStorageManager.writeLogcat("TEST", "Polygon is complex.");
		}
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean getPoligonSimple()
	{
		return poligonoSimple;
	}
	
	public VertexArray getVertices()
	{
		return vertices;
	}
	
	public TriangleArray getTriangulos()
	{
		return triangulos;
	}
	
	public HullArray getContorno()
	{
		return contorno;
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
	
	public static Mesh calcularMeshGenerator(FloatArray vertices)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices);
	}
	
	public static ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
}
