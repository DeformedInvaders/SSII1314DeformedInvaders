package com.view.display;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.MapaBits;
import com.project.data.Movimientos;
import com.project.data.Pegatinas;
import com.project.data.Personaje;
import com.project.main.OpenGLRenderer;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private boolean personajeCargado;
	
	// Pegatinas
	private Pegatinas pegatinas;
	private FloatBuffer coordPegatina;
	private FloatBuffer puntosOjos, puntosBoca, puntosArma;
	private boolean pegatinaOjosCargada, pegatinaBocaCargada, pegatinaArmaCargada;
		
	// Esqueleto	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	// Animación
	private Movimientos movimientos;
	private List<FloatArray> listaVerticesAnimacion;
	private FloatArray verticesAnimacion;
	private FloatBuffer triangulosAnimacion;
	private FloatBuffer contornoAnimacion;
	private int posicionAnimacion;
	private TDisplayEstado estado;
	
	// Captura Pantalla
	private Bitmap captura;
	private int canvasHeight, canvasWidth;
	private TCapturaEstado estadoCaptura; 
	
	// Texturas
	private Bitmap bitmap;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	public DisplayOpenGLRenderer(Context context)
	{
		super(context);
		
		personajeCargado = false;
		estado = TDisplayEstado.Nada;
	}
	
	public DisplayOpenGLRenderer(Context context, Personaje personaje)
	{
        super(context);
        
        personajeCargado = true;
        estado = TDisplayEstado.Nada;
        
        // Esqueleto
        contorno = personaje.getEsqueleto().getContorno();
		vertices = personaje.getEsqueleto().getVertices();
		
		triangulos = personaje.getEsqueleto().getTriangulos();
		
		bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
		
		// Textura
		estadoCaptura = TCapturaEstado.Nada;
        
		bitmap = personaje.getTextura().getMapaBits().getBitmap();
		coords = personaje.getTextura().getCoordTextura();

		bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
		
		movimientos = personaje.getMovimientos();
		
		// Pegatinas
		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
        
        pegatinas = personaje.getTextura().getPegatinas();
        coordPegatina = construirBufferListaPuntos(texture);
        
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		if(personajeCargado)
		{
			// Textura
			cargarTextura(gl, bitmap, nombreTexturas, 0);

	        if(!pegatinaOjosCargada && pegatinas.getIndiceOjos() != -1)
			{
				FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceOjos(), nombreTexturas, 1);
				puntosOjos = construirBufferListaPuntos(puntos);
				pegatinaOjosCargada = true;
			}
			
			if(!pegatinaBocaCargada && pegatinas.getIndiceBoca() != -1)
			{
				FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceBoca(), nombreTexturas, 2);
				puntosBoca = construirBufferListaPuntos(puntos);
				pegatinaBocaCargada = true;
			}
			
			if(!pegatinaArmaCargada && pegatinas.getIndiceArma() != -1)
			{
				FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceArma(), nombreTexturas, 3);
				puntosArma = construirBufferListaPuntos(puntos);
				pegatinaArmaCargada = true;
			}
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
			
		if(personajeCargado)
		{
			
			if(estado == TDisplayEstado.Nada)
			{
				dibujarEsqueleto(gl, bufferTriangulos, bufferContorno, vertices);
			
				if(estadoCaptura == TCapturaEstado.Capturando)
				{
					// Capturar Pantalla
				    MapaBits textura = capturaPantallaPolariod(gl, canvasWidth, canvasHeight);
					captura = textura.getBitmap();
					
					// Desactivar Modo Captura
					estadoCaptura = TCapturaEstado.Terminado;
					
					// Restaurar posición anterior de la Cámara
					restore();
					
					dibujarEsqueleto(gl, bufferTriangulos, bufferContorno, vertices);
				}
				else if(estadoCaptura == TCapturaEstado.Retocando)
				{
					// Marco Oscuro
					dibujarMarco(gl);
				}
			}
			else
			{
				dibujarEsqueleto(gl, triangulosAnimacion, contornoAnimacion, verticesAnimacion);
			}
		}
	}
	
	private void dibujarEsqueleto(GL10 gl, FloatBuffer triangulos, FloatBuffer contorno, FloatArray vertices)
	{
		super.onDrawFrame(gl);
		
		// Textura
		dibujarTextura(gl, triangulos, bufferCoords, nombreTexturas, 0);
			
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, contorno);
		
		// Pegatinas
		if(pegatinaOjosCargada && pegatinas.getIndiceOjos() != -1)
		{
			int indice = pegatinas.getVerticeOjos();
			dibujarPegatina(gl, puntosOjos, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 1);
		}
		
		if(pegatinaBocaCargada && pegatinas.getIndiceBoca() != -1)
		{
			int indice = pegatinas.getVerticeBoca();
			dibujarPegatina(gl, puntosBoca, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 2);
		}
		
		if(pegatinaArmaCargada && pegatinas.getIndiceArma() != -1)
		{
			int indice = pegatinas.getVerticeArma();
			dibujarPegatina(gl, puntosArma, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 3);
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onMultiTouchEvent() { }
	
	/* Métodos de Modificación de Estado */
	
	public void seleccionarRetoque(float height, float width)
	{
		// Construir rectangulos	
		estado = TDisplayEstado.Nada;
		estadoCaptura = TCapturaEstado.Retocando;
	}
	
	public void seleccionarCaptura(int height, int width)
	{
		canvasHeight = height;
		canvasWidth = width;
		estado = TDisplayEstado.Nada;
		estadoCaptura = TCapturaEstado.Capturando;
	}
	
	public void iniciarAnimacion()
	{
		posicionAnimacion = 0;
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		triangulosAnimacion = construirBufferListaTriangulosRellenos(triangulos, verticesAnimacion);
		contornoAnimacion = construirBufferListaIndicePuntos(contorno, verticesAnimacion);
	}
	
	public void reproducirAnimacion()
	{
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		actualizarBufferListaTriangulosRellenos(triangulosAnimacion, triangulos, verticesAnimacion);
		actualizarBufferListaIndicePuntos(contornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion = (posicionAnimacion + 1)  % listaVerticesAnimacion.size();
	}
	
	public void selecionarRun() 
	{
		estado = TDisplayEstado.Run;
		
		listaVerticesAnimacion = movimientos.get(0);
		iniciarAnimacion();	
	}
	
	public void selecionarJump() 
	{
		estado = TDisplayEstado.Jump;
		
		listaVerticesAnimacion = movimientos.get(1);
		iniciarAnimacion();
	}
	
	public void selecionarCrouch() 
	{
		estado = TDisplayEstado.Crouch;
		
		listaVerticesAnimacion = movimientos.get(2);
		iniciarAnimacion();
	}
	
	public void selecionarAttack() 
	{
		estado = TDisplayEstado.Attack;
		
		listaVerticesAnimacion = movimientos.get(3);
		iniciarAnimacion();
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean isEstadoRetoque()
	{
		return estado == TDisplayEstado.Nada && estadoCaptura == TCapturaEstado.Retocando;
	}
	
	public Bitmap getCapturaPantalla()
	{
		if(estadoCaptura == TCapturaEstado.Capturando)
		{	
			while(estadoCaptura != TCapturaEstado.Terminado);
		
			return captura;
		}
		
		return null;
	}
	
	/* Métodos de Guardado de Información */
	
	public void saveData()
	{
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
	}
}
