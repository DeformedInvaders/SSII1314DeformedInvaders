package com.example.math;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.matrix.Matrix;
import com.example.matrix.QRDecomposition;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class Deformator
{
	private ArrayList<Arista> aristas;
	private FloatArray puntos, handles;
	private ShortArray triangulos, indiceHandles, vecinos;
	
	private int numVertices, numAristas, numHandles;
	private static final float w = 1000;
	
	private Matrix matrizA, matrizB;
	
	public Deformator(FloatArray puntos, ShortArray triangulos, FloatArray handles, ShortArray indiceHandles)
	{
		this.puntos = puntos.clone();
		this.triangulos = triangulos;
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		
		this.aristas = new ArrayList<Arista>();
		this.vecinos = computeTriangles();
		
		this.numVertices = puntos.size/2;
		this.numAristas = vecinos.size/4;
		this.numHandles = indiceHandles.size;
		
		this.matrizA = calcularMatrizA();
		this.matrizB = calcularMatrizB();
	}
	
	// Obtener nuevos puntos
	private FloatArray computeDeformation()
	{
		Matrix matrizAt = matrizA.transpose();
		Matrix matrizAtA = matrizAt.times(matrizA);
		
		Matrix matrizAtB = matrizAt.times(matrizB);
		
		QRDecomposition solver = new QRDecomposition(matrizAtA); 
		Matrix m = solver.solve(matrizAtB);
		
		FloatArray nuevosPuntos = new FloatArray();
		for(int i = 0; i < 2*numVertices; i++)
		{
			nuevosPuntos.add((float) m.get(i, 0));
		}
		
		return nuevosPuntos;
	}
	
	// Modificación de Posición de Handles
	public FloatArray computeDeformation(FloatArray handles)
	{
		this.handles = handles;
		
		this.matrizB = calcularMatrizB();
		
		return computeDeformation();
	}
	
	// Añadir nuevos Handles
	public void computeDeformation(FloatArray handles, ShortArray indiceHandles)
	{
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		this.numHandles = this.indiceHandles.size;
		
		this.matrizA = actualizarMatrizA();
		this.matrizB = calcularMatrizB();
	}
	
	// Calculo Matriz H
	private Matrix calcularMatrizG(int a, int b, int c, int d)
	{
		float vix = puntos.get(2*a);
		float viy = puntos.get(2*a+1);
		
		float vjx = puntos.get(2*b);
		float vjy = puntos.get(2*b+1);
		
		float vlx = 0;
		float vly = 0;
		
		float vrx = 0;
		float vry = 0;
		
		if(c != -1)
		{
			vlx = puntos.get(2*c);
			vly = puntos.get(2*c+1);
		}
		
		if(d != -1)
		{
			vrx = puntos.get(2*d);
			vry = puntos.get(2*d+1);
		}
		
		Matrix m = new Matrix(8, 2);
		
		m.set(0, 0, vix);	m.set(0, 1, viy);
		m.set(1, 0, viy);	m.set(1, 1, -vix);
		
		m.set(2, 0, vjx);	m.set(2, 1, vjy);
		m.set(3, 0, vjy);	m.set(3, 1, -vjx);
		
		m.set(4, 0, vlx);	m.set(4, 1, vly);
		m.set(5, 0, vly);	m.set(5, 1, -vlx);
	
		m.set(6, 0, vrx);	m.set(6, 1, vry);
		m.set(7, 0, vry);	m.set(7, 1, -vrx);
		
		return m;
	}
	
	private Matrix calcularMatrizE(int a, int b)
	{
		float vix = puntos.get(2*a);
		float viy = puntos.get(2*a+1);
		float vjx = puntos.get(2*b);
		float vjy = puntos.get(2*b+1);
		
		float ex = vjx - vix;
		float ey = vjy - viy;
		
		Matrix m = new Matrix(2, 2);
		
		m.set(0, 0, ex);		m.set(0, 1, ey);
		m.set(1, 0, ey);		m.set(1, 1, -ex);
		
		return m;
	}
	
	private Matrix calcularMatrizH(int a, int b, int c, int d)
	{
		Matrix h1 = new Matrix(2, 8);
		
		h1.set(0, 0, -1);
		h1.set(0, 2, 1);
		h1.set(1, 1, -1);
		h1.set(1, 3, 1);
		
		Matrix g = calcularMatrizG(a, b, c, d);
		Matrix gt = g.transpose();
		Matrix gtg = gt.times(g);
		Matrix gtginv = gtg.inverse();
		
		Matrix e = calcularMatrizE(a, b);
		Matrix eginv = e.times(gtginv);
		
		Matrix h2 = eginv.times(gt);
		
		return h1.minus(h2);
	}
	
	// Calculo Matriz A
	private Matrix calcularMatrizA()
	{
		Matrix m = new Matrix(2*numAristas + 2*numHandles, 2*numVertices);
		
		int i = 0;
		int j = 0;
		while(i < vecinos.size)
		{
			int a = vecinos.get(i);
			int b = vecinos.get(i+1);
			int c = vecinos.get(i+2);
			int d = vecinos.get(i+3);
			
			Matrix h = calcularMatrizH(a, b, c, d);
			
			m.set(j, 2*a, h.get(0, 0));		m.set(j, 2*a+1, h.get(0, 1));
			m.set(j+1, 2*a, h.get(1, 0));	m.set(j+1, 2*a+1, h.get(1, 1));
			
			m.set(j, 2*b, h.get(0, 2));		m.set(j, 2*b+1, h.get(0, 3));
			m.set(j+1, 2*b, h.get(1, 2));	m.set(j+1, 2*b+1, h.get(1, 3));
			
			if(c != -1)
			{
				m.set(j, 2*c, h.get(0, 4));		m.set(j, 2*c+1, h.get(0, 5));
				m.set(j+1, 2*c, h.get(1, 4));	m.set(j+1, 2*c+1, h.get(1, 5));
			}
			
			if(d != -1)
			{
				m.set(j, 2*d, h.get(0, 6));		m.set(j, 2*d+1, h.get(0, 7));
				m.set(j+1, 2*d, h.get(1, 6));	m.set(j+1, 2*d+1, h.get(1, 7));
			}
			
			i = i + 4;
			j = j + 2;
		}
		
		for(int k = 0; k < numHandles; k++)
		{
			int pos = 2*numAristas + 2*k;
			int h = indiceHandles.get(k);
			
			m.set(pos, 2*h, w);		m.set(pos+1, 2*h+1, w);
		}
		
		return m;
	}
	
	// Actualizar Matriz A
	private Matrix actualizarMatrizA()
	{
		Matrix m = new Matrix(2*numAristas + 2*numHandles, 2*numVertices);
		
		
		for(int i = 0; i < 2*numAristas; i++)
		{
			for(int j = 0; j < 2*numVertices; j++)
			{
				m.set(i, j, matrizA.get(i, j));
			}
		}
		
		for(int k = 0; k < numHandles; k++)
		{
			int pos = 2*numAristas + 2*k;
			int h = indiceHandles.get(k);
			
			m.set(pos, 2*h, w);		m.set(pos+1, 2*h+1, w);
		}
		
		return m;
	}
	// Calculo Matriz B
	
	private Matrix calcularMatrizB()
	{
		Matrix m = new Matrix(2*numAristas + 2*numHandles, 1);
		
		for(int i = 0; i < numHandles; i++)
		{
			int pos = 2*numAristas + 2*i;
			float x = handles.get(2*i);
			float y = handles.get(2*i+1);
			
			m.set(pos, 0, w*x);
			m.set(pos+1, 0, w*y);
		}
		
		return m;
	}
	
	private ShortArray computeTriangles()
	{
		int i = 0;
		while(i < triangulos.size)
		{
			int a = triangulos.get(i);
			int b = triangulos.get(i+1);
			int c = triangulos.get(i+2);
			
			anyadirArista(a, b, c);
			anyadirArista(b, c, a);
			anyadirArista(c, a, b);
			
			i = i + 3;
		}
		
		ShortArray vecinos = new ShortArray();
		
		Iterator<Arista> it = aristas.iterator();
		while(it.hasNext())
		{
			Arista arista = it.next();
			vecinos.add(arista.getVerticeA());
			vecinos.add(arista.getVerticeB());
			vecinos.add(arista.getVecinoL());
			vecinos.add(arista.getVecinoR());
		}
		
		return vecinos;
	}
	
	private boolean anyadirArista(int a, int b, int c)
	{
		Iterator<Arista> it = aristas.iterator();
		while(it.hasNext())
		{
			Arista arista = it.next();
			if(arista.equals(a, b))
			{
				if(arista.getVecinoL() == -1)
				{
					arista.setVecinoL(c);
				}
				else
				{
					arista.setVecinoR(c);
				}
				return false;
			}
		}
		
		aristas.add(new Arista(a, b, c, puntos));
		return true;
	}
	
	private class Arista
	{		
		private int verticeA;
		private int verticeB;
		
		private int vecinoR;
		private int vecinoL;
		
		public Arista(int a, int b, int c, FloatArray puntos)
		{
			this.verticeA = a;
			this.verticeB = b;
			this.vecinoR = -1;
			this.vecinoL = -1;
			
			int lado = Intersector.pointLineSide(puntos.get(2*a), puntos.get(2*a+1), puntos.get(2*b), puntos.get(2*b+1), puntos.get(2*c), puntos.get(2*c+1));
			
			if(lado == -1)
			{
				this.vecinoL = c;
			}
			else if(lado == 1)
			{
				this.vecinoR = c;
			}
		}

		public int getVerticeA()
		{
			return verticeA;
		}

		public int getVerticeB()
		{
			return verticeB;
		}
		
		public int getVecinoR()
		{
			return vecinoR;
		}
		
		public int getVecinoL()
		{
			return vecinoL;
		}

		public void setVecinoR(int r)
		{
			this.vecinoR = r;
		}	
		
		public void setVecinoL(int l)
		{
			this.vecinoL = l;
		}
		
		public boolean equals(int a, int b)
		{
			return (verticeA == a && verticeB == b) || (verticeA == b && verticeB == a);			
		}
		
		public String toString()
		{
			if(vecinoR == -1)
			{
				return "A: "+verticeA+" B: "+verticeB+" L: "+vecinoL;
			}
			else if(vecinoL == -1)
			{
				return "A: "+verticeA+" B: "+verticeB+" R: "+vecinoR;
			}
			else
			{
				return "A: "+verticeA+" B: "+verticeB+" L: "+vecinoL+" R: "+vecinoR;
			}
		}
	}
}
