package com.create.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.data.Handle;
import com.project.data.Pegatinas;
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;
	
	private final int NUM_HANDLES;
	private final static int NUM_ITER = 20;
	
	// Modo Grabado
	private TDeformEstado estado;
	private boolean modoGrabar;
	
	/* Movimientos */
	
	// Información de Movimiento
	private List<FloatArray> listaHandlesAnimacion; 
	private List<FloatArray> listaVerticesAnimacion; 
	
	//Informacion para la reproduccion de la animacion
	private FloatArray verticesAnimacion;
	private FloatBuffer triangulosAnimacion;
	private FloatBuffer contornoAnimacion;
	private int posicionAnimacion;
	
	/* Esqueleto */
	
	// Indice de Vertices que forman en ConvexHull
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	// Coordenadas de Vertices	
	private FloatArray vertices;
	private FloatArray verticesModificados;
	
	// Indice de Vertices que forman Triángulos	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	/* Handles */
	
	// Coordenadas de Handles
	private FloatArray handles;
	
	// Indice Vertice asociado a Handles
	private ShortArray indiceHandles;
	
	// Coordenadas de Handles Seleccionados
	private FloatArray handleSeleccionado;
	
	private Handle objetoVertice, objetoHandle, objetoHandleSeleccionado;
	
	/* Textura */
	
	private Bitmap bitmap;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	// Pegatinas
	private Pegatinas pegatinas;
	
	/* SECTION Constructora */
	
	public DeformOpenGLRenderer(Context context, int num_handles, Esqueleto esqueleto, Textura textura)
	{
        super(context);
        
        NUM_HANDLES = num_handles;
        
        estado = TDeformEstado.Nada;
        modoGrabar = false;
        listaHandlesAnimacion = new ArrayList<FloatArray>();
        
        // Esqueleto
		contorno = esqueleto.getContorno();
		vertices = esqueleto.getVertices();
		verticesModificados = vertices.clone();
		triangulos = esqueleto.getTriangulos();
		
		bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
        
		// Handles
        handles = new FloatArray();
        indiceHandles = new ShortArray();
        
        handleSeleccionado = new FloatArray();
        reinciarHandlesSeleccionados();
        
		// Textura
        pegatinas = textura.getPegatinas();
        
		bitmap = textura.getMapaBits().getBitmap();
		coords = textura.getCoordTextura();
		
		bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
        
        objetoHandle = new Handle(20, POINTWIDTH);
        objetoVertice = new Handle(20, POINTWIDTH/2);
        objetoHandleSeleccionado = new Handle(20, 2*POINTWIDTH);
        
		// Deformador
		deformator = new Deformator(vertices, triangulos, handles, indiceHandles);
	}
	
	/* SECTION Métodos Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
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
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		if(estado == TDeformEstado.Reproducir)
		{			
			dibujarPersonaje(gl, triangulosAnimacion, contornoAnimacion, bufferCoords, pegatinas, verticesAnimacion);
		}
		else
		{
			dibujarPersonaje(gl, bufferTriangulos, bufferContorno, bufferCoords, pegatinas, verticesModificados);
			
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);
			
			if(estado != TDeformEstado.Deformar)
			{
				dibujarListaHandle(gl, Color.RED, objetoVertice.getBuffer(), verticesModificados);
			}
			
			// Handles		
			if(handles.size > 0)
			{
				dibujarListaHandle(gl, Color.BLACK, objetoHandle.getBuffer(), handles);
			}
			
			// Seleccionado
			dibujarListaIndiceHandle(gl, Color.RED, objetoHandleSeleccionado.getBuffer(), handleSeleccionado);
			
			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);
		}
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	public void seleccionarAnyadir()
	{
		estado = TDeformEstado.Anyadir;
	}

	public void seleccionarEliminar()
	{
		estado = TDeformEstado.Eliminar;
	}

	public void seleccionarMover()
	{
		estado = TDeformEstado.Deformar;
	}

	/* SECTION Métodos Abstractos de OpenGLRenderer */
	
	@Override
	protected void reiniciar()
	{
		estado = TDeformEstado.Nada;
		modoGrabar = false;
		
		handles.clear();
		indiceHandles.clear();
		reinciarHandlesSeleccionados();
		
		verticesModificados = vertices.clone();
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
		
		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = null;
	}
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{		
		if(estado == TDeformEstado.Anyadir)
		{			
			anyadirHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if(estado == TDeformEstado.Eliminar)
		{
			eliminarHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if(estado == TDeformEstado.Deformar)
		{		
			seleccionarHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			if(modoGrabar)
			{
				listaHandlesAnimacion.add(handles.clone());
			}
		}	
	}
	
	private void anyadirHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{
			// Vértice no pertenece a los Handles
			if(!indiceHandles.contains(j))
			{
				indiceHandles.add(j);
				handles.add(verticesModificados.get(2*j));
				handles.add(verticesModificados.get(2*j+1));
				
				// Añadir Handle Nuevo
				deformator.anyadirHandles(handles, indiceHandles);
			}
		}
	}
	
	private void eliminarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{
			// Vértice no pertenece a los Handles
			if(indiceHandles.contains(j))
			{		
				int pos = indiceHandles.indexOf(j);
				indiceHandles.removeIndex(pos);
				
				handles.removeIndex(2*pos+1);
				handles.removeIndex(2*pos);
				
				// Eliminar Handle
				deformator.anyadirHandles(handles, indiceHandles);
			}
		}
	}
	
	private void seleccionarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{	
			// Vértice pertenece a los Handles
			if(indiceHandles.contains(j))
			{
				// Seleccionar Handle
				int indiceHandleSeleccionado = indiceHandles.indexOf(j);
				handleSeleccionado.set(4*pointer, indiceHandleSeleccionado);
				handleSeleccionado.set(4*pointer+1, 1);
				handleSeleccionado.set(4*pointer+2, handles.get(2*indiceHandleSeleccionado));
				handleSeleccionado.set(4*pointer+3, handles.get(2*indiceHandleSeleccionado+1));
			}
		}
	}
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			// Handle sin Pulsar
			if(handleSeleccionado.get(4*pointer+1) == 0)
			{
				onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
			else
			{
				moverHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
				
				if(modoGrabar)
				{
					listaHandlesAnimacion.add(handles.clone());
				}				
			}
		}
	}
	
	private void moverHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		float frameX = convertToFrameXCoordinate(worldX);
		float frameY = convertToFrameYCoordinate(worldY);
		
		if(inPixelInCanvas(worldX, worldY))
		{
			int indiceHandleSeleccionado = (int) handleSeleccionado.get(4*pointer);
			float lastFrameX = handles.get(2*indiceHandleSeleccionado);
			float lastFrameY = handles.get(2*indiceHandleSeleccionado);
			
			float lastWorldX = convertFromFrameXCoordinate(lastFrameX);
			float lastWorldY = convertFromFrameYCoordinate(lastFrameY);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);			
			
			if(Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3*MAX_DISTANCE_PIXELS)
			{
				handles.set(2*indiceHandleSeleccionado, frameX);
				handles.set(2*indiceHandleSeleccionado+1, frameY);
				
				handleSeleccionado.set(4*pointer+2, frameX);
				handleSeleccionado.set(4*pointer+3, frameY);
			}
		}
	}
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			handleSeleccionado.set(4*pointer, -1);
			handleSeleccionado.set(4*pointer+1, 0);
			
			if(modoGrabar)
			{
				modoGrabar = false;
				estado = TDeformEstado.Nada;
				construirListadeMovimientos();
			}
		}	
	}
	
	private void construirListadeMovimientos()
	{
		int i = 0;
		int r = 0;
		
		if(listaHandlesAnimacion.size() < NUM_ITER)
		{
			r = 1;
		}
		else
		{
			r = listaHandlesAnimacion.size() / NUM_ITER;
		}
		
		FloatArray v = vertices.clone();
		
		while(i < listaHandlesAnimacion.size())
		{
			deformator.moverHandles(listaHandlesAnimacion.get(i), v);
			
			listaVerticesAnimacion.add(v.clone());
			i = i+r;
		}
		
		deformator.moverHandles(listaHandlesAnimacion.get(listaHandlesAnimacion.size()-1), v);
		listaVerticesAnimacion.add(v.clone());
		
	}
	
	@Override
	protected void onMultiTouchEvent()
	{
		if(estado == TDeformEstado.Deformar)
		{
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);
			
			actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
		}
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	public void seleccionarGrabado() 
	{ 
		modoGrabar = true;
		estado = TDeformEstado.Deformar;
		
		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = new ArrayList<FloatArray>();
		
		reiniciarHandles();
		
		verticesModificados = vertices.clone();
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
	
	public void selecionarPlay() 
	{
		estado = TDeformEstado.Reproducir;
		
		iniciarAnimacion();
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
	
	private void reiniciarHandles()
	{
		// Handles
		for(int i = 0; i < indiceHandles.size; i++)
		{
			short pos = indiceHandles.get(i);
			
			float x = vertices.get(2*pos);
			float y = vertices.get(2*pos+1);
			
			handles.set(2*i, x);
			handles.set(2*i+1, y);
		}
	}
	
	private void reinciarHandlesSeleccionados()
	{
        for(int i = 0; i < NUM_HANDLES; i++)
        {
        	// Indice Handle
        	handleSeleccionado.add(-1);
        	// Estado Handle
        	handleSeleccionado.add(0);
        	// Posicion Handle
        	handleSeleccionado.add(0);
        	handleSeleccionado.add(0);
        }
	}
	
	public void seleccionarAudio()
	{
		estado = TDeformEstado.Audio;
	}
	
	public void seleccionarReposo() 
	{
		estado = TDeformEstado.Nada;
	}	

	/* SECTION Métodos de Obtención de Información */

	public boolean isHandlesVacio()
	{
		return indiceHandles.size == 0;
	}
	
	public boolean isEstadoAnyadir() 
	{
		return estado == TDeformEstado.Anyadir;
	}
	
	public boolean isEstadoEliminar() 
	{
		return estado == TDeformEstado.Eliminar;
	}
	
	public boolean isEstadoDeformar() 
	{
		return estado == TDeformEstado.Deformar;
	}
	
	public boolean isEstadoGrabacion() 
	{
		return estado == TDeformEstado.Deformar && modoGrabar;
	}
	
	public boolean isEstadoAudio()
	{
		return estado == TDeformEstado.Audio;
	}
	
	public boolean isEstadoReproduccion()
	{
		return estado == TDeformEstado.Reproducir;
	}
	
	public List<FloatArray> getMovimientos() 
	{ 
		return listaVerticesAnimacion;
	}
	
	public boolean isGrabacionReady() 
	{
		return listaVerticesAnimacion != null && listaVerticesAnimacion.size() > 0;
	}	
	
	/* SECTION Métodos de Guardado de Información */
	
	public DeformDataSaved saveData()
	{
		return new DeformDataSaved(handles, indiceHandles, verticesModificados, estado, listaVerticesAnimacion);
	}
	
	public void restoreData(DeformDataSaved data)
	{
		modoGrabar = false;
		estado = data.getEstado();
		handles = data.getHandles();
		indiceHandles = data.getIndiceHandles();
		verticesModificados = data.getVerticesModificados();
		listaVerticesAnimacion = data.getListaVertices();
		
		deformator.anyadirHandles(handles, indiceHandles);
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
}
