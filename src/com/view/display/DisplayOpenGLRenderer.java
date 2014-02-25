package com.view.display;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

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
	private int posicionAnimacion;
	private TDisplayEstado estado;
	
	private FloatArray verticesAnimacion;
	private FloatBuffer bufferTriangulosAnimacion;
	private FloatBuffer bufferContornoAnimacion;
	
	// Captura Pantalla
	private Bitmap captura;
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
			cargarTexturaEsqueleto(gl, bitmap);
			
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
				dibujarPersonaje(gl, bufferTriangulos, bufferContorno, bufferCoords, pegatinas, vertices);
			
				if(estado == TDisplayEstado.Captura)
				{
					if(estadoCaptura == TCapturaEstado.Capturando)
					{
						// Capturar Pantalla
					    MapaBits textura = capturaPantalla(gl);
						captura = textura.getBitmap();
						
						// Desactivar Modo Captura
						estadoCaptura = TCapturaEstado.Terminado;
						
						// Restaurar posición anterior de la Cámara
						camaraRestore();
						
						super.onDrawFrame(gl);
						
						dibujarTexturaFondo(gl);
						
						dibujarPersonaje(gl, bufferTriangulos, bufferContorno, bufferCoords, pegatinas, vertices);
					}
					else if(estadoCaptura == TCapturaEstado.Retocando)
					{
						// Marco Oscuro
						dibujarMarcoLateral(gl);
						dibujarMarcoCentral(gl);
					}
				}
			}
			else
			{
				dibujarPersonaje(gl, bufferTriangulosAnimacion, bufferContornoAnimacion, bufferCoords, pegatinas, verticesAnimacion);
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
	
	public void seleccionarCaptura()
	{
		if(estado == TDisplayEstado.Captura)
		{			
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
		bufferTriangulosAnimacion = construirBufferListaTriangulosRellenos(triangulos, verticesAnimacion);
		bufferContornoAnimacion = construirBufferListaIndicePuntos(contorno, verticesAnimacion);
	}
	
	public void reproducirAnimacion()
	{
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		actualizarBufferListaTriangulosRellenos(bufferTriangulosAnimacion, triangulos, verticesAnimacion);
		actualizarBufferListaIndicePuntos(bufferContornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion = (posicionAnimacion + 1)  % listaVerticesAnimacion.size();
	}
	
	public void seleccionarRun() 
	{
		estado = TDisplayEstado.Run;
		
		listaVerticesAnimacion = movimientos.get(0);
		iniciarAnimacion();	
	}
	
	public void seleccionarJump() 
	{
		estado = TDisplayEstado.Jump;
		
		listaVerticesAnimacion = movimientos.get(1);
		iniciarAnimacion();
	}
	
	public void seleccionarCrouch() 
	{
		estado = TDisplayEstado.Crouch;
		
		listaVerticesAnimacion = movimientos.get(2);
		iniciarAnimacion();
	}
	
	public void seleccionarAttack() 
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
