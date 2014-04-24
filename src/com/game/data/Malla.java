package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;

public abstract class Malla extends Entidad
{
	// Esqueleto
	protected HullArray contorno;
	protected FloatBuffer bufferContorno;

	protected VertexArray vertices;

	protected TriangleArray triangulos;
	protected FloatBuffer bufferTriangulos;

	// Animación
	protected List<VertexArray> listaVerticesAnimacion;

	protected int posicionAnimacion;
	protected VertexArray verticesAnimacion;
	protected FloatBuffer bufferTriangulosAnimacion;
	protected FloatBuffer bufferContornoAnimacion;

	// Texturas
	protected Textura textura;
	protected FloatBuffer coordTextura;

	// Pegatinas
	protected Pegatinas pegatinas;

	protected float posicionX, posicionY;
	protected boolean esqueletoReady, texturaReady, movimientosReady;
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (texturaReady)
		{
			// Textura
			textura.cargarTextura(gl, renderer, context, tipoEntidad, idEntidad);
	
			// Pegatinas
			pegatinas.cargarTexturas(gl, renderer, context, tipoEntidad, idEntidad);
		}
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		if (texturaReady)
		{
			// Textura
			textura.descargarTextura(renderer, tipoEntidad, idEntidad);
	
			// Pegatinas
			pegatinas.descargarTextura(renderer, tipoEntidad, idEntidad);
		}
	}
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			// Textura
			textura.dibujar(gl, renderer, bufferTriangulosAnimacion, coordTextura, tipoEntidad, idEntidad);
	
			// Contorno
			OpenGLManager.dibujarBuffer(gl, Color.BLACK, bufferContornoAnimacion);
			
			// Pegatinas
			pegatinas.dibujar(gl, renderer, verticesAnimacion, triangulos, tipoEntidad, idEntidad);
		}
	}

	/* Métodos de Animación */
	
	protected void iniciar()
	{
		if (movimientosReady)
		{
			posicionX = 0.0f;
			posicionY = 0.0f;
	
			posicionAnimacion = 0;
			verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
			bufferTriangulosAnimacion = BufferManager.construirBufferListaTriangulosRellenos(triangulos, verticesAnimacion);
			bufferContornoAnimacion = BufferManager.construirBufferListaIndicePuntos(contorno, verticesAnimacion);
		}
	}

	public void reposo()
	{
		if (movimientosReady)
		{
			verticesAnimacion = vertices;
			bufferTriangulosAnimacion = bufferTriangulos;
			bufferContornoAnimacion = bufferContorno;
		}
	}

	public boolean animar()
	{ 
		if (movimientosReady)
		{
			verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
			BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulosAnimacion, triangulos, verticesAnimacion);
			BufferManager.actualizarBufferListaIndicePuntos(bufferContornoAnimacion, contorno, verticesAnimacion);
			posicionAnimacion++;
	
			return posicionAnimacion == listaVerticesAnimacion.size() - 1;
		}
		return false;
	}

	/* Métodos de Modificación de Información */

	public void setEsqueleto(Esqueleto e)
	{
		contorno = e.getContorno();
		vertices = e.getVertices();
		triangulos = e.getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
		
		esqueletoReady = true;
	}

	public void setTextura(Textura t)
	{
		textura = t;
		pegatinas = textura.getPegatinas();
		coordTextura = BufferManager.construirBufferListaTriangulosRellenos(triangulos, textura.getCoordTextura());

		width = textura.getWidth();
		height = textura.getHeight();
		
		texturaReady = true;
	}

	/* Métodos de Obtención de Información */

	public boolean isEsqueletoReady()
	{
		return esqueletoReady;
	}
	
	public boolean isTexturaReady()
	{
		return texturaReady;
	}
	
	public boolean isMovimientosReady()
	{
		return movimientosReady;
	}
	
	public Esqueleto getEsqueleto()
	{ 
		if(isEsqueletoReady())
		{
			return new Esqueleto(contorno, vertices, triangulos);
		}
		
		return null;
	}

	public Textura getTextura()
	{
		return textura;
	}
}
