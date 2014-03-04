package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.creation.data.MapaBits;
import com.creation.data.Movimientos;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class Personaje
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
	
	public Personaje()
	{
		
	}
	
	public void cargar(GL10 gl, OpenGLRenderer renderer)
	{
		// Textura
		renderer.cargarTexturaEsqueleto(gl, mapaBits.getBitmap());
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
		{
			if(pegatinas.isCargada(i))
			{
				renderer.cargarTexturaPegatinas(gl, pegatinas.getIndice(i), i);
			}
		}
	}
	
	public void descargar(OpenGLRenderer renderer)
	{
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
        {
			renderer.descargarTexturaPegatinas(i);
        }
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarPersonaje(gl, bufferTriangulos, bufferContorno, bufferCoords, pegatinas, vertices);
	}
	
	public void dibujarAnimacion(GL10 gl, OpenGLRenderer renderer)
	{
		renderer.dibujarPersonaje(gl, bufferTriangulosAnimacion, bufferContornoAnimacion, bufferCoords, pegatinas, verticesAnimacion);
	}
	
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
	
	public void animar()
	{
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulosAnimacion, triangulos, verticesAnimacion);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion = (posicionAnimacion + 1)  % listaVerticesAnimacion.size();		
	}
	
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
	}
	
	public void setNombre(String n)
	{
		nombre = n;
	}
	
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
