package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Skeleton;
import com.creation.data.Stickers;
import com.creation.data.Texture;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;

public abstract class Mesh extends Entity
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
	protected Texture textura;
	protected FloatBuffer coordTextura;

	// Pegatinas
	protected Stickers pegatinas;

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

	public void setEsqueleto(Skeleton e)
	{
		contorno = e.getContorno();
		vertices = e.getVertices();
		triangulos = e.getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
		
		esqueletoReady = true;
	}

	public void setTextura(Texture t)
	{
		textura = t;
		pegatinas = textura.getPegatinas();
		coordTextura = BufferManager.construirBufferListaTriangulosRellenos(triangulos, textura.getCoordTextura());
		texturaReady = true;
		
		width = textura.getWidth();
		height = textura.getHeight();
	}
	
	/* Métodos de Obtención de Información */	
	public int getIndiceAnimacion()
	{
		if (listaVerticesAnimacion != null)
		{
			return listaVerticesAnimacion.size();
		}
		
		return 0;
	}

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
	
	public Skeleton getEsqueleto()
	{ 
		if(isEsqueletoReady())
		{
			return new Skeleton(contorno, vertices, triangulos);
		}
		
		return null;
	}

	public Texture getTextura()
	{
		return textura;
	}
}
