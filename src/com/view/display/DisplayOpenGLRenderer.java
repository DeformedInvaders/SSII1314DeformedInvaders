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
import com.project.main.R;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private boolean personajeCargado;
	
	// Pegatinas
	private Pegatinas pegatinas;
		
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
	
	/* SECTION Constructura */
	
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
        pegatinas = personaje.getTextura().getPegatinas();
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// BackGround
		indiceTexturaFondo = R.drawable.background_display;
		
		if(personajeCargado)
		{
			// Textura
			cargarTextura(gl, bitmap, POS_TEXTURE_SKELETON);
			
			// Pegatinas
			for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
			{
				if(pegatinas.isCargada(i))
				{
					cargarTexturaPegatinas(gl, pegatinas.getIndice(i), i);
				}
			}
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Background
		dibujarTexturaFondo(gl);	
		
		if(personajeCargado)
		{
			if(estado == TDisplayEstado.Nada || estado == TDisplayEstado.Captura)
			{
				dibujarEsqueleto(gl, bufferTriangulos, bufferContorno, vertices);
			
				if(estado == TDisplayEstado.Captura)
				{
					if(estadoCaptura == TCapturaEstado.Capturando)
					{
						// Capturar Pantalla
					    MapaBits textura = capturaPantallaPolariod(gl, canvasWidth, canvasHeight);
						captura = textura.getBitmap();
						
						// Desactivar Modo Captura
						estadoCaptura = TCapturaEstado.Terminado;
						
						// Restaurar posición anterior de la Cámara
						restore();
						
						super.onDrawFrame(gl);
						
						dibujarTexturaFondo(gl);
						
						dibujarEsqueleto(gl, bufferTriangulos, bufferContorno, vertices);
					}
					else if(estadoCaptura == TCapturaEstado.Retocando)
					{
						// Marco Oscuro
						dibujarMarco(gl);
					}
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
		// Textura
		dibujarTextura(gl, triangulos, bufferCoords, POS_TEXTURE_SKELETON);
			
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, contorno);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
		{
			if(pegatinas.isCargada(i))
			{
				int indice = pegatinas.getVertice(i);
				dibujarPegatina(gl, vertices.get(2*indice), vertices.get(2*indice+1), i);
			}
		}
	}
	
	/* SECTION Métodos abstractos de OpenGLRenderer */
	
	@Override
	protected void reiniciar() { }
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION Métodos de Modificación de Estado */
	
	public void seleccionarRetoque(float height, float width)
	{
		// Construir rectangulos	
		estado = TDisplayEstado.Captura;
		estadoCaptura = TCapturaEstado.Retocando;
	}
	
	public void seleccionarCaptura(int height, int width)
	{
		if(estado == TDisplayEstado.Captura)
		{
			canvasHeight = height;
			canvasWidth = width;
			
			estadoCaptura = TCapturaEstado.Capturando;
		}
	}
	
	public void seleccionarTerminado()
	{
		if(estado == TDisplayEstado.Captura)
		{
			estado = TDisplayEstado.Nada;
			estadoCaptura = TCapturaEstado.Nada;
		}
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
	
	/* SECTION Métodos de Obtención de Información */
	
	public boolean isEstadoReposo()
	{
		return estado == TDisplayEstado.Nada;
	}
	
	public boolean isEstadoRetoque()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}
	
	public boolean isEstadoCapturando()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Retocando;
	}
	
	public boolean isEstadoTerminado()
	{
		return estado == TDisplayEstado.Captura && estadoCaptura == TCapturaEstado.Terminado;
	}
	
	public boolean isEstadoAnimacion()
	{
		return estado != TDisplayEstado.Nada && estado != TDisplayEstado.Captura;
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
	
	/* SECTION Métodos de Guardado de Información */
	
	public void saveData()
	{
		if(personajeCargado)
		{
			for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
	        {
	        	descargarTexturaPegatinas(i);
	        }
		}
	}
}
