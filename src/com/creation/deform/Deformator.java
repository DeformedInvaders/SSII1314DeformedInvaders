package com.creation.deform;

import com.android.storage.ExternalStorageManager;
import com.lib.buffer.EdgeArray;
import com.lib.buffer.HandleArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.matrix.Matrix;

public class Deformator
{
	private static final float w = 1000;
	
	private VertexArray mVertices;
	private TriangleArray mTriangles;
	private EdgeArray mEdges;

	private int numVertices, numEdges, numHandles;
	
	// Cálculo de Matrices A1 y B1

	private Matrix matrizG, matrizGt, matrizGtG;

	private Matrix matrizE, matrizEGtGinv;

	private Matrix matrizh1, matrizh2;
	private Matrix matrizH;

	private Matrix matrizA1, matrizA1t, matrizA1tA1;
	private Matrix matrizB1, matrizA1tB1;

	// Cálculo de Matrices A2 y B2

	private Matrix matrizGtGinvGt, matrizV;
	private Matrix matrizTk, matrizT;

	private Matrix matrizA2, matrizA2t, matrizA2tA2;
	private Matrix matrizB2x, matrizB2y, matrizA2tB2x, matrizA2tB2y;

	/* Constructora */

	public Deformator(VertexArray mesh, TriangleArray triangles, HandleArray handles)
	{
		mVertices = mesh;
		mTriangles = triangles;
		mEdges = new EdgeArray();
		
		buildEdges(mTriangles);

		numVertices = mVertices.getNumVertices();
		numEdges = mEdges.getNumEdges();

		// Calcular Matriz G
		matrizG = new Matrix(8, 2);
		matrizGt = new Matrix(2, 8);
		matrizGtG = new Matrix(2, 2);

		// Calcular Matriz H
		matrizH = new Matrix(2, 8);

		matrizh1 = new Matrix(2, 8);
		matrizh1.set(0, 0, -1);
		matrizh1.set(0, 2, 1);
		matrizh1.set(1, 1, -1);
		matrizh1.set(1, 3, 1);

		matrizh2 = new Matrix(2, 8);

		matrizE = new Matrix(2, 2);
		matrizEGtGinv = new Matrix(2, 2);

		// Calcular Matriz T
		matrizV = new Matrix(8, 1);
		matrizGtGinvGt = new Matrix(2, 8);
		matrizTk = new Matrix(2, 1);
		matrizT = new Matrix(2, 2);

		// Calcular MatrizA1
		matrizA1tA1 = new Matrix(2 * numVertices, 2 * numVertices);

		// Calcular MatrizA2
		matrizA2tA2 = new Matrix(numVertices, numVertices);

		// Calcular MatrizB1
		matrizA1tB1 = new Matrix(2 * numVertices, 1);

		// Calcular Matriz B2
		matrizA2tB2x = new Matrix(numVertices, 1);
		matrizA2tB2y = new Matrix(numVertices, 1);

		anyadirHandles(handles);
	}

	// Añadir nuevos Handles
	public void anyadirHandles(HandleArray handles)
	{
		numHandles = handles.getNumHandles();

		// Calcular MatrizA1
		matrizA1 = new Matrix(2 * numEdges + 2 * numHandles, 2 * numVertices);
		calcularMatrizA1(mVertices, handles, matrizA1);

		matrizA1t = new Matrix(2 * numVertices, 2 * numEdges + 2 * numHandles);
		matrizA1.transpose(matrizA1t);

		matrizA1t.times(matrizA1, matrizA1tA1);

		// Calcular MatrizB1
		matrizB1 = new Matrix(2 * numEdges + 2 * numHandles, 1);
		calcularMatrizB1(handles, matrizB1);

		// Calcular MatrizA2
		matrizA2 = new Matrix(numEdges + numHandles, numVertices);
		calcularMatrizA2(handles, matrizA2);

		matrizA2t = new Matrix(numVertices, numEdges + numHandles);
		matrizA2.transpose(matrizA2t);

		matrizA2t.times(matrizA2, matrizA2tA2);

		// Calcular Matriz B2
		matrizB2x = new Matrix(numEdges + numHandles, 1);
		matrizB2y = new Matrix(numEdges + numHandles, 1);
	}

	// Borrar Handles
	public void eliminarHandles(HandleArray handles)
	{
		anyadirHandles(handles);
	}

	// Modificación de Posición de Handles
	public void moverHandles(HandleArray handles, VertexArray verticesModified)
	{
		try
		{
			// Actualizar MatrizB
			calcularMatrizB1(handles, matrizB1);

			// Cálculo de Ajuste de Traslación y Rotación
			//
			// A1t * A1 * X = A1t * B1

			matrizA1t.times(matrizB1, matrizA1tB1);
			
			Matrix m1 = matrizA1tA1.solve(matrizA1tB1);
	
			// Actualizar Valores de los Vertices después del Ajuste de Traslación y
			// Rotación
			VertexArray verticesTrasRot = mVertices.clone();
			for (short i = 0; i < numVertices; i++)
			{
				verticesTrasRot.setVertex(i, (float) m1.get(2 * i, 0), (float) m1.get(2 * i + 1, 0));
			}
	
			// Cálculo de Ajuste de Escalación
			//
			// A2t * A2 * X = A2t * B2
	
			calcularMatrizB2(mVertices, verticesTrasRot, handles, matrizB2x, matrizB2y);
	
			matrizA2t.times(matrizB2x, matrizA2tB2x);
			matrizA2t.times(matrizB2y, matrizA2tB2y);
	
			Matrix m2x = matrizA2tA2.solve(matrizA2tB2x);
			Matrix m2y = matrizA2tA2.solve(matrizA2tB2y);
	
			// Actualizar Valores de los Vertices después del Ajuste de Escala
			for (short i = 0; i < numVertices; i++)
			{
				verticesModified.setVertex(i, (float) m2x.get(i, 0), (float) m2y.get(i, 0));
			}
		}
		catch(RuntimeException e)
		{
			ExternalStorageManager.writeLogcat("TEST", "RuntimeException: "+e.getMessage());
		}
	}

	/* Ajuste de Traslación y Rotación */

	// Cálculo Matriz G
	private void calcularMatrizG(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = 0;
		float vly = 0;

		float vrx = 0;
		float vry = 0;

		if (c != -1)
		{
			vlx = vertices.getXVertex(c);
			vly = vertices.getYVertex(c);
		}

		if (d != -1)
		{
			vrx = vertices.getXVertex(d);
			vry = vertices.getYVertex(d);
		}

		m.set(0, 0, vix);
		m.set(0, 1, viy);
		m.set(1, 0, viy);
		m.set(1, 1, -vix);

		m.set(2, 0, vjx);
		m.set(2, 1, vjy);
		m.set(3, 0, vjy);
		m.set(3, 1, -vjx);

		m.set(4, 0, vlx);
		m.set(4, 1, vly);
		m.set(5, 0, vly);
		m.set(5, 1, -vlx);

		m.set(6, 0, vrx);
		m.set(6, 1, vry);
		m.set(7, 0, vry);
		m.set(7, 1, -vrx);

	}

	// Cálculo Matriz E
	private void calcularMatrizE(short a, short b, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);
		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float ex = vjx - vix;
		float ey = vjy - viy;

		m.set(0, 0, ex);
		m.set(0, 1, ey);
		m.set(1, 0, ey);
		m.set(1, 1, -ex);
	}

	// Cálculo Matriz H
	private void calcularMatrizH(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		calcularMatrizG(a, b, c, d, vertices, matrizG);

		matrizG.transpose(matrizGt);
		matrizGtG = matrizGt.times(matrizG);
		Matrix gtginv = matrizGtG.inverse();

		calcularMatrizE(a, b, vertices, matrizE);

		matrizE.times(gtginv, matrizEGtGinv);
		matrizEGtGinv.times(matrizGt, matrizh2);

		// Matriz H
		matrizh1.minus(matrizh2, m);
	}

	// Cálculo Matriz A1
	private void calcularMatrizA1(VertexArray vertices, HandleArray handles, Matrix m)
	{
		int j = 0;
		for (short i = 0; i < numEdges; i++)
		{
			short a = mEdges.getAVertex(i);
			short b = mEdges.getBVertex(i);
			short c = mEdges.getLVertex(i);
			short d = mEdges.getRVertex(i);

			calcularMatrizH(a, b, c, d, vertices, matrizH);

			m.set(j, 2 * a, matrizH.get(0, 0));
			m.set(j, 2 * a + 1, matrizH.get(0, 1));
			m.set(j + 1, 2 * a, matrizH.get(1, 0));
			m.set(j + 1, 2 * a + 1, matrizH.get(1, 1));

			m.set(j, 2 * b, matrizH.get(0, 2));
			m.set(j, 2 * b + 1, matrizH.get(0, 3));
			m.set(j + 1, 2 * b, matrizH.get(1, 2));
			m.set(j + 1, 2 * b + 1, matrizH.get(1, 3));

			if (c != -1)
			{
				m.set(j, 2 * c, matrizH.get(0, 4));
				m.set(j, 2 * c + 1, matrizH.get(0, 5));
				m.set(j + 1, 2 * c, matrizH.get(1, 4));
				m.set(j + 1, 2 * c + 1, matrizH.get(1, 5));
			}

			if (d != -1)
			{
				m.set(j, 2 * d, matrizH.get(0, 6));
				m.set(j, 2 * d + 1, matrizH.get(0, 7));
				m.set(j + 1, 2 * d, matrizH.get(1, 6));
				m.set(j + 1, 2 * d + 1, matrizH.get(1, 7));
			}

			j = j + 2;
		}

		for (short k = 0; k < numHandles; k++)
		{
			int pos = 2 * numEdges + 2 * k;
			
			short triangle = handles.getIndexHandle(k);
			float alfa = handles.getAlfaHandle(k);
			float beta = handles.getBetaHandle(k);
			float gamma = handles.getGammaHandle(k);
			
			short a = mTriangles.getAVertex(triangle);
			short b = mTriangles.getBVertex(triangle);
			short c = mTriangles.getCVertex(triangle);

			m.set(pos, 2 * a, w * alfa);
			m.set(pos + 1, 2 * a + 1, w * alfa);
			
			m.set(pos, 2 * b, w * beta);
			m.set(pos + 1, 2 * b + 1, w * beta);
			
			m.set(pos, 2 * c, w * gamma);
			m.set(pos + 1, 2 * c + 1, w * gamma);
		}
	}

	// Cálculo Matriz B1
	private void calcularMatrizB1(HandleArray handles, Matrix m)
	{
		for (short i = 0; i < numHandles; i++)
		{
			int pos = 2 * numEdges + 2 * i;
			
			float x = handles.getXCoordHandle(i);
			float y = handles.getYCoordHandle(i);

			m.set(pos, 0, w * x);
			m.set(pos + 1, 0, w * y);
		}
	}

	/* Ajuste de Escala */

	// Cálculo Matriz A2
	private void calcularMatrizA2(HandleArray handles, Matrix m)
	{
		int j = 0;
		for (short i = 0; i < numEdges; i++)
		{
			int a = mEdges.getAVertex(i);
			int b = mEdges.getBVertex(i);

			m.set(j, a, 1);
			m.set(j, b, -1);

			j = j + 1;
		}

		for (short k = 0; k < numHandles; k++)
		{
			int pos = numEdges + k;
			
			short triangle = handles.getIndexHandle(k);
			float alfa = handles.getAlfaHandle(k);
			float beta = handles.getBetaHandle(k);
			float gamma = handles.getGammaHandle(k);
			
			short a = mTriangles.getAVertex(triangle);
			short b = mTriangles.getBVertex(triangle);
			short c = mTriangles.getCVertex(triangle);

			m.set(pos, a, w * alfa);
			m.set(pos, b, w * beta);
			m.set(pos, c, w * gamma);
		}
	}

	// Cálculo Matriz V
	private void calcularMatrizV(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = 0;
		float vly = 0;

		float vrx = 0;
		float vry = 0;

		if (c != -1)
		{
			vlx = vertices.getXVertex(c);
			vly = vertices.getYVertex(c);
		}

		if (d != -1)
		{
			vrx = vertices.getXVertex(d);
			vry = vertices.getYVertex(d);
		}

		m.set(0, 0, vix);
		m.set(1, 0, viy);

		m.set(2, 0, vjx);
		m.set(3, 0, vjy);

		m.set(4, 0, vlx);
		m.set(5, 0, vly);

		m.set(6, 0, vrx);
		m.set(7, 0, vry);
	}

	// Cálculo Matriz T
	private void calcularMatrizT(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		calcularMatrizG(a, b, c, d, vertices, matrizG);

		matrizG.transpose(matrizGt);
		matrizGtG = matrizGt.times(matrizG);
		Matrix gtginv = matrizGtG.inverse();

		gtginv.times(matrizGt, matrizGtGinvGt);

		calcularMatrizV(a, b, c, d, vertices, matrizV);

		matrizGtGinvGt.times(matrizV, matrizTk);
		
		// double ck = matrizTk.get(0, 0);
		double ck = -matrizTk.get(0, 0);
		double sk = matrizTk.get(1, 0);

		m.set(0, 0, ck);
		m.set(0, 1, sk);
		m.set(1, 0, -sk);
		m.set(1, 1, ck);

		double k = 1.0 / (Math.pow(ck, 2) + Math.pow(sk, 2));

		m.times(k);
	}

	// Cálculo Matriz B2
	private void calcularMatrizB2(VertexArray vertices, VertexArray verticesRotation, HandleArray handles, Matrix m, Matrix n)
	{
		int j = 0;
		for (short i = 0; i < numEdges; i++)
		{
			short a = mEdges.getAVertex(i);
			short b = mEdges.getBVertex(i);
			short c = mEdges.getLVertex(i);
			short d = mEdges.getRVertex(i);

			calcularMatrizT(a, b, c, d, verticesRotation, matrizT);

			float vix = vertices.getXVertex(a);
			float viy = vertices.getYVertex(a);
			float vjx = vertices.getXVertex(b);
			float vjy = vertices.getYVertex(b);

			float ex = vjx - vix;
			float ey = vjy - viy;

			double tex = matrizT.get(0, 0) * ex + matrizT.get(0, 1) * ey;
			double tey = matrizT.get(1, 0) * ex + matrizT.get(1, 1) * ey;

			m.set(j, 0, tex);
			n.set(j, 0, tey);

			j = j + 1;
		}

		for (short k = 0; k < numHandles; k++)
		{
			int pos = numEdges + k;
			
			float x = handles.getXCoordHandle(k);
			float y = handles.getYCoordHandle(k);

			m.set(pos, 0, w * x);
			n.set(pos, 0, w * y);
		}
	}

	/* Cálculo de Vecinos */

	private void buildEdges(TriangleArray triangles)
	{
		for (short i = 0; i < triangles.getNumTriangles(); i++)
		{
			short a = triangles.getAVertex(i);
			short b = triangles.getBVertex(i);
			short c = triangles.getCVertex(i);
			
			mEdges.addEdge(a, b, c, mVertices);
			mEdges.addEdge(b, c, a, mVertices);
			mEdges.addEdge(c, a, b, mVertices);		
		}
	}
}
