package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.creation.data.MapaBits;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public abstract class Malla extends Entidad
{
	// Nombre
	private String nombre;

	// Esqueleto
	protected ShortArray contorno;
	private FloatBuffer bufferContorno;

	protected FloatArray vertices;

	protected ShortArray triangulos;
	private FloatBuffer bufferTriangulos;

	// Animación
	protected List<FloatArray> listaVerticesAnimacion;

	protected int posicionAnimacion;
	protected FloatArray verticesAnimacion;
	protected FloatBuffer bufferTriangulosAnimacion;
	protected FloatBuffer bufferContornoAnimacion;

	// Texturas
	private MapaBits mapaBits;
	private FloatArray coords;
	private FloatBuffer bufferCoords;

	// Pegatinas
	private Pegatinas pegatinas;

	protected float posicionX, posicionY;

	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		// Textura
		renderer.cargarTexturaMalla(gl, mapaBits.getBitmap(), tipo);

		// Pegatinas
		pegatinas.cargarTexturas(gl, renderer, context, tipo, id);
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		// Textura
		renderer.descargarTexturaMalla(tipo);

		// Pegatinas
		pegatinas.descargarTextura(renderer, tipo, id);
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();

			gl.glTranslatef(posicionX, posicionY, 0.0f);
	
			// Textura
			renderer.dibujarTexturaMalla(gl, bufferTriangulosAnimacion, bufferCoords, tipo);
	
			// Contorno
			renderer.dibujarBuffer(gl, Color.BLACK, bufferContornoAnimacion);
	
			// Pegatinas
			pegatinas.dibujar(gl, renderer, verticesAnimacion, tipo, id);

		gl.glPopMatrix();
	}

	/* Métodos de Animación */

	protected void iniciar()
	{
		posicionY = 0.0f;

		posicionAnimacion = 0;
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		bufferTriangulosAnimacion = BufferManager.construirBufferListaTriangulosRellenos(triangulos, verticesAnimacion);
		bufferContornoAnimacion = BufferManager.construirBufferListaIndicePuntos(contorno, verticesAnimacion);
	}

	public void reposo()
	{
		verticesAnimacion = vertices;
		bufferTriangulosAnimacion = bufferTriangulos;
		bufferContornoAnimacion = bufferContorno;
	}

	public boolean animar()
	{ 
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulosAnimacion, triangulos, verticesAnimacion);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion++;

		return posicionAnimacion == listaVerticesAnimacion.size() - 1;
	}

	/* Métodos de Modificación de Información */

	public void setEsqueleto(Esqueleto e)
	{
		contorno = e.getContorno();
		vertices = e.getVertices();
		triangulos = e.getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
	}

	public void setTextura(Textura t)
	{
		mapaBits = t.getMapaBits();
		pegatinas = t.getPegatinas();
		coords = t.getCoordTextura();

		width = mapaBits.getWidth();
		height = mapaBits.getHeight();

		bufferCoords = BufferManager.construirBufferListaTriangulosRellenos(triangulos, coords);
	}

	public void setNombre(String n)
	{
		nombre = n;
	}

	/* Métodos de Obtención de Información */

	public Esqueleto getEsqueleto()
	{ 
		if (contorno == null || vertices == null || triangulos == null)
		{
			return null;
		}
		
		return new Esqueleto(contorno, vertices, triangulos);
	}

	public Textura getTextura()
	{
		if (mapaBits == null || coords == null || pegatinas == null)
		{
			return null;
		}
		
		return new Textura(mapaBits, coords, pegatinas);
	}

	public String getNombre()
	{
		return nombre;
	}
}
