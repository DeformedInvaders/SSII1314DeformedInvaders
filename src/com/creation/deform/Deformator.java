package com.creation.deform;

import java.util.ArrayList;
import java.util.Iterator;

import com.lib.math.Intersector;
import com.lib.matrix.Matrix;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Deformator
{
	/*private boolean escaladoActivado;*/
	
	private ArrayList<Arista> aristas;
	private FloatArray puntos;
	private ShortArray triangulos;
	private ShortArray vecinos;
	
	private int numVertices, numAristas, numHandles;
	private static final float w = 1000;
	
	// Cálculo de Matrices A1 y B1
	
	private Matrix matrizG, matrizGt, matrizGtG;
	
	private Matrix matrizE, matrizEGtGinv;
	
	private Matrix matrizh1, matrizh2;
	private Matrix matrizH;
	
	private Matrix matrizA1, matrizA1t, matrizA1tA1;
	private Matrix matrizB1;
	
	// Cálculo de Matrices A2 y B2
	
	/*private Matrix matrizA2, matrizA2t, matrizA2tA2;*/
	
	/* SECTION Constructora */
	
	public Deformator(FloatArray puntos, ShortArray triangulos, FloatArray handles, ShortArray indiceHandles)
	{
		/*this.escaladoActivado = false;*/
		
		this.puntos = puntos.clone();
		this.triangulos = triangulos;
		
		this.aristas = new ArrayList<Arista>();
		this.vecinos = computeTriangles();
		
		this.numVertices = puntos.size/2;
		this.numAristas = vecinos.size/4;
		this.numHandles = indiceHandles.size;
		
		// Calcular Matriz H		
		this.matrizH = new Matrix(2, 8);
		this.matrizh1 = new Matrix(2, 8);
		this.matrizh1.set(0, 0, -1);
		this.matrizh1.set(0, 2, 1);
		this.matrizh1.set(1, 1, -1);
		this.matrizh1.set(1, 3, 1);
		
		this.matrizh2 = new Matrix(2, 8);
		
		this.matrizG = new Matrix(8, 2);
		this.matrizGt = new Matrix(2, 8);
		this.matrizGtG = new Matrix(2, 2);
		
		this.matrizE = new Matrix(2, 2);
		this.matrizEGtGinv = new Matrix(2, 2);
		
		// Calcular MatrizA1
		this.matrizA1 = calcularMatrizA1(puntos, handles, indiceHandles);
		this.matrizA1t = matrizA1.transpose();
		this.matrizA1tA1 = matrizA1t.times(matrizA1);
		
		// Calcular MatrizB1
		this.matrizB1 = calcularMatrizB1(handles);
		
		// Calcular MatrizA2
		/*this.matrizA2 = calcularMatrizA2(indiceHandles);
		this.matrizA2t = matrizA2.transpose();
		this.matrizA2tA2 = matrizA2t.times(matrizA2);*/
	}
	
	// Modificación de Posición de Handles
	public void moverHandles(FloatArray handles, FloatArray vertices)
	{
		// Actualizar MatrizB
		actualizarMatrizB1(handles);
		
		// Cálculo de Ajuste de Traslación y Rotación
		//
		// A1t * A1 * X = A1t * B1
		
		Matrix matrizA1tB1 = matrizA1t.times(matrizB1);
		
		Matrix m = matrizA1tA1.solve(matrizA1tB1);
		
		// Actualizar Valores de los Vertices
		for(int i = 0; i < 2*numVertices; i++)
		{
			vertices.set(i, (float) m.get(i, 0));
		}
		
		// Cálculo de Ajuste de Escalación
		//
		// A2t * A2 * X = A2t * B2
		
		/*if(escaladoActivado)
		{		
			Matrix matrizB2 = calcularMatrizB2(vertices, handles);
			Matrix matrizB2x = matrizB2.getMatrix(0, matrizB2.getRowDimension() - 1, 0, 0);
			Matrix matrizB2y = matrizB2.getMatrix(0, matrizB2.getRowDimension() - 1, 1, 1);
			
			Matrix matrizA2tB2x = matrizA2t.times(matrizB2x);
			Matrix matrizA2tB2y = matrizA2t.times(matrizB2y);
			
			Matrix m2x = matrizA2tA2.solve(matrizA2tB2x);
			Matrix m2y = matrizA2tA2.solve(matrizA2tB2y);
			
			// Actualizar Valores de los Vertices
			for(int i = 0; i < numVertices; i++)
			{
				vertices.set(2*i, (float) m2x.get(i, 0));
				vertices.set(2*i+1, (float) m2y.get(i, 0));
			}
		}*/
	}
	
	// Añadir nuevos Handles
	public void anyadirHandles(FloatArray handles, ShortArray indiceHandles)
	{
		this.numHandles = indiceHandles.size;
		
		// Calcular MatrizA1
		this.matrizA1 = actualizarMatrizA1(handles, indiceHandles);
		this.matrizA1t = matrizA1.transpose();
		this.matrizA1tA1 = matrizA1t.times(matrizA1);
		
		// Calcular MatrizB1
		this.matrizB1 = calcularMatrizB1(handles);
		
		// Calcular MatrizA2
		/*this.matrizA2 = actualizarMatrizA2(indiceHandles);
		this.matrizA2t = matrizA2.transpose();
		this.matrizA2tA2 = matrizA2t.times(matrizA2);*/
	}
	
	// Borrar Handles
	public void eliminarHandles(FloatArray handles, ShortArray indiceHandles)
	{
		anyadirHandles(handles, indiceHandles);
	}
	
	/* Ajuste de Traslación y Rotación */
	
	// Cálculo Matriz G
	//
	//		|	vix		viy		|
	//		|	viy		-vix	|
	//		|	vjx		vjy		|
	// G =	|	vjy		-vjx	|
	//		|	vlx		vly		|
	//		|	vly		-vlx	|
	//		|	vrx		vry		|
	//		|	vry		-vrx	|
	private void calcularMatrizG(int a, int b, int c, int d, FloatArray puntos, Matrix m)
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
				
		m.set(0, 0, vix);	m.set(0, 1, viy);
		m.set(1, 0, viy);	m.set(1, 1, -vix);
		
		m.set(2, 0, vjx);	m.set(2, 1, vjy);
		m.set(3, 0, vjy);	m.set(3, 1, -vjx);
		
		m.set(4, 0, vlx);	m.set(4, 1, vly);
		m.set(5, 0, vly);	m.set(5, 1, -vlx);
	
		m.set(6, 0, vrx);	m.set(6, 1, vry);
		m.set(7, 0, vry);	m.set(7, 1, -vrx);
		
	}
	
	// Cálculo Matriz E
	//
	// E =	|	ekx		eky		|
	//		|	eky		-ekx	|
	private void calcularMatrizE(int a, int b, FloatArray puntos, Matrix m)
	{
		float vix = puntos.get(2*a);
		float viy = puntos.get(2*a+1);
		float vjx = puntos.get(2*b);
		float vjy = puntos.get(2*b+1);
		
		float ex = vjx - vix;
		float ey = vjy - viy;
		
		m.set(0, 0, ex);		m.set(0, 1, ey);
		m.set(1, 0, ey);		m.set(1, 1, -ex);
	}
	
	//	Cálculo Matriz H
	//
	//	H =	|	-1	0	1	0	0	0	0	0	|	-	E	*	(Gtk*Gk)^-1	* Gtk	= 
	//		|	0	-1	0	1	0	0	0	0	|
	//
	//	=	|	Hk00	Hk10	Hk20	Hk30	Hk40	Hk50	Hk60	Hk70	|
	//		|	Hk01	Hk11	Hk21	Hk31	Hk41	Hk51	Hk61	Hk71	|
	private void calcularMatrizH(int a, int b, int c, int d, FloatArray puntos, Matrix m)
	{
		// Matriz h1 constante inicializada en la constructora.
		
		// Matriz h2
		
		calcularMatrizG(a, b, c, d, puntos, matrizG);
		
		matrizG.transpose(matrizGt);
		matrizGtG = matrizGt.times(matrizG);
		Matrix gtginv = matrizGtG.inverse();
		
		calcularMatrizE(a, b, puntos, matrizE);
		
		matrizE.times(gtginv, matrizEGtGinv);
		matrizEGtGinv.times(matrizGt, matrizh2);
		
		// Matriz H
		matrizh1.minus(matrizh2, m);
	}
	
	// Cálculo Matriz A1
	//
	//		|	h020	h030								|
	//		|	h021	h031								|
	//		|					h100	h110				|	|	2*NumAristas
	//		|					h101	h111				|	V
	//		|						...						|
	// A1 =	|______________________________________ _ _ _ 	|
	//		|					w							|
	//		|							w					|	|	2*NumHandles
	//		|						...						|	V
	//							-> 2*NumVertices
	private Matrix calcularMatrizA1(FloatArray puntos, FloatArray handles, ShortArray indiceHandles)
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
			
			calcularMatrizH(a, b, c, d, puntos, matrizH);
			
			m.set(j, 2*a, matrizH.get(0, 0));		m.set(j, 2*a+1, matrizH.get(0, 1));
			m.set(j+1, 2*a, matrizH.get(1, 0));		m.set(j+1, 2*a+1, matrizH.get(1, 1));
			
			m.set(j, 2*b, matrizH.get(0, 2));		m.set(j, 2*b+1, matrizH.get(0, 3));
			m.set(j+1, 2*b, matrizH.get(1, 2));		m.set(j+1, 2*b+1, matrizH.get(1, 3));
			
			if(c != -1)
			{
				m.set(j, 2*c, matrizH.get(0, 4));		m.set(j, 2*c+1, matrizH.get(0, 5));
				m.set(j+1, 2*c, matrizH.get(1, 4));		m.set(j+1, 2*c+1, matrizH.get(1, 5));
			}
			
			if(d != -1)
			{
				m.set(j, 2*d, matrizH.get(0, 6));		m.set(j, 2*d+1, matrizH.get(0, 7));
				m.set(j+1, 2*d, matrizH.get(1, 6));		m.set(j+1, 2*d+1, matrizH.get(1, 7));
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
	
	// Actualizar Matriz A1
	private Matrix actualizarMatrizA1(FloatArray handles, ShortArray indiceHandles)
	{
		Matrix m = new Matrix(2*numAristas + 2*numHandles, 2*numVertices);
		
		for(int i = 0; i < 2*numAristas; i++)
		{
			for(int j = 0; j < 2*numVertices; j++)
			{
				m.set(i, j, matrizA1.get(i, j));
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
	
	// Cálculo Matriz B1
	//
	//		|	0		|
	//		|	0		|
	//		|	0		|	|	2*NumAristas
	//		|	0		|	V
	//		|	...		|
	// B1 =	|__________	|
	//		|	wc0x	|
	//		|	wc0y	|	|	2*NumHandles
	//		|	wc1x	|	V				
	//		|	...		|
	private Matrix calcularMatrizB1(FloatArray handles)
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
	
	// Actualizar Matriz B1
	private void actualizarMatrizB1(FloatArray handles)
	{
		for(int i = 0; i < numHandles; i++)
		{
			int pos = 2*numAristas + 2*i;
			float x = handles.get(2*i);
			float y = handles.get(2*i+1);
			
			matrizB1.set(pos, 0, w*x);
			matrizB1.set(pos+1, 0, w*y);
		}
	}
	
	/* Ajuste de Escala */
	
	// Cálculo Matriz A2
	//
	//		|	1		-1			|
	//		|		-1	1			|	| 	NumAristas
	//		|		...				|	V
	// A2 =	|_______________ _ _ _	|
	//		|			w			|
	//		|	w					|	|	NumHandles
	//		|		...				|	V
	//				-> NumVertices
	/*private Matrix calcularMatrizA2(ShortArray indiceHandles)
	{
		Matrix m = new Matrix(numAristas + numHandles, numVertices);
		
		int i = 0;
		int j = 0;
		while(i < vecinos.size)
		{
			int a = vecinos.get(i);
			int b = vecinos.get(i+1);
						
			m.set(j, a, 1);			
			m.set(j, b, -1);
			
			i = i + 4;
			j = j + 1;
		}
		
		for(int k = 0; k < numHandles; k++)
		{
			int pos = numAristas + k;
			int h = indiceHandles.get(k);
			
			m.set(pos, h, w);
		}
		
		return m;
	}*/
	
	// Actualizar Matriz A2
	/*private Matrix actualizarMatrizA2(ShortArray indiceHandles)
	{
		Matrix m = new Matrix(numAristas + numHandles, numVertices);
		
		for(int i = 0; i < numAristas; i++)
		{
			for(int j = 0; j < numVertices; j++)
			{
				m.set(i, j, matrizA2.get(i, j));
			}
		}
		
		for(int k = 0; k < numHandles; k++)
		{
			int pos = numAristas + k;
			int h = indiceHandles.get(k);
			
			m.set(pos, h, w);
		}
		
		return m;
	}*/
	
	// Cálculo Matriz V
	//
	//		|	Vix	|
	//		|	Viy	|
	//		|	Vjx	|
	// V =	|	Vjy	|
	//		|	Vlx	|
	//		|	Vly	|
	//		|	Vrx	|
	//		|	Vry	|
	/*private Matrix calcularMatrizV(int a, int b, int c, int d, FloatArray puntos)
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
		
		Matrix m = new Matrix(8, 1);
		
		m.set(0, 0, vix);
		m.set(1, 0, viy);
		
		m.set(2, 0, vjx);
		m.set(3, 0, vjy);
		
		m.set(4, 0, vlx);
		m.set(5, 0, vly);
	
		m.set(6, 0, vrx);
		m.set(7, 0, vry);
		
		return m;
	}*/
	
	// Cálculo Matriz T
	//
	//	|	ck	|	=	(Gtk*Gk)^-1 * Gtk * V
	//	|	sk	|
	//
	//	Tk = 1/(ck^2 + sk^2) *	|	ck		sk	|
	//							|	-sk		ck	|
	/*private Matrix calcularMatrizT(int a, int b, int c, int d, FloatArray puntos)
	{
		Matrix g = calcularMatrizG(a, b, c, d, puntos);
		Matrix gt = g.transpose();
		Matrix gtg = gt.times(g);
		Matrix gtginv = gtg.inverse();
		
		Matrix gfinal = gtginv.times(gt);
		
		Matrix v = calcularMatrizV(a, b, c, d, puntos);
		
		Matrix tk = gfinal.times(v);
		double ck = tk.get(0, 0);
		double sk = tk.get(1, 0);
		
		Matrix m = new Matrix(2, 2);
		m.set(0, 0, ck);	m.set(0, 1, sk);
		m.set(1, 0, -sk);	m.set(1, 1, ck);	
		
		double k = 1.0/(Math.pow(ck, 2) + Math.pow(sk, 2));
				
		return m.times(k);
	}*/
	
	// Cálculo Matriz B2
	//
	//		|	T0*e0|x		T0*e0|y		|
	//		|	T1*e1|x		T1*e1|y		|	|	NumAristas
	//		|			...				|	V
	// B2 =	|___________________________|
	//		|	wC0x		wC0y		|	|	NumHandles
	//		|	wC1x		wC1y		|	V
	//		|			...				|
	/*private Matrix calcularMatrizB2(FloatArray puntos, FloatArray handles)
	{
		Matrix m = new Matrix(numAristas + numHandles, 2);
		
		int i = 0;
		int j = 0;
		while(i < vecinos.size)
		{
			int a = vecinos.get(i);
			int b = vecinos.get(i+1);
			int c = vecinos.get(i+2);
			int d = vecinos.get(i+3);
			
			Matrix t = calcularMatrizT(a, b, c, d, puntos);
			
			float vix = puntos.get(2*a);
			float viy = puntos.get(2*a+1);
			float vjx = puntos.get(2*b);
			float vjy = puntos.get(2*b+1);
			
			float ex = vjx - vix;
			float ey = vjy - viy;
			
			double tex = t.get(0, 0) * ex + t.get(0, 1) * ey;
			double tey = t.get(1, 0) * ex + t.get(1, 1) * ey;		
			
			m.set(j, 0, tex);
			m.set(j, 1, tey);
			
			i = i+4;
			j = j+1;
		}

		for(int k = 0; k < numHandles; k++)
		{
			int pos = numAristas + k;
			float x = handles.get(2*k);
			float y = handles.get(2*k+1);
			
			m.set(pos, 0, w*x);
			m.set(pos, 1, w*y);
		}
		
		return m;
	}*/
	
	/* Cálculo de Vecinos */
	
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
	}
}
