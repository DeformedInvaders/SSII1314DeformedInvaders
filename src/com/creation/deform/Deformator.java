package com.creation.deform;

import android.util.Log;

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

	private Matrix matrixG, matrixGt, matrixGtG;

	private Matrix matrixE, matrixEGtGinv;

	private Matrix matrixh1, matrixh2;
	private Matrix matrixH;

	private Matrix matrixA1, matrixA1t, matrixA1tA1;
	private Matrix matrixB1, matrixA1tB1;

	// Cálculo de Matrices A2 y B2

	private Matrix matrixGtGinvGt, matrixV;
	private Matrix matrixTk, matrixT;

	private Matrix matrixA2, matrixA2t, matrixA2tA2;
	private Matrix matrixB2x, matrixB2y, matrixA2tB2x, matrixA2tB2y;

	/* Constructora */

	public Deformator(VertexArray mesh, TriangleArray triangles, HandleArray handles)
	{
		mVertices = mesh.clone();
		mTriangles = triangles;
		mEdges = new EdgeArray();
		
		buildEdgeMesh(mTriangles);

		numVertices = mVertices.getNumVertices();
		numEdges = mEdges.getNumEdges();

		// Calcular Matriz G
		matrixG = new Matrix(8, 2);
		matrixGt = new Matrix(2, 8);
		matrixGtG = new Matrix(2, 2);

		// Calcular Matriz H
		matrixH = new Matrix(2, 8);

		matrixh1 = new Matrix(2, 8);
		matrixh1.set(0, 0, -1);
		matrixh1.set(0, 2, 1);
		matrixh1.set(1, 1, -1);
		matrixh1.set(1, 3, 1);

		matrixh2 = new Matrix(2, 8);

		matrixE = new Matrix(2, 2);
		matrixEGtGinv = new Matrix(2, 2);

		// Calcular Matriz T
		matrixV = new Matrix(8, 1);
		matrixGtGinvGt = new Matrix(2, 8);
		matrixTk = new Matrix(2, 1);
		matrixT = new Matrix(2, 2);

		// Calcular MatrizA1
		matrixA1tA1 = new Matrix(2 * numVertices, 2 * numVertices);

		// Calcular MatrizA2
		matrixA2tA2 = new Matrix(numVertices, numVertices);

		// Calcular MatrizB1
		matrixA1tB1 = new Matrix(2 * numVertices, 1);

		// Calcular Matriz B2
		matrixA2tB2x = new Matrix(numVertices, 1);
		matrixA2tB2y = new Matrix(numVertices, 1);

		addHandles(handles);
	}

	// Añadir nuevos Handles
	public void addHandles(HandleArray handles)
	{
		numHandles = handles.getNumHandles();

		// Calcular MatrizA1
		matrixA1 = new Matrix(2 * numEdges + 2 * numHandles, 2 * numVertices);
		buildMatrixA1(mVertices, handles, matrixA1);

		matrixA1t = new Matrix(2 * numVertices, 2 * numEdges + 2 * numHandles);
		matrixA1.transpose(matrixA1t);

		matrixA1t.times(matrixA1, matrixA1tA1);

		// Calcular MatrizB1
		matrixB1 = new Matrix(2 * numEdges + 2 * numHandles, 1);
		buildMatrixB1(handles, matrixB1);

		// Calcular MatrizA2
		matrixA2 = new Matrix(numEdges + numHandles, numVertices);
		buildMatrixA2(handles, matrixA2);

		matrixA2t = new Matrix(numVertices, numEdges + numHandles);
		matrixA2.transpose(matrixA2t);

		matrixA2t.times(matrixA2, matrixA2tA2);

		// Calcular Matriz B2
		matrixB2x = new Matrix(numEdges + numHandles, 1);
		matrixB2y = new Matrix(numEdges + numHandles, 1);
	}

	// Borrar Handles
	public void deleteHandles(HandleArray handles)
	{
		addHandles(handles);
	}

	// Modificación de Posición de Handles
	public void moveHandles(HandleArray handles, VertexArray verticesModificados)
	{
		try
		{		
			// Actualizar MatrizB
			buildMatrixB1(handles, matrixB1);
	
			// Cálculo de Ajuste de Traslación y Rotación
			//
			// A1t * A1 * X = A1t * B1
	
			matrixA1t.times(matrixB1, matrixA1tB1);
			
			Matrix m1 = matrixA1tA1.solve(matrixA1tB1);
			
			// Actualizar Valores de los Vertices después del Ajuste de Traslación y Rotación
			VertexArray verticesTrasRot = mVertices.clone();
			for (short i = 0; i < numVertices; i++)
			{
				verticesTrasRot.setVertex(i, (float) m1.get(2 * i, 0), (float) m1.get(2 * i + 1, 0));
			}
	
			// Cálculo de Ajuste de Escalación
			//
			// A2t * A2 * X = A2t * B2
	
			buildMatrixB2(mVertices, verticesTrasRot, handles, matrixB2x, matrixB2y);
	
			matrixA2t.times(matrixB2x, matrixA2tB2x);
			matrixA2t.times(matrixB2y, matrixA2tB2y);
	
			Matrix m2x = matrixA2tA2.solve(matrixA2tB2x);
			Matrix m2y = matrixA2tA2.solve(matrixA2tB2y);
	
			// Actualizar Valores de los Vertices después del Ajuste de Escala
			for (short i = 0; i < numVertices; i++)
			{
				verticesModificados.setVertex(i, (float) m2x.get(i, 0), (float) m2y.get(i, 0));
			}
		}
		catch (RuntimeException e)
		{
			Log.d("TEST", "RuntimeException "+e.getMessage());
		}
	}

	/* Ajuste de Traslación y Rotación */

	// Cálculo Matriz G
	private void buildMatrixG(short a, short b, short c, short d, VertexArray vertices, Matrix m)
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
	private void buildMatrixE(short a, short b, VertexArray vertices, Matrix m)
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
	private void buildMatrixH(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		buildMatrixG(a, b, c, d, vertices, matrixG);

		matrixG.transpose(matrixGt);
		matrixGtG = matrixGt.times(matrixG);
		Matrix gtginv = matrixGtG.inverse();

		buildMatrixE(a, b, vertices, matrixE);

		matrixE.times(gtginv, matrixEGtGinv);
		matrixEGtGinv.times(matrixGt, matrixh2);

		// Matriz H
		matrixh1.minus(matrixh2, m);
	}

	// Cálculo Matriz A1
	private void buildMatrixA1(VertexArray vertices, HandleArray handles, Matrix m)
	{
		for (short i = 0; i < numEdges; i++)
		{
			short a = mEdges.getAVertex(i);
			short b = mEdges.getBVertex(i);
			short c = mEdges.getLVertex(i);
			short d = mEdges.getRVertex(i);

			buildMatrixH(a, b, c, d, vertices, matrixH);

			m.set(2 * i, 2 * a, matrixH.get(0, 0));
			m.set(2 * i, 2 * a + 1, matrixH.get(0, 1));
			m.set(2 * i + 1, 2 * a, matrixH.get(1, 0));
			m.set(2 * i + 1, 2 * a + 1, matrixH.get(1, 1));

			m.set(2 * i, 2 * b, matrixH.get(0, 2));
			m.set(2 * i, 2 * b + 1, matrixH.get(0, 3));
			m.set(2 * i + 1, 2 * b, matrixH.get(1, 2));
			m.set(2 * i + 1, 2 * b + 1, matrixH.get(1, 3));

			if (c != -1)
			{
				m.set(2 * i, 2 * c, matrixH.get(0, 4));
				m.set(2 * i, 2 * c + 1, matrixH.get(0, 5));
				m.set(2 * i + 1, 2 * c, matrixH.get(1, 4));
				m.set(2 * i + 1, 2 * c + 1, matrixH.get(1, 5));
			}

			if (d != -1)
			{
				m.set(2 * i, 2 * d, matrixH.get(0, 6));
				m.set(2 * i, 2 * d + 1, matrixH.get(0, 7));
				m.set(2 * i + 1, 2 * d, matrixH.get(1, 6));
				m.set(2 * i + 1, 2 * d + 1, matrixH.get(1, 7));
			}
		}

		for (short k = 0; k < numHandles; k++)
		{
			int pos = 2 * numEdges + 2 * k;
			
			short triangulo = handles.getIndexHandle(k);
			float alfa = handles.getAlfaHandle(k);
			float beta = handles.getBetaHandle(k);
			float gamma = handles.getGammaHandle(k);
			
			short a = mTriangles.getAVertex(triangulo);
			short b = mTriangles.getBVertex(triangulo);
			short c = mTriangles.getCVertex(triangulo);

			m.set(pos, 2 * a, w * alfa);
			m.set(pos + 1, 2 * a + 1, w * alfa);
			
			m.set(pos, 2 * b, w * beta);
			m.set(pos + 1, 2 * b + 1, w * beta);
			
			m.set(pos, 2 * c, w * gamma);
			m.set(pos + 1, 2 * c + 1, w * gamma);
		}
	}

	// Cálculo Matriz B1
	private void buildMatrixB1(HandleArray handles, Matrix m)
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
	private void buildMatrixA2(HandleArray handles, Matrix m)
	{
		for (short i = 0; i < numEdges; i++)
		{
			int a = mEdges.getAVertex(i);
			int b = mEdges.getBVertex(i);

			m.set(i, a, 1);
			m.set(i, b, -1);
		}

		for (short k = 0; k < numHandles; k++)
		{
			int pos = numEdges + k;
			
			short triangulo = handles.getIndexHandle(k);
			float alfa = handles.getAlfaHandle(k);
			float beta = handles.getBetaHandle(k);
			float gamma = handles.getGammaHandle(k);
			
			short a = mTriangles.getAVertex(triangulo);
			short b = mTriangles.getBVertex(triangulo);
			short c = mTriangles.getCVertex(triangulo);

			m.set(pos, a, w * alfa);
			m.set(pos, b, w * beta);
			m.set(pos, c, w * gamma);
		}
	}

	// Cálculo Matriz V
	private void buildMatrixV(short a, short b, short c, short d, VertexArray vertices, Matrix m)
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
	private void buildMatrixT(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		buildMatrixG(a, b, c, d, vertices, matrixG);

		matrixG.transpose(matrixGt);
		matrixGtG = matrixGt.times(matrixG);
		Matrix gtginv = matrixGtG.inverse();

		gtginv.times(matrixGt, matrixGtGinvGt);

		buildMatrixV(a, b, c, d, vertices, matrixV);

		matrixGtGinvGt.times(matrixV, matrixTk);
		
		// double ck = matrizTk.get(0, 0);
		double ck = -matrixTk.get(0, 0);
		double sk = matrixTk.get(1, 0);

		m.set(0, 0, ck);
		m.set(0, 1, sk);
		m.set(1, 0, -sk);
		m.set(1, 1, ck);

		double k = 1.0 / (Math.pow(ck, 2) + Math.pow(sk, 2));

		m.times(k);
	}

	// Cálculo Matriz B2
	private void buildMatrixB2(VertexArray vertices, VertexArray verticesTrasRot, HandleArray handles, Matrix m, Matrix n)
	{
		for (short i = 0; i < numEdges; i++)
		{
			short a = mEdges.getAVertex(i);
			short b = mEdges.getBVertex(i);
			short c = mEdges.getLVertex(i);
			short d = mEdges.getRVertex(i);

			buildMatrixT(a, b, c, d, verticesTrasRot, matrixT);

			float vix = vertices.getXVertex(a);
			float viy = vertices.getYVertex(a);
			float vjx = vertices.getXVertex(b);
			float vjy = vertices.getYVertex(b);

			float ex = vjx - vix;
			float ey = vjy - viy;

			double tex = matrixT.get(0, 0) * ex + matrixT.get(0, 1) * ey;
			double tey = matrixT.get(1, 0) * ex + matrixT.get(1, 1) * ey;

			m.set(i, 0, tex);
			n.set(i, 0, tey);
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

	private void buildEdgeMesh(TriangleArray triangulos)
	{
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);
			
			mEdges.addEdge(a, b, c, mVertices);
			mEdges.addEdge(b, c, a, mVertices);
			mEdges.addEdge(c, a, b, mVertices);		
		}
	}
}
