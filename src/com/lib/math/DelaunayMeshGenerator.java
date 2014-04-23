package com.lib.math;

import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;

public class DelaunayMeshGenerator
{
	// FIXME Calcular area dependiendo de la pantalla.
	private final static float AREA_TRIANG = 700.0f;

	public Mesh computeMesh(FloatArray vertices, int profundidad, float longitud)
	{	
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		
		FloatArray delaunayPuntos = vertices.clone();
		ShortArray delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);

		for (int i = 1; i < profundidad; i++)
		{
			Polygon poligono = new Polygon();
			float[] vert = new float[6];
			
			int j = 0;
			while (j < delaunayTriangulos.size)
			{	
				short a = delaunayTriangulos.get(j);
				short b = delaunayTriangulos.get(j + 1);
				short c = delaunayTriangulos.get(j + 2);
				
				vert[0] = delaunayPuntos.get(2 * a);
				vert[1] = delaunayPuntos.get(2 * a + 1);
				vert[2] = delaunayPuntos.get(2 * b);
				vert[3] = delaunayPuntos.get(2 * b + 1);
				vert[4] = delaunayPuntos.get(2 * c);
				vert[5] = delaunayPuntos.get(2 * c + 1);
				
				poligono.setVertices(vert);
				
				if (Math.abs(poligono.area()) > AREA_TRIANG)
				{
					Vector2 centroGravedad = GeometryUtils.triangleCentroid(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5], new Vector2());
					
					if (Intersector.isPointInTriangle(centroGravedad.x, centroGravedad.y, vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]))
					{
						delaunayPuntos.add(centroGravedad.x);
						delaunayPuntos.add(centroGravedad.y);
					}
				}
				
				j = j + 3;
			}
			
			delaunayCalculator = new DelaunayTriangulator();
			delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
			delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);
		}
		
		return new Mesh(new VertexArray(delaunayPuntos), new TriangleArray(delaunayTriangulos));
	}
}
