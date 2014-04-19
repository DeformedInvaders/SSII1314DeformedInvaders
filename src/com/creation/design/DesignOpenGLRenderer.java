package com.creation.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.lib.buffer.BufferManager;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.Intersector;
import com.project.model.GamePreferences;

public class DesignOpenGLRenderer extends OpenGLRenderer
{
	// Estructura de Datos de la Escena
	private TEstadoDesign estado;
	private Triangulator triangulator;

	private VertexArray puntos;
	private VertexArray vertices;
	private TriangleArray triangulos;
	private HullArray contorno;

	private FloatBuffer bufferPoligono;
	private FloatBuffer bufferMalla;

	private boolean poligonoSimple;

	/* Constructora */

	public DesignOpenGLRenderer(Context context, int color)
	{
		super(context, color);

		estado = TEstadoDesign.Dibujando;

		puntos = new VertexArray();
		poligonoSimple = false;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
				
			if (estado == TEstadoDesign.Dibujando)
			{
				if (puntos.getNumVertices() > 0)
				{
					// Centrado de Marco
					centrarPersonajeEnMarcoInicio(gl);
					
					BufferManager.dibujarBuffer(gl, GL10.GL_POINTS, GamePreferences.POINT_WIDTH, Color.RED, bufferPoligono);
	
					if (puntos.getNumVertices() > 1)
					{
						BufferManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferPoligono);
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
				
				BufferManager.dibujarBuffer(gl, GL10.GL_LINES, GamePreferences.SIZE_LINE, Color.BLACK, bufferMalla);
				
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
		boolean anyadir = true;

		if (puntos.getNumVertices() > 0)
		{
			float lastFrameX = puntos.getLastXVertex();
			float lastFrameY = puntos.getLastYVertex();

			float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
			float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > GamePreferences.MAX_DISTANCE_PIXELS;
		}

		if (anyadir)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
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
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);

			float cFrameX = (frameX + lastFrameX) / 2.0f;
			float cFrameY = (frameY + lastFrameY) / 2.0f;

			escalarVertices(factor, factor, cFrameX, cFrameY, vertices);
			BufferManager.actualizarBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoDesign.Retocando)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);

			float dWorldX = frameX - lastFrameX;
			float dWorldY = frameY - lastFrameY;

			trasladarVertices(dWorldX, dWorldY, vertices);
			BufferManager.actualizarBufferListaTriangulos(bufferMalla, triangulos, vertices);
		}
	}

	@Override
	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoDesign.Retocando)
		{
			float cFrameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float cFrameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			rotarVertices(ang, cFrameX, cFrameY, vertices);
			BufferManager.actualizarBufferListaTriangulos(bufferMalla, triangulos, vertices);
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
		return puntos.getNumVertices() >= 3;
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
