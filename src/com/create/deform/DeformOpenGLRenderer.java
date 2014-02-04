package com.create.deform;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.lib.math.Deformator;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;
	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	private FloatArray verticesModificados;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	// Coordenadas de Handles
	private FloatArray handles;
	// Indice Vertice asociado a Handles
	private ShortArray indiceHandles;
	// Coordenadas de Handles Seleccionados
	private FloatArray handleSeleccionado;
	
	private Handle objetoVertice, objetoHandle, objetoHandleSeleccionado;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	private int posTextura;
	
	private Bitmap textura;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	private TDeformEstado estado;
	
	private final int NUM_HANDLES;
	
	public DeformOpenGLRenderer(Context context, int num_handles)
	{
        super(context);
        
        NUM_HANDLES = num_handles;
        
        estado = TDeformEstado.Nada;
        
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
        
        nombreTextura = new int[numeroTexturas];
        
        objetoHandle = new Handle(20, POINTWIDTH);
        objetoVertice = new Handle(20, POINTWIDTH/2);
        objetoHandleSeleccionado = new Handle(20, 2*POINTWIDTH);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Textura	
		cargarTextura(gl, textura, nombreTextura, 0);
		dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTextura, posTextura);
		
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		
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
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{		
		this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		this.verticesModificados = vertices.clone();
		
		this.triangulos = esqueleto.getTriangulos();
		
		this.textura = textura.getTextura().getBitmap();
		this.coords = textura.getCoordTextura();
		
		this.deformator = new Deformator(vertices, triangulos, handles, indiceHandles);

		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		this.bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
		this.bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
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
		
        for(int i = 0; i < NUM_HANDLES; i++)
        {
        	// Indice Handle
        	handleSeleccionado.set(4*i, -1);
        	// Estado Handle
        	handleSeleccionado.set(4*i+1, 0);
        }
	}
	
	@Override
	public void onTouchDown(float x, float y, float width, float height, int pos)
	{		
		if(estado == TDeformEstado.Anyadir)
		{			
			anyadirHandle(x, y);
		}
		else if(estado == TDeformEstado.Eliminar)
		{
			eliminarHandle(x, y);
		}
		else if(estado == TDeformEstado.Deformar)
		{						
			seleccionarHandle(x, y, pos);
		}
	}

	private short buscarPixel(float x, float y)
	{
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		return (short) GeometryUtils.isPointInMesh(verticesModificados, nx, ny);
	}
	
	private void anyadirHandle(float x, float y)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(x, y);
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
	
	private void eliminarHandle(float x, float y)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(x, y);
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
	
	private void seleccionarHandle(float x, float y, int pos)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(x, y);
		if(j != -1)
		{	
			// Vértice pertenece a los Handles
			if(indiceHandles.contains(j))
			{
				// Seleccionar Handle
				int indiceHandleSeleccionado = indiceHandles.indexOf(j);
				handleSeleccionado.set(4*pos, indiceHandleSeleccionado);
				handleSeleccionado.set(4*pos+1, 1);
				handleSeleccionado.set(4*pos+2, handles.get(2*indiceHandleSeleccionado));
				handleSeleccionado.set(4*pos+3, handles.get(2*indiceHandleSeleccionado+1));
			}
		}
	}
	
	@Override
	public void onTouchMove(float x, float y, float width, float height, int pos)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			// Handle sin Pulsar
			if(handleSeleccionado.get(4*pos+1) == 0)
			{
				onTouchDown(x, y, width, height, pos);
			}
			else
			{
				moverHandle(x, y, pos);
			}
		}
	}
	
	private void moverHandle(float x, float y, int pos)
	{
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		int indiceHandleSeleccionado = (int) handleSeleccionado.get(4*pos);
		float lastX = handles.get(2*indiceHandleSeleccionado);
		float lastY = handles.get(2*indiceHandleSeleccionado);
		
		if(Math.abs(Intersector.distancePoints(nx, ny, lastX, lastY)) > 2*EPSILON)
		{
			handles.set(2*indiceHandleSeleccionado, nx);
			handles.set(2*indiceHandleSeleccionado+1, ny);
			
			handleSeleccionado.set(4*pos+2, nx);
			handleSeleccionado.set(4*pos+3, ny);
		}
	}
	
	@Override
	public void onTouchUp(float x, float y, float width, float height, int pos)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			onTouchMove(x, y, width, height, pos);
			
			handleSeleccionado.set(4*pos, -1);
			handleSeleccionado.set(4*pos+1, 0);
		}	
	}
	
	@Override
	public void onMultiTouchEvent()
	{
		// Cambiar Posicion de los Handles
		deformator.moverHandles(handles, verticesModificados);
		
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}

	public boolean handlesVacio()
	{
		return indiceHandles.size == 0;
	}
	
	public DeformDataSaved saveData()
	{
		return new DeformDataSaved(handles, indiceHandles, verticesModificados);
	}
	
	public void restoreData(DeformDataSaved data)
	{
		this.handles = data.getHandles();
		this.indiceHandles = data.getIndiceHandles();
		this.verticesModificados = data.getVerticesModificados();
		
		deformator.anyadirHandles(handles, indiceHandles);
		actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
}
