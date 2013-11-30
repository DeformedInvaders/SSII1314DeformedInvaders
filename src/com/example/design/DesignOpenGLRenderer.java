package com.example.design;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.example.main.GLESUtils;
import com.example.main.OpenGLRenderer;
import com.example.math.BSpline;
import com.example.math.ConvexHull;
import com.example.math.DelaunayMeshGenerator;
import com.example.math.DelaunayTriangulator;
import com.example.math.EarClippingTriangulator;
import com.example.math.GeometryUtils;
import com.example.math.Vector2;
import com.example.utils.FloatArray;
import com.example.utils.Mesh;
import com.example.utils.ShortArray;

public class DesignOpenGLRenderer extends OpenGLRenderer
{	
	//Estructura de Datos de la Escena
	private TDesignEstado estado;
	
	private FloatArray puntos;
	private FloatArray puntosDelaunay;
	private FloatArray puntosMesh;
	private FloatArray handles;
	
	private FloatArray lineasBSpline;
	private FloatArray lineasConvexHull;
	private ShortArray triangulosDelaunay;
	private ShortArray triangulosEarClipping;
	private ShortArray triangulosMesh;
	private ShortArray lineasSimple;
	
	/* TEST */
	private FloatArray puntosTest;
	private ShortArray triangulosTest;
	/* TEST */
	
	//Estructura de Datos para OpenGL ES
	private FloatBuffer bufferPuntos;
	private FloatBuffer bufferHandles;
	
	private FloatBuffer bufferBSpline;
	private FloatBuffer bufferConvexHull;
	private ArrayList<FloatBuffer> bufferDelaunay;
	private ArrayList<FloatBuffer> bufferEarClipping;
	private ArrayList<FloatBuffer> bufferMeshTriangles;
	private ArrayList<FloatBuffer> bufferSimple;
	
	/* TEST */
	private ArrayList<FloatBuffer> bufferTest;
	/* TEST*/
	
	public DesignOpenGLRenderer()
	{        
        estado = TDesignEstado.Dibujar;

        puntos = new FloatArray();
        handles = new FloatArray();
	}
	
	public TDesignEstado getEstado()
	{
		return estado;
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		gl.glPointSize(10.0f);
				
		// Dibujar Segmentos
		switch(estado)
		{
			case Dibujar:
				if(puntos.size > 2)
				{
					GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferPuntos);
				}
			break;
			case BSpline:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.YELLOW, bufferBSpline);
			break;
			case ConvexHull:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.GREEN, bufferConvexHull);
			break;
			case Delaunay:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLUE, bufferDelaunay);
			break;
			case EarClipping:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.GRAY, bufferEarClipping);
			break;
			case MeshGenerator:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.MAGENTA, bufferMeshTriangles);
			break;
			case Simple:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.LTGRAY, bufferPuntos);
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.RED, bufferSimple);
			break;
			case Test:
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferTest);
			break;
		}
		
		// Dibujar Puntos
		if(puntos.size > 0)
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_POINTS, 3.0f, Color.RED, bufferPuntos);
		}
		
		// Dibujar Handles
		if(estado != TDesignEstado.Dibujar)
		{
			if(handles.size > 0)
			{
				GLESUtils.dibujarBuffer(gl, GL10.GL_POINTS, 3.0f, Color.YELLOW, bufferHandles);
			}
		}
	}
	
	public void anyadirPunto(float x, float y, float width, float height)
	{
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		puntos.add(nx);
		puntos.add(ny);
		
		bufferPuntos = GLESUtils.construirBuffer(puntos);
	}
	
	public void bSpline()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.BSpline;
			handles.clear();
			
			if(lineasBSpline == null)
			{
				lineasBSpline = calcularBSpline(puntos, 3, 100f);
				bufferBSpline = GLESUtils.construirBuffer(lineasBSpline);
			}
		}
	}
	
	private FloatArray calcularBSpline(FloatArray vertices, int grado, float iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	public void convexHull()
	{		
		if(puntos.size > 4)
		{
			estado = TDesignEstado.ConvexHull;
			handles.clear();
			
			if(lineasConvexHull == null)
			{
				lineasConvexHull = calcularConvexHull(puntos, false);
				bufferConvexHull = GLESUtils.construirBuffer(lineasConvexHull);
			}
		}
	}
	
	private FloatArray calcularConvexHull(FloatArray vertices, boolean ordenados)
	{
		ConvexHull convexHullCalculator = new ConvexHull();
		return convexHullCalculator.computePolygon(vertices, ordenados);
	}
	
	public void delaunay()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.Delaunay;
			handles.clear();
			
			if(triangulosDelaunay == null)
			{
				puntosDelaunay = new FloatArray(puntos);
				triangulosDelaunay = calcularDelaunay(puntosDelaunay, false);
				bufferDelaunay = GLESUtils.construirTriangulosBuffer(triangulosDelaunay, puntosDelaunay);
			}
		}
	}
	
	private ShortArray calcularDelaunay(FloatArray vertices, boolean ordenados)
	{
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		return delaunayCalculator.computeTriangles(vertices, ordenados);
	}

	public void earClipping()
	{
		if(puntos.size > 4) 
		{
			estado = TDesignEstado.EarClipping;
			handles.clear();
			
			if(triangulosEarClipping == null)
			{
				triangulosEarClipping = calcularEarClipping(puntos);
				bufferEarClipping = GLESUtils.construirTriangulosBuffer(triangulosEarClipping, puntos);
			}
		}
	}
	
	private ShortArray calcularEarClipping(FloatArray vertices)
	{
		EarClippingTriangulator earClippingCalculator = new EarClippingTriangulator();
		return earClippingCalculator.computeTriangles(vertices);
	}
	
	public void meshGenerator()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.MeshGenerator;
			handles.clear();
			
			if(triangulosMesh == null)
			{
				Mesh m = calcularMeshGenerator(puntos, 3, 10.0f);
				puntosMesh = m.getVertices();
				triangulosMesh = m.getTriangulos();
				bufferMeshTriangles = GLESUtils.construirTriangulosBuffer(triangulosMesh, puntosMesh);
			}
		}
	}
	
	private Mesh calcularMeshGenerator(FloatArray vertices, int profundidad, float longitud)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices, profundidad, longitud);
	}
	
	public boolean testSimple()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.Simple;
			handles.clear();
			
			if(lineasSimple == null)
			{
				lineasSimple = calcularPoligonoSimple(puntos, false);
				bufferSimple = GLESUtils.construirLineasBuffer(lineasSimple, puntos);
			}
			
			return lineasSimple.size == 0;
		}
		
		return false;
	}
	
	private ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
	
	/* TEST */
	public Mesh test()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.Test;
			
			//if(testSimple()) {
				// TODO Calcular Iteraciones en función del Area del Poligono
				FloatArray bsplineVertices = calcularBSpline(puntos, 3, 100.0f);
				Mesh m = calcularMeshGenerator(bsplineVertices, 3, 10.0f);
				puntosTest = m.getVertices();
				triangulosTest = m.getTriangulos();
				
				bufferTest = GLESUtils.construirTriangulosBuffer(triangulosTest, puntosTest);
				
				return m;
			//}
		}
		
		return null;
	}
	
	/* TEST */
	
	public void reiniciarPuntos()
	{
		estado = TDesignEstado.Dibujar;
		
		puntos.clear();
		puntosMesh = null;
		puntosDelaunay = null;
		handles.clear();
		
		lineasBSpline = null;
		lineasConvexHull = null;
		triangulosDelaunay = null;
		triangulosEarClipping = null;
		triangulosMesh = null;
		lineasSimple = null;
		
		/* TEST */
		puntosTest = null;
		triangulosTest = null;
		/* TEST */ 
	}
}
