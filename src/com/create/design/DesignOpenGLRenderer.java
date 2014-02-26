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
	
	/* SECTION Constructora */
	
	public DesignOpenGLRenderer(Context context)
	{        
		super(context);
		
        estado = TDesignEstado.Dibujando;

        puntos = new FloatArray();
        poligonoSimple = false;
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		if(estado == TDesignEstado.Dibujando)
		{
			if(puntos.size > 0)
			{
				dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPoligono);
				
				if(puntos.size > 2)
				{
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPoligono);
				}
			}
		}
		else
		{
			dibujarBuffer(gl, GL10.GL_LINES, SIZELINE, Color.BLACK, bufferMalla);
			
			if(estado == TDesignEstado.Retocando)
			{
				// Marco Oscuro
				dibujarMarcoLateral(gl);
				dibujarMarcoCentral(gl);
			}
		}
	}
	
	/* SECTION Métodos Abstractos de OpenGLRenderer */
	
	@Override
	protected void reiniciar()
	{
		estado = TDesignEstado.Dibujando;
		
		puntos.clear();
		
		vertices = null;
		triangulos = null;
		contorno = null;
	}
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
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
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TDesignEstado.Dibujando)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
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
	protected void onMultiTouchEvent() { }
	
	@Override
	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);
			
			float cWorldX = (lastWorldX + worldX) / 2.0f;
			float cWorldY = (lastWorldY + worldY) / 2.0f;
			
			escalarVertices(factor, factor, cWorldX, cWorldY, vertices);
			construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}
	
	@Override
	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);

			float dWorldX = worldX - lastWorldX;
			float dWorldY = worldY - lastWorldY;
			
			trasladarVertices(dWorldX, dWorldY, vertices);
			construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if(estado == TDesignEstado.Retocando)
		{
			float cWorldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float cWorldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			rotarVertices(ang, cWorldX, cWorldY, vertices);
			construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	public boolean seleccionarTriangular()
	{
		if(poligonoSimple)
		{
			estado = TDesignEstado.Triangulando;
			return true;
		}
		
		return false;
	}
	
	public void seleccionarRetoque()
	{
		estado = TDesignEstado.Retocando;
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		if(estado == TDesignEstado.Terminado)
		{
			recortarPoligonoDentroMarco(vertices);
			
			return new Esqueleto(contorno, vertices, triangulos);
		}
		
		return null;
	}
	
	public boolean isEstadoDibujando()
	{
		return estado == TDesignEstado.Dibujando;
	}
	
	public boolean isEstadoTriangulando()
	{
		return estado == TDesignEstado.Triangulando;
	}
	
	public boolean isEstadoRetocando()
	{
		return estado == TDesignEstado.Retocando;
	}

	public boolean isPoligonoCompleto()
	{
		return puntos.size >= 6;
	}
	
	public boolean isPoligonoDentroMarco()
	{
		if(isPoligonoDentroMarco(vertices))
		{
			estado = TDesignEstado.Terminado;
			return true;
		}
				
		return false;
	}
	
	/* SECTION Métodos de Guardado de Información */
	
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
