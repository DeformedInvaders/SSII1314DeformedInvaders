package com.creation.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.model.GamePreferences;

public class DesignOpenGLRenderer extends OpenGLRenderer
{
	// Estructura de Datos de la Escena
	private TEstadoDesign estado;
	private Triangulator triangulator;

	private FloatArray puntos;
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;

	private FloatBuffer bufferPoligono;
	private FloatBuffer bufferMalla;

	private boolean poligonoSimple;

	/* Constructora */

	public DesignOpenGLRenderer(Context context)
	{
		super(context);

		estado = TEstadoDesign.Dibujando;

		puntos = new FloatArray();
		poligonoSimple = false;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
				
			if (estado == TEstadoDesign.Dibujando)
			{
				if (puntos.size > 0)
				{
					// Centrado de Marco
					centrarPersonajeEnMarcoInicio(gl);
					
					dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferPoligono);
	
					if (puntos.size > 2)
					{
						dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferPoligono);
					}
					
					// Centrado de Marco
					centrarPersonajeEnMarcoFinal(gl);
				}
			}
			else
			{
				if (estado == TEstadoDesign.Retocando)
				{
					dibujarMarcoInterior(gl, Color.LTGRAY);
				}
				
				// Centrado de Marco
				centrarPersonajeEnMarcoInicio(gl);
				
				dibujarBuffer(gl, GL10.GL_LINES, SIZELINE, Color.BLACK, bufferMalla);
				
				// Centrado de Marco
				centrarPersonajeEnMarcoFinal(gl);
			}
	}

	/* Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		estado = TEstadoDesign.Dibujando;

		puntos.clear();

		vertices = null;
		triangulos = null;
		contorno = null;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDesign.Dibujando)
		{
			return anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}

		return false;
	}

	private boolean anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

		boolean anyadir = true;

		if (puntos.size > 0)
		{
			float lastWorldX = puntos.get(puntos.size - 2);
			float lastWorldY = puntos.get(puntos.size - 1);

			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);

			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > GamePreferences.MAX_DISTANCE_PIXELS;
		}

		if (anyadir)
		{
			float frameX = convertToFrameXCoordinate(worldX);
			float frameY = convertToFrameYCoordinate(worldY);
			
			puntos.add(frameX);
			puntos.add(frameY);

			bufferPoligono = BufferManager.construirBufferListaPuntos(puntos);

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDesign.Dibujando)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDesign.Dibujando)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);

			triangulator = new Triangulator(puntos);

			poligonoSimple = triangulator.getPoligonSimple();
			vertices = triangulator.getVertices();
			triangulos = triangulator.getTriangulos();
			contorno = triangulator.getContorno();

			if (poligonoSimple)
			{
				bufferMalla = BufferManager.construirBufferListaTriangulos(triangulos, vertices);
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}

	@Override
	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoDesign.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);

			float cWorldX = (lastWorldX + worldX) / 2.0f;
			float cWorldY = (lastWorldY + worldY) / 2.0f;
			
			float cframeX = convertToFrameXCoordinate(cWorldX);
			float cframeY = convertToFrameYCoordinate(cWorldY);

			escalarVertices(factor, factor, cframeX, cframeY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoDesign.Retocando)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

			float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
			float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);

			float dWorldX = worldX - lastWorldX;
			float dWorldY = worldY - lastWorldY;

			trasladarVertices(dWorldX, dWorldY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoDesign.Retocando)
		{
			float cWorldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float cWorldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			float cframeX = convertToFrameXCoordinate(cWorldX);
			float cframeY = convertToFrameYCoordinate(cWorldY);

			rotarVertices(ang, cframeX, cframeY, vertices);
			BufferManager.construirBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	/* Métodos de Selección de Estado */

	public boolean seleccionarTriangular()
	{
		if (poligonoSimple)
		{
			estado = TEstadoDesign.Triangulando;
			return true;
		}

		return false;
	}

	public void seleccionarRetoque()
	{
		estado = TEstadoDesign.Retocando;
	}

	/* Métodos de Obtención de Información */

	public Esqueleto getEsqueleto()
	{
		if (estado == TEstadoDesign.Terminado)
		{
			return new Esqueleto(contorno, vertices, triangulos);
		}

		return null;
	}

	public boolean isEstadoDibujando()
	{
		return estado == TEstadoDesign.Dibujando;
	}

	public boolean isEstadoTriangulando()
	{
		return estado == TEstadoDesign.Triangulando;
	}

	public boolean isEstadoRetocando()
	{
		return estado == TEstadoDesign.Retocando;
	}

	public boolean isPoligonoCompleto()
	{
		return puntos.size >= 6;
	}

	public boolean isPoligonoDentroMarco()
	{
		if (isPoligonoDentroMarco(vertices))
		{
			estado = TEstadoDesign.Terminado;
			return true;
		}

		return false;
	}

	/* Métodos de Guardado de Información */

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

		if (poligonoSimple)
		{
			bufferPoligono = BufferManager.construirBufferListaPuntos(puntos);
			bufferMalla = BufferManager.construirBufferListaTriangulos(triangulos, vertices);
		}
	}
}
