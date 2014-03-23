package com.lib.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;

public class DelaunayMeshGenerator {
	
	CoordinateArray puntos;
	FloatArray delaunayPuntos;
	ShortArray delaunayTriangulos;
	
	private final static float AREA_TRIANG = 700.0f;

	public DelaunayMeshGenerator() {
		puntos = new CoordinateArray();
	}
	
	public Mesh computeMesh(FloatArray hull, int profundidad, float longitud) {
		
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		puntos.addAll(hull);
		
		delaunayPuntos = puntos.toFloatArray();
		delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, hull, 0, hull.size);
		puntos.clear();
		puntos.addAll(delaunayPuntos);
		
		for(int k = 1; k < profundidad; k++) {
	
			int i = 0;
			while(i < delaunayTriangulos.size) {
				
				int p1 = delaunayTriangulos.get(i);
				int p2 = delaunayTriangulos.get(i+1);
				int p3 = delaunayTriangulos.get(i+2);
				
				float area = areaTriangulo(p1,p2,p3);
				
				if(area>AREA_TRIANG){					
					Coordinate cg = new Coordinate();
					cg = centroGravedad(p1,p2,p3);
					puntos.add(cg);
				}	
				
				i = i+3;
			}
			
			delaunayCalculator = new DelaunayTriangulator();
			delaunayPuntos = puntos.toFloatArray();
			delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
			delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, hull, 0, hull.size);
			puntos.clear();
			puntos.addAll(delaunayPuntos);
		}
		
		return new Mesh(puntos.toFloatArray(), delaunayTriangulos);
	}
	
	public Mesh computeMesh(FloatArray hull, FloatArray auxVertices) {
		
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		puntos.addAll(hull);
		
		delaunayPuntos = puntos.toFloatArray();
		puntos.clear();
		puntos.addAll(delaunayPuntos);
		
				
		int i = 0;
		while(i < auxVertices.size) {
			
			float p1 = auxVertices.get(i);
			float p2 = auxVertices.get(i+1);
			

			Coordinate punto = new Coordinate(p1, p2);
			puntos.add(punto);
			i=i+2;
		}
		
		
		delaunayCalculator = new DelaunayTriangulator();
		delaunayPuntos = puntos.toFloatArray();
		delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, hull, 0, hull.size);
		
		puntos.clear();
		puntos.addAll(delaunayPuntos);
	
		
		return new Mesh(puntos.toFloatArray(), delaunayTriangulos);
	}
	
	private float areaTriangulo(int p1, int p2, int p3) {
		
		//P1P2
		float p1p2x = puntos.get(p2).getX() - puntos.get(p1).getX();
		float p1p2y = puntos.get(p2).getY() - puntos.get(p1).getY();
		
		//Vector perpendicular
		float np1p2x = - p1p2y;
		float np1p2y =  p1p2x;
		
		//P1P3
		float p1p3x = puntos.get(p3).getX() - puntos.get(p1).getX();
		float p1p3y = puntos.get(p3).getY() - puntos.get(p1).getY();
		
		//Area
		float area = (float)  0.5 * Math.abs((np1p2x*p1p3x) + (np1p2y*p1p3y));
		
		return area;
	}
	
	private Coordinate centroGravedad(int p1, int p2, int p3) {
		
		//Punto medio de P1,P2 y P3
		float cgx = (puntos.get(p1).getX() + puntos.get(p2).getX() + puntos.get(p3).getX())/3;
		float cgy = (puntos.get(p1).getY() + puntos.get(p2).getY() + puntos.get(p3).getY())/3;
		
		
		//Area
		Coordinate cg = new Coordinate(cgx,  cgy);
		
		return cg;
	}
	
	public float calcularAreaMesh(FloatArray vertices)
	{
	
		float areaTotal = 0;
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		puntos.addAll(vertices);
		delaunayPuntos = puntos.toFloatArray();
		delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);
		puntos.clear();
		puntos.addAll(delaunayPuntos);
		
		int i = 0;
		while(i < delaunayTriangulos.size) {
			
			int p1 = delaunayTriangulos.get(i);
			int p2 = delaunayTriangulos.get(i+1);
			int p3 = delaunayTriangulos.get(i+2);
			
			//Area
			areaTotal = areaTotal + areaTriangulo(p1, p2, p3);

			i=i+3;
		}
		delaunayCalculator = new DelaunayTriangulator();
		delaunayPuntos = puntos.toFloatArray();
		delaunayTriangulos = delaunayCalculator.computeTriangles(delaunayPuntos, false);
		delaunayCalculator.trim(delaunayTriangulos, delaunayPuntos, vertices, 0, vertices.size);
		
		puntos.clear();
		puntos.addAll(delaunayPuntos);
			
		return areaTotal;
	}
	
	
	public class Coordinate {
		float x, y;
		
		public Coordinate() {
			this(0.0f, 0.0f);
		}
		
		public Coordinate(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public float getX() {
			return x;
		}

		public void setX(float x) {
			this.x = x;
		}

		public float getY() {
			return y;
		}

		public void setY(float y) {
			this.y = y;
		}
		
		public boolean equals(Coordinate c) {
			float dx = Math.abs(c.getX() - x);
			float dy = Math.abs(c.getY() - y);
			
			return dx < 0.1 && dy < 0.1;
		}
	}
	
	public class CoordinateArray {
		List<Coordinate> array;
		
		public CoordinateArray() {
			this.array = new ArrayList<Coordinate>();
		}
		
		public CoordinateArray(FloatArray vertices) {
			this.array = new ArrayList<Coordinate>();
			
			int i = 0;
			while(i < vertices.size) {
				array.add(new Coordinate(vertices.get(i), vertices.get(i+1)));
				i = i+2;
			}
		}
		
		public boolean add(Coordinate c) {
			Iterator<Coordinate> it = array.iterator();
			while(it.hasNext()) {
				Coordinate cit = it.next();
				
				if(cit.equals(c)) return false;
			}
			return array.add(c);
		}
		
		public boolean addAll(FloatArray array) {
			int i = 0;
			while(i < array.size) {
				add(new Coordinate(array.get(i), array.get(i+1)));
				i = i+2;
			}
			return true;
		}
		
		public Coordinate get(int index) {
			return array.get(index);
		}
		
		public void clear() {
			array.clear();
		}
		
		public int size() {
			return array.size();
		}
		
		public float distancia(int a, int b)
		{
			Coordinate pa = get(a);
			Coordinate pb = get(b);
			return Intersector.distancePoints(pa.getX(), pa.getY(), pb.getX(), pb.getY());
		}
		
		public FloatArray toFloatArray()
		{
			FloatArray vertices = new FloatArray(2*array.size());
			
			Iterator<Coordinate> it = array.iterator();
			while(it.hasNext()) {
				Coordinate c = it.next();
				vertices.add(c.getX());
				vertices.add(c.getY());
			}
			
			return vertices;
		}		
	}
}
