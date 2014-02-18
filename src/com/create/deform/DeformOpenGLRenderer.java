package com.create.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.lib.math.Deformator;
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
	
	// Información de Movimiento. Posición de los Handles en los pasos intermedios.
	private List<FloatArray> listaHandles; 
	private List<FloatArray> listaVertices; 
	
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
	
	// Pegatinas
	private Pegatinas pegatinas;
	private FloatBuffer coordPegatina;
	private FloatBuffer puntosOjos, puntosBoca, puntosArma;
	private boolean pegatinaOjosCargada, pegatinaBocaCargada, pegatinaArmaCargada;
	
	private Bitmap bitmap;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	public DeformOpenGLRenderer(Context context, int num_handles, Esqueleto esqueleto, Textura textura)
	{
        super(context);
        
        NUM_HANDLES = num_handles;
        
        estado = TDeformEstado.Nada;
        modoGrabar = false;
        listaHandles = new ArrayList<FloatArray>();
        
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
        
		// Textura
        float texture[] = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
        
        pegatinas = textura.getPegatinas();
        coordPegatina = construirBufferListaPuntos(texture);
        
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
        
		bitmap = textura.getTextura().getBitmap();
		coords = textura.getCoordTextura();
		
		bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
        
        objetoHandle = new Handle(20, POINTWIDTH);
        objetoVertice = new Handle(20, POINTWIDTH/2);
        objetoHandleSeleccionado = new Handle(20, 2*POINTWIDTH);
        
		// Deformador
		deformator = new Deformator(vertices, triangulos, handles, indiceHandles);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		// Textura
		cargarTextura(gl, bitmap, nombreTexturas, 0);
		
		// Pegatinas
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
	
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Textura
		dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTexturas, 0);

		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		
		// Pegatinas
		if(pegatinaOjosCargada && pegatinas.getIndiceOjos() != -1)
		{
			int indice = pegatinas.getVerticeOjos();
			dibujarPegatina(gl, puntosOjos, coordPegatina, verticesModificados.get(2*indice), verticesModificados.get(2*indice+1), nombreTexturas, 1);
		}
		
		if(pegatinaBocaCargada && pegatinas.getIndiceBoca() != -1)
		{
			int indice = pegatinas.getVerticeBoca();
			dibujarPegatina(gl, puntosBoca, coordPegatina, verticesModificados.get(2*indice), verticesModificados.get(2*indice+1), nombreTexturas, 2);
		}
		
		if(pegatinaArmaCargada && pegatinas.getIndiceArma() != -1)
		{
			int indice = pegatinas.getVerticeArma();
			dibujarPegatina(gl, puntosArma, coordPegatina, verticesModificados.get(2*indice), verticesModificados.get(2*indice+1), nombreTexturas, 3);
		}
		
		// Vertices
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
	}
	
	/* Selección de Estado */
	
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

	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		estado = TDeformEstado.Nada;
		    
		handles.clear();
		indiceHandles.clear();
		
		verticesModificados = vertices.clone();
		
		listaHandles.clear();
		listaVertices = null;
		modoGrabar = false;
		
        for(int i = 0; i < NUM_HANDLES; i++)
        {
        	// Indice Handle
        	handleSeleccionado.set(4*i, -1);
        	// Estado Handle
        	handleSeleccionado.set(4*i+1, 0);
        }
	}
	
	@Override
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
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
			
			anyadirMovimiento();
		}	
	}
	
	private void anyadirHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{
			// Vértice no pertenece a los Handles
			if(!indiceHandles.contains((short) j))
			{
				indiceHandles.add(j);
				handles.add(verticesModificados.get(2*j));
				handles.add(verticesModificados.get(2*j+1));
				
				// Añadir Handle Nuevo
				deformator.anyadirHandles(handles, indiceHandles);
			}
		}
	}
	
	private void anyadirMovimiento()
	{
		if(modoGrabar)
			listaHandles.add(handles.clone());
	}
	
	private void eliminarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{
			// Vértice no pertenece a los Handles
			if(indiceHandles.contains((short) j))
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
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
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
				
				anyadirMovimiento();				
			}
		}
	}
	
	private void moverHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		if(inPixelInCanvas(worldX, worldY))
		{
			int indiceHandleSeleccionado = (int) handleSeleccionado.get(4*pointer);
			float lastWorldX = handles.get(2*indiceHandleSeleccionado);
			float lastWorldY = handles.get(2*indiceHandleSeleccionado);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);
			
			if(Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3*MAX_DISTANCE_PIXELS)
			{
				handles.set(2*indiceHandleSeleccionado, worldX);
				handles.set(2*indiceHandleSeleccionado+1, worldY);
				
				handleSeleccionado.set(4*pointer+2, worldX);
				handleSeleccionado.set(4*pointer+3, worldY);
			}
		}
	}
	
	@Override
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
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
				reducirMovimientos();
			}
		}	
	}
	
	private void reducirMovimientos()
	{
		int i = 0;
		int r = 0;
		
		if(listaHandles.size() < NUM_ITER)
		{
			r = 1;
		}
		else
		{
			r = listaHandles.size() / NUM_ITER;
		}
		
		FloatArray v = vertices.clone();
		
		while(i < listaHandles.size())
		{
			deformator.moverHandles(listaHandles.get(i), v);
			listaVertices.add(v);
			i = i+r;
		}
		
		deformator.moverHandles(listaHandles.get(listaHandles.size()-1), v);
		listaVertices.add(v);
		
	}
	
	@Override
	public void onMultiTouchEvent()
	{
		if(estado == TDeformEstado.Deformar)
		{
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);
			
			actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
		}
	}
	
	/* Métodos de Actualización de Estado */
	
	public void seleccionarGrabado() 
	{ 
		modoGrabar = true;
		estado = TDeformEstado.Deformar;
		verticesModificados = vertices.clone();
		listaHandles.clear();
		listaVertices = new ArrayList<FloatArray>();
	}

	/* Métodos de Obtención de Información */

	public boolean handlesVacio()
	{
		return indiceHandles.size == 0;
	}
	
	public boolean getEstadoGrabacion() 
	{
		return modoGrabar;
	}
	
	public List<FloatArray> getMovimientos() 
	{ 
		return listaVertices;
	}
	
	/* Métodos de Salvados de Información */
	
	public DeformDataSaved saveData()
	{
		return new DeformDataSaved(handles, indiceHandles, verticesModificados, estado, listaVertices);
	}
	
	public void restoreData(DeformDataSaved data)
	{
		estado = data.getEstado();
		handles = data.getHandles();
		indiceHandles = data.getIndiceHandles();
		verticesModificados = data.getVerticesModificados();
		listaVertices = data.getListaVertices();
		
		deformator.anyadirHandles(handles, indiceHandles);
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
}
