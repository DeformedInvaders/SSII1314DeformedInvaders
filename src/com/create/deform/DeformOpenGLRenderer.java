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
	
	private FloatArray handles;
	private ShortArray indiceHandles;
	
	private FloatArray handleSeleccionado;
	
	private Handle objetoVertice, objetoHandle;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	private int posTextura;
	
	private Bitmap textura;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	private TDeformEstado estado;
	private TTouchEstado estadoPos1, estadoPos2;

	private static final int POS1 = 0;
	private static final int POS2 = 3;
	
	public DeformOpenGLRenderer(Context context)
	{
        super(context);
        
        estado = TDeformEstado.Nada;
        estadoPos1 = TTouchEstado.Up;
        estadoPos2 = TTouchEstado.Up;
        
        handles = new FloatArray();
        indiceHandles = new ShortArray();
        handleSeleccionado = new FloatArray();
        for(int i = 0; i < 2; i++)
        {
        	// Indice Handle
        	handleSeleccionado.add(-1);
        	// Posicion Handle
        	handleSeleccionado.add(0);
        	handleSeleccionado.add(0);
        }
        
        nombreTextura = new int[numeroTexturas];
        
        objetoHandle = new Handle(20, POINTWIDTH);
        objetoVertice = new Handle(20, POINTWIDTH/2);
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
		if(estadoPos1 != TTouchEstado.Move && estadoPos2 != TTouchEstado.Move)
		{
			dibujarListaHandle(gl, Color.RED, objetoVertice.getBuffer(), verticesModificados);
		}
		
		// Handles		
		if(handles.size > 0)
		{
			dibujarListaHandle(gl, Color.BLACK, objetoHandle.getBuffer(), handles);
		}
		
		// Seleccionado
		dibujarListaIndiceHandle(gl, Color.RED, objetoHandle.getBuffer(), handleSeleccionado);
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
        for(int i = 0; i < 2; i++)
        {
        	// Indice Handle
        	handleSeleccionado.add(-1);
        	// Posicion Handle
        	handleSeleccionado.add(0);
        	handleSeleccionado.add(0);
        }
	}
	
	public void onTouchDown(float x, float y, float width, float height)
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
			estadoPos1 = seleccionarHandle(estadoPos1, x, y, POS1);
		}
	}
	
	public void onTouchDown(float x1, float y1, float x2, float y2, float width, float height)
	{		
		// TODO
		
		if(estado == TDeformEstado.Deformar)
		{	
			if(estadoPos1 == TTouchEstado.Up)
			{
				estadoPos1 = seleccionarHandle(estadoPos1, x1, y1, POS1);
			}
			
			if(estadoPos2 == TTouchEstado.Up)
			{
				estadoPos2 = seleccionarHandle(estadoPos2, x2, y2, POS2);
			}
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
		short j = buscarPixel(x, y);
		if(j != -1)
		{
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
		short j = buscarPixel(x, y);
		if(j != -1)
		{
			if(indiceHandles.contains((short) j))
			{					
				int pos = indiceHandles.indexOf(j);
				indiceHandles.removeIndex(pos);
				
				handles.removeIndex(2*pos+1);
				handles.removeIndex(2*pos);
				
				deformator.anyadirHandles(handles, indiceHandles);
			}
		}
	}
	
	private TTouchEstado seleccionarHandle(TTouchEstado estado, float x, float y, int pos)
	{
		if(estado == TTouchEstado.Up)
		{
			short j = buscarPixel(x, y);
			if(j != -1)
			{	
				if(indiceHandles.contains(j))
				{
					// Seleccionar Handle
					int indiceHandleSeleccionado = indiceHandles.indexOf(j);
					handleSeleccionado.set(pos, indiceHandleSeleccionado);
					handleSeleccionado.set(pos+1, handles.get(2*indiceHandleSeleccionado));
					handleSeleccionado.set(pos+2, handles.get(2*indiceHandleSeleccionado+1));
					
					return TTouchEstado.Move;
				}
			}
		}
		
		return estado;
	}
	
	public void onTouchMove(float x, float y, float width, float height)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			if(estadoPos1 == TTouchEstado.Up)
			{
				onTouchDown(x, y, width, height);
			}
			else if(estadoPos1 == TTouchEstado.Move)
			{
				moverHandle(x, y, POS1);
			}
		}
	}
	
	public void onTouchMove(float x1, float y1, float x2, float y2, float width, float height)
	{
		//TODO: Unificar moverHandle(x1, y1, x2, y2)
		
		if(estado == TDeformEstado.Deformar)
		{
			if(estadoPos1 == TTouchEstado.Up)
			{
				onTouchDown(x1, y1, width, height);
			}
			else if(estadoPos1 == TTouchEstado.Move)
			{
				moverHandle(x1, y1, POS1);
			}
			
			if(estadoPos2 == TTouchEstado.Up)
			{
				onTouchDown(x2, y2, width, height);
			}
			else if(estadoPos2 == TTouchEstado.Move)
			{
				moverHandle(x2, y2, POS2);
			}
		}
	}
	
	private void moverHandle(float x, float y, int pos)
	{
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		int indiceHandleSeleccionado = (int) handleSeleccionado.get(pos);
		float lastX = handles.get(2*indiceHandleSeleccionado);
		float lastY = handles.get(2*indiceHandleSeleccionado);
		
		if(Math.abs(Intersector.distancePoints(nx, ny, lastX, lastY)) > 2*EPSILON)
		{
			handles.set(2*indiceHandleSeleccionado, nx);
			handles.set(2*indiceHandleSeleccionado+1, ny);
			
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);
			
			handleSeleccionado.set(pos+1, nx);
			handleSeleccionado.set(pos+2, ny);
			
			actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height)
	{	
		if(estado == TDeformEstado.Deformar)
		{
			onTouchMove(x, y, width, height);
			
			estadoPos1 = TTouchEstado.Up;
			handleSeleccionado.set(POS1, -1);
		}	
	}
	
	public void onTouchUp(float x1, float y1, float x2, float y2, float width, float height)
	{	
		// TODO
		
		if(estado == TDeformEstado.Deformar)
		{
			onTouchMove(x1, y1, x2, y2, width, height);
			
			estadoPos1 = TTouchEstado.Up;
			estadoPos2 = TTouchEstado.Up;
			handleSeleccionado.set(POS1, -1);
			handleSeleccionado.set(POS2, -1);
		}	
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
