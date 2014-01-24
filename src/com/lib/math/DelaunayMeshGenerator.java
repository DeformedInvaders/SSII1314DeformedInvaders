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
				
				if(2*longitud <= puntos.distancia(p1, p2)) {
					float ax = (puntos.get(p1).getX() + puntos.get(p2).getX())/2.0f;
					float ay = (puntos.get(p1).getY() + puntos.get(p2).getY())/2.0f;
					puntos.add(new Coordinate(ax, ay));
				}
				
				if(2*longitud <= puntos.distancia(p2, p3)) {
					float bx = (puntos.get(p2).getX() + puntos.get(p3).getX())/2.0f;
					float by = (puntos.get(p2).getY() + puntos.get(p3).getY())/2.0f;
					puntos.add(new Coordinate(bx, by));
				}
				
				if(2*longitud <= puntos.distancia(p3, p1)) {
					float cx = (puntos.get(p3).getX() + puntos.get(p1).getX())/2.0f;
					float cy = (puntos.get(p3).getY() + puntos.get(p1).getY())/2.0f;
					puntos.add(new Coordinate(cx, cy));
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
