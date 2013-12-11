package com.example.design;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.example.main.Esqueleto;
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
	private FloatBuffer bufferPuntos;
	
	private FloatArray puntosDelaunay;
	
	private FloatArray puntosMesh;
	
	private FloatArray lineasBSpline;
	private FloatBuffer bufferBSpline;
	
	private FloatArray lineasConvexHull;
	private FloatBuffer bufferConvexHull;
	
	private ShortArray triangulosDelaunay;
	private ArrayList<FloatBuffer> bufferDelaunay;
	
	private ShortArray triangulosEarClipping;
	private ArrayList<FloatBuffer> bufferEarClipping;
	
	private ShortArray triangulosMesh;
	private ArrayList<FloatBuffer> bufferMeshTriangles;
	
	private ShortArray lineasSimple;
	private ArrayList<FloatBuffer> bufferSimple;	
	
	public DesignOpenGLRenderer(Context context)
	{        
		super(context);
		
        this.estado = TDesignEstado.Dibujar;

        this.puntos = new FloatArray();
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		switch(estado)
		{
			case Dibujar:
				if(puntos.size > 2)
				{
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPuntos);
					dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPuntos);
				}
			break;
			case BSpline:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.YELLOW, bufferBSpline);
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferBSpline);
			break;
			case ConvexHull:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.GREEN, bufferConvexHull);
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferConvexHull);
			break;
			case Delaunay:
				dibujarListaBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLUE, bufferDelaunay);
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPuntos);
			break;
			case EarClipping:
				dibujarListaBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.GRAY, bufferEarClipping);
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPuntos);
			break;
			case MeshGenerator:
				dibujarListaBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.MAGENTA, bufferMeshTriangles);
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPuntos);
			break;
			case Simple:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.LTGRAY, bufferPuntos);
				dibujarListaBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.RED, bufferSimple);
			break;
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		estado = TDesignEstado.Dibujar;
		
		puntos.clear();
		puntosMesh = null;
		puntosDelaunay = null;
		
		lineasBSpline = null;
		lineasConvexHull = null;
		triangulosDelaunay = null;
		triangulosEarClipping = null;
		triangulosMesh = null;
		lineasSimple = null;
	}
	
	public void onTouchDown(float x, float y, float width, float height)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			// Conversión Pixel - Punto	
			float nx = xLeft + (xRight-xLeft)*x/width;
			float ny = yBot + (yTop-yBot)*(height-y)/height;
			
			puntos.add(nx);
			puntos.add(ny);
			
			bufferPuntos = construirBufferListaPuntos(puntos);
		}
	}
	
	public void onTouchMove(float x, float y, float width, float height)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			onTouchDown(x, y, width, height);
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			onTouchDown(x, y, width, height);
		}
	}
	
	/* Selección de Estado */
	
	public void bSpline()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.BSpline;
			
			if(lineasBSpline == null)
			{
				lineasBSpline = calcularBSpline(puntos, 3, 100);
				bufferBSpline = construirBufferListaPuntos(lineasBSpline);
			}
		}
	}
	
	private FloatArray calcularBSpline(FloatArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	public void convexHull()
	{		
		if(puntos.size > 4)
		{
			estado = TDesignEstado.ConvexHull;
			
			if(lineasConvexHull == null)
			{
				lineasConvexHull = calcularConvexHull(puntos, false);
				bufferConvexHull = construirBufferListaPuntos(lineasConvexHull);
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
			
			if(triangulosDelaunay == null)
			{
				puntosDelaunay = new FloatArray(puntos);
				triangulosDelaunay = calcularDelaunay(puntosDelaunay, false);
				bufferDelaunay = construirBufferListaTriangulos(triangulosDelaunay, puntosDelaunay);
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
			
			if(triangulosEarClipping == null)
			{
				triangulosEarClipping = calcularEarClipping(puntos);
				bufferEarClipping = construirBufferListaTriangulos(triangulosEarClipping, puntos);
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
			
			if(triangulosMesh == null)
			{
				Mesh m = calcularMeshGenerator(puntos, 3, 10.0f);
				puntosMesh = m.getVertices();
				triangulosMesh = m.getTriangulos();
				bufferMeshTriangles = construirBufferListaTriangulos(triangulosMesh, puntosMesh);
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
			
			if(lineasSimple == null)
			{
				lineasSimple = calcularPoligonoSimple(puntos, false);
				bufferSimple = construirBufferListaLineas(lineasSimple, puntos);
			}
			
			return lineasSimple.size == 0;
		}
		
		return false;
	}
	
	private ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
	
	/* Captura de Esqueleto */
	
	public Esqueleto getEsqueleto()
	{
		if(puntos.size > 4)
		{
			int numBSplineVertices = 75;
			// TODO Calcular Iteraciones en función del Area del Poligono
			FloatArray bsplineVertices = calcularBSpline(puntos, 3, numBSplineVertices);
			
			ShortArray testSimple = calcularPoligonoSimple(bsplineVertices, false);
			if(testSimple.size == 0)
			{
				Mesh m = calcularMeshGenerator(bsplineVertices, 3, 75.0f);
				FloatArray puntosTest = m.getVertices();
				ShortArray triangulosTest = m.getTriangulos();
				ShortArray contornoTest = new ShortArray(numBSplineVertices);
				for(int i = 0; i < numBSplineVertices; i++) contornoTest.add(i);
				
				return new Esqueleto(contornoTest, puntosTest, triangulosTest);
			}
		}
		
		return null;
	}
}
