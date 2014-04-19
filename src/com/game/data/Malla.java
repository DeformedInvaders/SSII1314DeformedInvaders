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
import com.lib.opengl.BufferManager;
import com.lib.opengl.HullArray;
import com.lib.opengl.TriangleArray;
import com.lib.opengl.VertexArray;

public abstract class Malla extends Entidad
{
	// Nombre
	private String nombre;

	// Esqueleto
	protected HullArray contorno;
	private FloatBuffer bufferContorno;

	protected VertexArray vertices;

	protected TriangleArray triangulos;
	private FloatBuffer bufferTriangulos;

	// Animaci�n
	protected List<VertexArray> listaVerticesAnimacion;

	protected int posicionAnimacion;
	protected VertexArray verticesAnimacion;
	protected FloatBuffer bufferTriangulosAnimacion;
	protected FloatBuffer bufferContornoAnimacion;

	// Texturas
	private Textura textura;
	private FloatBuffer coordTextura;

	// Pegatinas
	private Pegatinas pegatinas;

	protected float posicionX, posicionY;
	protected boolean esqueletoReady, texturaReady, movimientosReady;

	/* M�todos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (texturaReady)
		{
			// Textura
			textura.cargarTextura(gl, renderer, context, tipo);
	
			// Pegatinas
			pegatinas.cargarTexturas(gl, renderer, context, tipo, id);
		}
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		if (texturaReady)
		{
			// Textura
			textura.descargarTextura(renderer, tipo);
	
			// Pegatinas
			pegatinas.descargarTextura(renderer, tipo, id);
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			gl.glPushMatrix();
	
				gl.glTranslatef(posicionX, posicionY, 0.0f);
		
				// Textura
				textura.dibujar(gl, renderer, bufferTriangulosAnimacion, coordTextura, tipo);
		
				// Contorno
				BufferManager.dibujarBuffer(gl, Color.BLACK, bufferContornoAnimacion);
		
				// Pegatinas
				pegatinas.dibujar(gl, renderer, verticesAnimacion, tipo, id);
	
			gl.glPopMatrix();
		}
	}

	/* M�todos de Animaci�n */

	protected void iniciar()
	{
		if (movimientosReady)
		{
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

	/* M�todos de Modificaci�n de Informaci�n */

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

	public void setNombre(String n)
	{
		nombre = n;
	}

	/* M�todos de Obtenci�n de Informaci�n */

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

	public String getNombre()
	{
		return nombre;
	}
}
