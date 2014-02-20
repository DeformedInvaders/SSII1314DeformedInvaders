package com.create.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.main.OpenGLRenderer;

public class DesignOpenGLRenderer extends OpenGLRenderer
{		
	//Estructura de Datos de la Escena
	private TDesignEstado estado;
	private Triangulator triangulator;
	
	private FloatArray puntos;	
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;
	
	private FloatBuffer bufferPoligono;	
	private FloatBuffer bufferMalla;
	
	private boolean poligonoSimple;
	
	public DesignOpenGLRenderer(Context context)
	{        
		super(context);
		
        estado = TDesignEstado.Dibujando;

        puntos = new FloatArray();
        poligonoSimple = false;
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		switch(estado)
		{
			case Dibujando:
				if(puntos.size > 0)
				{
					dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPoligono);
					
					if(puntos.size > 2)
					{
						dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPoligono);
					}
				}
			break;
			case Triangulando:
			case Terminado:
				dibujarBuffer(gl, GL10.GL_LINES, SIZELINE, Color.BLACK, bufferMalla);
			break;
			default:
			break;
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		estado = TDesignEstado.Dibujando;
		
		puntos.clear();
		
		vertices = null;
		triangulos = null;
		contorno = null;
	}
	
	@Override
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
	}
	
	
	private void anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
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
			
			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > MAX_DISTANCE_PIXELS;
		}
		
		if(anyadir)
		{
			puntos.add(worldX);
			puntos.add(worldY);
			
			bufferPoligono = construirBufferListaPuntos(puntos);
		}
	}
	
	@Override
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	@Override
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			triangulator = new Triangulator(puntos);
			
			poligonoSimple = triangulator.getPoligonSimple();
			vertices = triangulator.getVertices();
			triangulos = triangulator.getTriangulos();
			contorno = triangulator.getContorno();
			
			if(poligonoSimple)
			{
				bufferMalla = construirBufferListaTriangulos(triangulos, vertices);
			}
		}
	}
	
	@Override
	public void onMultiTouchEvent() { }
	
	/* Selección de Estado */
	
	public boolean seleccionarTriangular()
	{
		if(poligonoSimple)
		{
			estado = TDesignEstado.Triangulando;
			return true;
		}
		
		return false;
	}
	
	/* Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		if(poligonoSimple)
		{
			estado = TDesignEstado.Terminado;
			return new Esqueleto(contorno, vertices, triangulos);
		}
		
		return null;
	}

	public boolean poligonoCompleto()
	{
		return puntos.size >= 6;
	}
	
	/* Métodos de Salvados de Información */
	
	public DesignDataSaved saveData()
	{
		return new DesignDataSaved(puntos, vertices, triangulos, contorno, estado, poligonoSimple);
	}
	
	public void restoreData(DesignDataSaved data)
	{
		estado = data.getEstado();
		puntos = data.getPuntos();
		vertices = data.getVertices();
		triangulos = data.getTriangulos();
		contorno = data.getContorno();
		poligonoSimple = data.getPoligonoSimple();
		
		if(poligonoSimple)
		{
			bufferPoligono = construirBufferListaPuntos(puntos); 
			bufferMalla = construirBufferListaTriangulos(triangulos, vertices);
		}
	}
}
