package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.creation.data.MapaBits;
import com.creation.data.Movimientos;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Personaje extends Entidad
{	
	// Nombre
	private String nombre;
	
	// Esqueleto	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	// Animación
	private Movimientos movimientos;
	private List<FloatArray> listaVerticesAnimacion;
	private int posicionAnimacion;
	
	private FloatArray verticesAnimacion;
	private FloatBuffer bufferTriangulosAnimacion;
	private FloatBuffer bufferContornoAnimacion;

	// Texturas
	private MapaBits mapaBits;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	// Pegatinas
	private Pegatinas pegatinas;
	
	/* SECTION Constructora */
	
	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
	}
	
	/* SECTION Métodos abstractos de Entidad */
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer)
	{
		// Textura
		renderer.cargarTexturaMalla(gl, mapaBits.getBitmap(), tipo);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
		{
			if(pegatinas.isCargada(i))
			{
				renderer.cargarTexturaRectangulo(gl, pegatinas.getIndice(i), tipo, i);
			}
		}
	}
	
	public void descargarTextura(OpenGLRenderer renderer)
	{
		// Textura
		renderer.descargarTexturaMalla(tipo);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
        {
			renderer.descargarTexturaRectangulo(tipo, i);
        }
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{			
		gl.glPushMatrix();
		
			gl.glTranslatef(posicion, 0.0f, 0.0f);
			
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
					renderer.dibujarTexturaRectangulo(gl, verticesAnimacion.get(2*indice), verticesAnimacion.get(2*indice+1), tipo, i);
				}
			}
		
		gl.glPopMatrix();
	}
	
	/* SECTION Métodos de Animación */
	
	public void mover() 
	{
		listaVerticesAnimacion = movimientos.get(0);
		iniciarAnimacion();
	}
	
	public void saltar() 
	{
		listaVerticesAnimacion = movimientos.get(1);
		iniciarAnimacion();
	}
	
	public void agachar() 
	{
		listaVerticesAnimacion = movimientos.get(2);
		iniciarAnimacion();
	}
	
	public void atacar() 
	{
		listaVerticesAnimacion = movimientos.get(3);
		iniciarAnimacion();
	}
	
	private void iniciarAnimacion()
	{
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
	
	public void animar()
	{
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulosAnimacion, triangulos, verticesAnimacion);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion = (posicionAnimacion + 1) % listaVerticesAnimacion.size();
	
		//FIXME
		posicion += 10;
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
		
		bufferCoords = BufferManager.construirBufferListaTriangulosRellenos(triangulos, coords);
	}
	
	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		
		reposo();
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

	public Movimientos getMovimientos()
	{
		return movimientos;
	}

	public String getNombre()
	{
		return nombre;
	}
}
