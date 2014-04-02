package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

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
		
	/* SECTION Métodos abstractos de Entidad */
	
	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer)
	{
		// Textura
		renderer.cargarTexturaMalla(gl, mapaBits.getBitmap(), tipo);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
		{
			if(pegatinas.isCargada(i))
			{
				renderer.cargarTexturaRectangulo(gl, pegatinas.getIndice(i), tipo, id, i);
			}
		}
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		// Textura
		renderer.descargarTexturaMalla(tipo);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
        {
			renderer.descargarTexturaRectangulo(tipo, id, i);
        }
	}
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{			
		gl.glPushMatrix();
			//TODO
			gl.glTranslatef(posicionX, posicionY, 0.0f);
			
			// Textura
			renderer.dibujarTexturaMalla(gl, bufferTriangulosAnimacion, bufferCoords, tipo);
				
			// Contorno
			renderer.dibujarBuffer(gl, Color.BLACK, bufferContornoAnimacion);
			
			// Pegatinas
			for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
			{
				if(pegatinas.isCargada(i))
				{
					int indice = pegatinas.getVertice(i);
					renderer.dibujarTexturaRectangulo(gl, verticesAnimacion.get(2*indice), verticesAnimacion.get(2*indice+1), tipo, id, i);
				}
			}
		
		gl.glPopMatrix();
	}
	
	/* SECTION Métodos de Animación */
	
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
		
		return posicionAnimacion == listaVerticesAnimacion.size();
	}
	
	/* SECTION Métodos de Modificación de Información */
	
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
	
	/* SECTION Métodos de Obtención de Información */
	
	public Esqueleto getEsqueleto()
	{
		return new Esqueleto(contorno, vertices, triangulos);
	}

	public Textura getTextura()
	{
		return new Textura(mapaBits, coords, pegatinas);
	}

	public String getNombre()
	{
		return nombre;
	}
}
