package com.lib.math;

import com.android.storage.ExternalStorageManager;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.utils.DelaunayMesh;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DelaunayMeshGenerator
{
	public DelaunayMesh computeMesh(FloatArray vertices)
	{	
		Polygon poligonoBSpline = new Polygon(vertices.items);
		float areaPoligonoBSpline = Math.abs(poligonoBSpline.area());
		
ExternalStorageManager.writeLogcat("TEST", "New Delaunay Mesh Generator.");
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		
		FloatArray delaunayPuntos = vertices.clone();
		ShortArray delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);

		boolean meshChanged = true;
ExternalStorageManager.writeLogcat("TEST", "Generating Mesh.");

		while(meshChanged)
		{
ExternalStorageManager.writeLogcat("TEST", "Mesh has change.");

			meshChanged = false;
			
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
				
				if (Math.abs(poligono.area()) > areaPoligonoBSpline / 50)
				{
					Vector2 centroGravedad = GeometryUtils.triangleCentroid(vert[0], vert[1], vert[2], vert[3], vert[4], vert[5], new Vector2());
					
					if (Intersector.isPointInTriangle(centroGravedad.x, centroGravedad.y, vert[0], vert[1], vert[2], vert[3], vert[4], vert[5]))
					{
						delaunayPuntos.add(centroGravedad.x);
						delaunayPuntos.add(centroGravedad.y);
						
						meshChanged = true;
					}
				}
				
				j = j + 3;
			}
			
			delaunayCalculator = new DelaunayTriangulator();
			delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		}
		
ExternalStorageManager.writeLogcat("TEST", "Mesh Generation completed.");
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);
ExternalStorageManager.writeLogcat("TEST", "Mesh Generator triming outside triangles.");
		return new DelaunayMesh(new VertexArray(delaunayPuntos), new TriangleArray(delaunayTriangulos));
	}
}
