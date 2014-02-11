package com.create.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.lib.math.BSpline;
import com.lib.math.DelaunayMeshGenerator;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.math.Vector2;
import com.lib.utils.FloatArray;
import com.lib.utils.Mesh;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.main.OpenGLRenderer;

public class DesignOpenGLRenderer extends OpenGLRenderer
{		
	//Estructura de Datos de la Escena
	private TDesignEstado estado;
	
	private FloatArray puntos;
	private FloatBuffer bufferPuntos;	
	
	private FloatArray puntosTest;
	private ShortArray triangulosTest;
	private FloatBuffer bufferTest;
	
	private final static int NUM_BSPLINE_VERTICES = 60;
	private final static int DEEP_TRIANGULATOR = 3;
	private final static float MAX_LONG_EDGE_TRIANGULATOR = 100.0f;
	
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
				if(puntos.size > 0)
				{
					dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPuntos);
					
					if(puntos.size > 2)
					{
						dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPuntos);
					}
				}
			break;
			case Full:
				dibujarBuffer(gl, GL10.GL_LINES, SIZELINE, Color.BLACK, bufferTest);
			break;
			default:
			break;
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		estado = TDesignEstado.Dibujar;
		
		puntos.clear();
		
		puntosTest = null;
		triangulosTest = null;
	}
	
	@Override
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
	}
	
	
	private synchronized void anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		boolean anyadir = true;
		
		if(puntos.size > 0)
		{
			float lastWorldX = puntos.get(puntos.size-2);
			float lastWorldY = puntos.get(puntos.size-1);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);
			
			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > EPSILON;
		}
		
		if(anyadir)
		{
			puntos.add(worldX);
			puntos.add(worldY);
			
			bufferPuntos = construirBufferListaPuntos(puntos);
		}
	}
	
	@Override
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	@Override
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujar)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	@Override
	public void onMultiTouchEvent() { }
	
	/* Selección de Estado */
	
	private FloatArray calcularBSpline(FloatArray vertices, int grado, int iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	private Mesh calcularMeshGenerator(FloatArray vertices, int profundidad, float longitud)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices, profundidad, longitud);
	}
	
	private ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
	
	/* Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		if(puntos.size > 4)
		{
			estado = TDesignEstado.Full;
			
			// TODO Calcular Iteraciones en función del Area del Poligono
			FloatArray bsplineVertices = calcularBSpline(puntos, 3, NUM_BSPLINE_VERTICES);
			
			ShortArray testSimple = calcularPoligonoSimple(bsplineVertices, false);
			if(testSimple.size == 0)
			{
				Mesh m = calcularMeshGenerator(bsplineVertices, DEEP_TRIANGULATOR, MAX_LONG_EDGE_TRIANGULATOR);
				puntosTest = m.getVertices();
				triangulosTest = m.getTriangulos();
				bufferTest = construirBufferListaTriangulos(triangulosTest, puntosTest);
				
				// Contorno
				ShortArray contornoTest = new ShortArray(NUM_BSPLINE_VERTICES);
				for(int i = 0; i < NUM_BSPLINE_VERTICES; i++) contornoTest.add(i);
				
				return new Esqueleto(contornoTest, puntosTest, triangulosTest);
			}
		}
		
		return null;
	}

	public boolean poligonoCompleto()
	{
		return puntos.size >= 6;
	}
}
