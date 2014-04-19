package com.creation.design;

import com.lib.math.BSpline;
import com.lib.math.ConvexHull;
import com.lib.math.DelaunayMeshGenerator;
import com.lib.math.DelaunayTriangulator;
import com.lib.math.EarClippingTriangulator;
import com.lib.math.GeometryUtils;
import com.lib.math.Vector2;
import com.lib.opengl.HullArray;
import com.lib.opengl.TriangleArray;
import com.lib.opengl.VertexArray;
import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;

public class Triangulator
{
	private final static int NUM_BSPLINE_VERTICES = 60;
	private final static int DEEP_TRIANGULATOR = 2;
	private final static float MAX_LONG_EDGE_TRIANGULATOR = 20.0f;
	
	private VertexArray vertices;
	private TriangleArray triangulos;
	private HullArray contorno;
	private boolean poligonoSimple;
	
	/* SECTION Constructora */
	
	public Triangulator(VertexArray puntos)
	{
		poligonoSimple = false;
		
		if(puntos.getNumVertices() > 2)
		{			
			// TODO Calcular Iteraciones en función del Area del Poligono
			FloatArray bsplineVertices = calcularBSpline(puntos, 3, NUM_BSPLINE_VERTICES);
			
			poligonoSimple = calcularPoligonoSimple(bsplineVertices, false).size == 0;
			if(poligonoSimple)
			{
				Mesh m = calcularMeshGenerator(bsplineVertices, DEEP_TRIANGULATOR, MAX_LONG_EDGE_TRIANGULATOR);
				vertices = m.getVertices();
				triangulos = m.getTriangulos();
				
				contorno = new HullArray(NUM_BSPLINE_VERTICES);
				
				for(short i = 0; i < NUM_BSPLINE_VERTICES; i++)
				{
					contorno.addVertex(i);
				}
			}
		}
	}
	
	/* SECTION Métodos de Obtención de Información */
	
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
	
	/* SECTION Métodos Públicos Estáticos */
	
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
	
	public static Mesh calcularMeshGenerator(FloatArray vertices, int profundidad, float longitud)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices, profundidad, longitud);
	}
	
	public static ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
}
