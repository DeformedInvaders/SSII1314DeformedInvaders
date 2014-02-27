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
	//				if(i%2==0){	// Si se quiere que haya menos puntos usamos solo el añadir el centro de los triangulos en las iteraciones pares
					
					Coordinate cg = new Coordinate();
					cg = centroGravedad(p1,p2,p3);
					puntos.add(cg);
					
					/*float cgx = (puntos.get(p1).getX() + puntos.get(p2).getX() + puntos.get(p3).getX())/3;
					float cgy = (puntos.get(p1).getY() + puntos.get(p2).getY() + puntos.get(p3).getY())/3;
					
					puntos.add(new Coordinate(cgx,cgy));*/
	//				}
				}
				
				/* Version anterior
				 * 
				 * 
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
				*/
					
				/*	Modificaciones para que haya muchos puntos
				 * 
				 * float area = areaTriangulo(p1,p2,p3);
				System.out.println("area: "+area);
				if(area > 1000.0){
					System.out.println("INNNNNN: ");
					if(4*longitud <= puntos.distancia(p1, p2)) {
						float ax = (puntos.get(p1).getX() + puntos.get(p2).getX())/2.0f;
						float ay = (puntos.get(p1).getY() + puntos.get(p2).getY())/2.0f;
						float ax2 = (puntos.get(p1).getX() + ax)/2.0f;
						float ay2 = (puntos.get(p1).getY() + ay)/2.0f;
						float ax3 = (puntos.get(p2).getX() + ax)/2.0f;
						float ay3 = (puntos.get(p2).getY() + ay)/2.0f;
						puntos.add(new Coordinate(ax, ay));
						puntos.add(new Coordinate(ax2, ay2));
						puntos.add(new Coordinate(ax3, ay3));
					}
					
					if(4*longitud <= puntos.distancia(p2, p3)) {
						
						float bx = (puntos.get(p2).getX() + puntos.get(p3).getX())/2.0f;
						float by = (puntos.get(p2).getY() + puntos.get(p3).getY())/2.0f;
						float bx2 = (puntos.get(p2).getX() + bx)/2.0f;
						float by2 = (puntos.get(p2).getY() + by)/2.0f;
						float bx3 = (puntos.get(p3).getX() + bx)/2.0f;
						float by3 = (puntos.get(p3).getY() + by)/2.0f;
						puntos.add(new Coordinate(bx,  by));
						puntos.add(new Coordinate(bx2, by2));
						puntos.add(new Coordinate(bx3, by3));
					}
					
					if(4*longitud <= puntos.distancia(p3, p1)) {
	
						float cx = (puntos.get(p3).getX() + puntos.get(p1).getX())/2.0f;
						float cy = (puntos.get(p3).getY() + puntos.get(p1).getY())/2.0f;
						float cx2 = (puntos.get(p3).getX() + cx)/2.0f;
						float cy2 = (puntos.get(p3).getY() + cy)/2.0f;
						float cx3 = (puntos.get(p1).getX() + cx)/2.0f;
						float cy3 = (puntos.get(p1).getY() + cy)/2.0f;
						puntos.add(new Coordinate(cx,  cy));
						puntos.add(new Coordinate(cx2, cy2));
						puntos.add(new Coordinate(cx3, cy3));
					}	
					
					i = i+3;
				}
				else{
					i = i+3;
				}	*/
				
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
