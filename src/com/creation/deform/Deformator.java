package com.creation.deform;

import com.lib.buffer.EdgeArray;
import com.lib.buffer.HandleArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.matrix.LUDecomposition;
import com.lib.matrix.Matrix;

public class Deformator
{
	private static final float w = 1000;
	
	private VertexArray mVertices;
	private TriangleArray mTriangles;
	private EdgeArray mEdges;

	private int numVertices, numEdges, numHandles;
	
	// Cálculo de Matrices A1 y B1

	private Matrix matrixG3, matrixG3t, matrixGtG;
	private Matrix matrixG4, matrixG4t;

	private Matrix matrixE, matrixEGtGinv;

	private Matrix matrixh13, matrixh14, matrixh23, matrixh24;
	private Matrix matrixH3, matrixH4;

	private Matrix matrixA1, matrixA1t, matrixA1tA1;
	private Matrix matrixB1, matrixA1tB1;

	// Cálculo de Matrices A2 y B2

	private Matrix matrixGtGinvGt3, matrixGtGinvGt4, matrixV3, matrixV4;
	private Matrix matrixTk, matrixT;

	private Matrix matrixA2, matrixA2t, matrixA2tA2;
	private Matrix matrixB2x, matrixB2y, matrixA2tB2x, matrixA2tB2y;

	/* Constructora */
	
	public Deformator(VertexArray vertices, TriangleArray triangles)
	{
		this(vertices, triangles, new HandleArray());
	}

	public Deformator(VertexArray vertices, TriangleArray triangles, HandleArray handles)
	{
		mVertices = vertices.clone();
		mTriangles = triangles.clone();
		mEdges = new EdgeArray();
		
		buildEdgeMesh(mTriangles);

		numVertices = mVertices.getNumVertices();
		numEdges = mEdges.getNumEdges();

		// Calcular Matriz G
		matrixG4 = new Matrix(8, 2);
		matrixG4t = new Matrix(2, 8);
		matrixG3 = new Matrix(6, 2);
		matrixG3t = new Matrix(2, 6);
		matrixGtG = new Matrix(2, 2);

		// Calcular Matriz H
		matrixH3 = new Matrix(2, 6);

		matrixh13 = new Matrix(2, 6);
		matrixh13.set(0, 0, -1);
		matrixh13.set(0, 2, 1);
		matrixh13.set(1, 1, -1);
		matrixh13.set(1, 3, 1);
		
		matrixh23 = new Matrix(2, 6);
		
		matrixH4 = new Matrix(2, 8);
		
		matrixh14 = new Matrix(2, 8);
		matrixh14.set(0, 0, -1);
		matrixh14.set(0, 2, 1);
		matrixh14.set(1, 1, -1);
		matrixh14.set(1, 3, 1);

		matrixh24 = new Matrix(2, 8);

		matrixE = new Matrix(2, 2);
		matrixEGtGinv = new Matrix(2, 2);

		// Calcular Matriz T
		matrixV3 = new Matrix(6, 1);
		matrixV4 = new Matrix(8, 1);
		matrixGtGinvGt3 = new Matrix(2, 6);
		matrixGtGinvGt4 = new Matrix(2, 8);
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
			android.util.Log.d("TEST", "RuntimeException "+e.getMessage());
		}
	}

	/* Ajuste de Traslación y Rotación */

	// Cálculo Matriz G
	private void buildMatrixG4(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = vertices.getXVertex(c);
		float vly = vertices.getYVertex(c);

		float vrx = vertices.getXVertex(d);
		float vry = vertices.getYVertex(d);

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
	
	private void buildMatrixG3(short a, short b, short c, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = vertices.getXVertex(c);
		float vly = vertices.getYVertex(c);

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
	private void buildMatrixH3(short a, short b, short c, VertexArray vertices, Matrix m)
	{
		buildMatrixG3(a, b, c, vertices, matrixG3);

		matrixG3.transpose(matrixG3t);
		matrixGtG = matrixG3t.times(matrixG3);
		Matrix gtginv = matrixGtG.inverse();

		buildMatrixE(a, b, vertices, matrixE);

		matrixE.times(gtginv, matrixEGtGinv);
		matrixEGtGinv.times(matrixG3t, matrixh23);

		// Matriz H
		matrixh13.minus(matrixh23, m);
	}	
	
	private void buildMatrixH4(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		buildMatrixG4(a, b, c, d, vertices, matrixG4);

		matrixG4.transpose(matrixG4t);
		matrixGtG = matrixG4t.times(matrixG4);
		Matrix gtginv = matrixGtG.inverse();

		buildMatrixE(a, b, vertices, matrixE);

		matrixE.times(gtginv, matrixEGtGinv);
		matrixEGtGinv.times(matrixG4t, matrixh24);

		// Matriz H
		matrixh14.minus(matrixh24, m);
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
			
			if (c == -1 || d == -1)
			{
				short cd = c != -1 ? c : d;
				buildMatrixH3(a, b, cd, vertices, matrixH3);

				m.set(2 * i, 2 * a, matrixH3.get(0, 0));
				m.set(2 * i, 2 * a + 1, matrixH3.get(0, 1));
				m.set(2 * i + 1, 2 * a, matrixH3.get(1, 0));
				m.set(2 * i + 1, 2 * a + 1, matrixH3.get(1, 1));

				m.set(2 * i, 2 * b, matrixH3.get(0, 2));
				m.set(2 * i, 2 * b + 1, matrixH3.get(0, 3));
				m.set(2 * i + 1, 2 * b, matrixH3.get(1, 2));
				m.set(2 * i + 1, 2 * b + 1, matrixH3.get(1, 3));

				m.set(2 * i, 2 * cd, matrixH3.get(0, 4));
				m.set(2 * i, 2 * cd + 1, matrixH3.get(0, 5));
				m.set(2 * i + 1, 2 * cd, matrixH3.get(1, 4));
				m.set(2 * i + 1, 2 * cd + 1, matrixH3.get(1, 5));
			}
			else
			{
				buildMatrixH4(a, b, c, d, vertices, matrixH4);

				m.set(2 * i, 2 * a, matrixH4.get(0, 0));
				m.set(2 * i, 2 * a + 1, matrixH4.get(0, 1));
				m.set(2 * i + 1, 2 * a, matrixH4.get(1, 0));
				m.set(2 * i + 1, 2 * a + 1, matrixH4.get(1, 1));

				m.set(2 * i, 2 * b, matrixH4.get(0, 2));
				m.set(2 * i, 2 * b + 1, matrixH4.get(0, 3));
				m.set(2 * i + 1, 2 * b, matrixH4.get(1, 2));
				m.set(2 * i + 1, 2 * b + 1, matrixH4.get(1, 3));

				m.set(2 * i, 2 * c, matrixH4.get(0, 4));
				m.set(2 * i, 2 * c + 1, matrixH4.get(0, 5));
				m.set(2 * i + 1, 2 * c, matrixH4.get(1, 4));
				m.set(2 * i + 1, 2 * c + 1, matrixH4.get(1, 5));

				m.set(2 * i, 2 * d, matrixH4.get(0, 6));
				m.set(2 * i, 2 * d + 1, matrixH4.get(0, 7));
				m.set(2 * i + 1, 2 * d, matrixH4.get(1, 6));
				m.set(2 * i + 1, 2 * d + 1, matrixH4.get(1, 7));	
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
	private void buildMatrixV3(short a, short b, short c, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = vertices.getXVertex(c);
		float vly = vertices.getYVertex(c);

		m.set(0, 0, vix);
		m.set(1, 0, viy);

		m.set(2, 0, vjx);
		m.set(3, 0, vjy);

		m.set(4, 0, vlx);
		m.set(5, 0, vly);
	}
	
	private void buildMatrixV4(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		float vix = vertices.getXVertex(a);
		float viy = vertices.getYVertex(a);

		float vjx = vertices.getXVertex(b);
		float vjy = vertices.getYVertex(b);

		float vlx = vertices.getXVertex(c);
		float vly = vertices.getYVertex(c);

		float vrx = vertices.getXVertex(d);
		float vry = vertices.getYVertex(d);

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
	private void buildMatrixT3(short a, short b, short c, VertexArray vertices, Matrix m)
	{
		buildMatrixG3(a, b, c, vertices, matrixG3);

		matrixG3.transpose(matrixG3t);
		matrixGtG = matrixG3t.times(matrixG3);
		Matrix gtginv = matrixGtG.inverse();

		gtginv.times(matrixG3t, matrixGtGinvGt3);

		buildMatrixV3(a, b, c, vertices, matrixV3);

		matrixGtGinvGt3.times(matrixV3, matrixTk);
		
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
	
	private void buildMatrixT4(short a, short b, short c, short d, VertexArray vertices, Matrix m)
	{
		buildMatrixG4(a, b, c, d, vertices, matrixG4);

		matrixG4.transpose(matrixG4t);
		matrixGtG = matrixG4t.times(matrixG4);
		Matrix gtginv = matrixGtG.inverse();

		gtginv.times(matrixG4t, matrixGtGinvGt4);

		buildMatrixV4(a, b, c, d, vertices, matrixV4);

		matrixGtGinvGt4.times(matrixV4, matrixTk);
		
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
			
			float vix = vertices.getXVertex(a);
			float viy = vertices.getYVertex(a);
			float vjx = vertices.getXVertex(b);
			float vjy = vertices.getYVertex(b);

			float ex = vjx - vix;
			float ey = vjy - viy;
			
			if (c == -1 || d == -1)
			{				
				buildMatrixT3(a, b, c != -1 ? c : d, verticesTrasRot, matrixT);
	
				double tex = matrixT.get(0, 0) * ex + matrixT.get(0, 1) * ey;
				double tey = matrixT.get(1, 0) * ex + matrixT.get(1, 1) * ey;
	
				m.set(i, 0, tex);
				n.set(i, 0, tey);
			}
			else
			{
				buildMatrixT4(a, b, c, d, verticesTrasRot, matrixT);
				
				double tex = matrixT.get(0, 0) * ex + matrixT.get(0, 1) * ey;
				double tey = matrixT.get(1, 0) * ex + matrixT.get(1, 1) * ey;
	
				m.set(i, 0, tex);
				n.set(i, 0, tey);
			}
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
	
	public boolean isSingular()
	{
		LUDecomposition lu = new LUDecomposition(matrixA1tA1);
		return !lu.isNonsingular();
	}
}
