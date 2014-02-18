package com.lib.math;

import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;

public class Triangulator
{
	private final static int NUM_BSPLINE_VERTICES = 60;
	private final static int DEEP_TRIANGULATOR = 4;
	private final static float MAX_LONG_EDGE_TRIANGULATOR = 100.0f;
	
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;
	private boolean poligonoSimple;
	
	public Triangulator(FloatArray puntos)
	{
		poligonoSimple = false;
		
		if(puntos.size > 4)
		{			
			// TODO Calcular Iteraciones en función del Area del Poligono
			FloatArray bsplineVertices = calcularBSpline(puntos, 3, NUM_BSPLINE_VERTICES);
			
			poligonoSimple = calcularPoligonoSimple(bsplineVertices, false).size == 0;
			if(poligonoSimple)
			{
				Mesh m = calcularMeshGenerator(bsplineVertices, DEEP_TRIANGULATOR, MAX_LONG_EDGE_TRIANGULATOR);
				vertices = m.getVertices();
				triangulos = m.getTriangulos();
				
				contorno = new ShortArray(NUM_BSPLINE_VERTICES);
				for(int i = 0; i < NUM_BSPLINE_VERTICES; i++) contorno.add(i);
			}
		}
	}
	
	public boolean getPoligonSimple()
	{
		return poligonoSimple;
	}
	
	public FloatArray getVertices()
	{
		return vertices;
	}
	
	public ShortArray getTriangulos()
	{
		return triangulos;
	}
	
	public ShortArray getContorno()
	{
		return contorno;
	}
	
	private FloatArray calcularBSpline(FloatArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	private Mesh calcularMeshGenerator(FloatArray vertices, int profundidad, float longitud)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices, profundidad, longitud);
	}
	
	private ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}

}
