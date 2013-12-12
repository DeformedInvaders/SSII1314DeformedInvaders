package com.example.deform;

import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.OpenGLRenderer;
import com.example.math.Deformator;
import com.example.math.GeometryUtils;
import com.example.math.Intersector;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;
	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	private FloatArray verticesModificados;
	private FloatBuffer bufferVertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	private Handle objetoHandle;
	private FloatArray handles;
	private ShortArray indiceHandles;
	
	private int indiceHandleSeleccionado;
	private FloatArray handleSeleccionado;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	
	private Bitmap textura;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	private TDeformEstado estado;
	
	public DeformOpenGLRenderer(Context context)
	{
        super(context);
        
        estado = TDeformEstado.Nada;
        
        handles = new FloatArray();
        indiceHandles = new ShortArray();
        handleSeleccionado = new FloatArray();
        handleSeleccionado.add(0);
        handleSeleccionado.add(0);
        indiceHandleSeleccionado = -1;
        
        nombreTextura = new int[numeroTexturas];
        
        objetoHandle = new Handle(20, 2*POINTWIDTH);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Textura	
		cargarTextura(gl, textura, nombreTextura, 0);
		dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTextura, 0);
		
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		
		// Vertices
		if(estado != TDeformEstado.Mover)
		{
			dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferVertices);
		}
		
		// Handles		
		if(handles.size > 0)
		{
			dibujarListaHandle(gl, Color.BLACK, objetoHandle.getBuffer(), handles);
		}
		
		// Seleccionado
		if(indiceHandleSeleccionado != -1)
		{		
			dibujarListaHandle(gl, Color.RED, objetoHandle.getBuffer(), handleSeleccionado);
		}	
	}
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		this.verticesModificados = vertices.clone();
		
		this.triangulos = esqueleto.getTriangulos();
		
		this.textura = esqueleto.getTextura().getBitmap();
		this.coords = esqueleto.getCoordTextura();
				
		this.deformator = new Deformator(vertices, triangulos, handles, indiceHandles);

		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		this.bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
		this.bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
		this.bufferVertices = construirBufferListaPuntos(verticesModificados);
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
		estado = TDeformEstado.Seleccionar;
	}

	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		estado = TDeformEstado.Nada;
		    
		handles.clear();
		indiceHandles.clear();
		indiceHandleSeleccionado = -1;
	}
	
	public void onTouchDown(float x, float y, float width, float height)
	{		
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		short j = (short) GeometryUtils.isPointInMesh(verticesModificados, nx, ny);
		if(j != -1)
		{
			if(estado == TDeformEstado.Anyadir)
			{			
				if(!indiceHandles.contains((short) j))
				{
					indiceHandles.add(j);
					handles.add(verticesModificados.get(2*j));
					handles.add(verticesModificados.get(2*j+1));
					
					// Añadir Handle Nuevo
					deformator.computeDeformation(handles, indiceHandles);
				}
			}
			else if(estado == TDeformEstado.Eliminar)
			{
				if(indiceHandles.contains((short) j))
				{					
					int pos = indiceHandles.indexOf(j);
					indiceHandles.removeIndex(pos);
					
					handles.removeIndex(2*pos+1);
					handles.removeIndex(2*pos);
					
					deformator.computeDeformation(handles, indiceHandles);
				}
			}
			else if(estado == TDeformEstado.Seleccionar)
			{						
				if(indiceHandles.contains((short) j))
				{
					// Seleccionar Handle
					indiceHandleSeleccionado = indiceHandles.indexOf(j);
					
					handleSeleccionado.set(0, handles.get(2*indiceHandleSeleccionado));
					handleSeleccionado.set(1, handles.get(2*indiceHandleSeleccionado+1));
					
					estado = TDeformEstado.Mover;
				}
			}
		}
	}
	
	public void onTouchMove(float x, float y, float width, float height)
	{	
		if(estado == TDeformEstado.Seleccionar)
		{
			// TODO:
			onTouchDown(x, y, width, height);
		}
		else if(estado == TDeformEstado.Mover)
		{
			// Conversión Pixel - Punto	
			float nx = xLeft + (xRight-xLeft)*x/width;
			float ny = yBot + (yTop-yBot)*(height-y)/height;
			
			float lastX = handles.get(2*indiceHandleSeleccionado);
			float lastY = handles.get(2*indiceHandleSeleccionado);
			
			if(Math.abs(Intersector.distancePoints(nx, ny, lastX, lastY)) > EPSILON)
			{
				handles.set(2*indiceHandleSeleccionado, nx);
				handles.set(2*indiceHandleSeleccionado+1, ny);
				
				// Cambiar Posicion de los Handles
				verticesModificados = deformator.computeDeformation(handles);
				
				handleSeleccionado.set(0, nx);
				handleSeleccionado.set(1, ny);
				
				actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
				actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
			}
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height)
	{	
		if(estado == TDeformEstado.Mover)
		{
			onTouchMove(x, y, width, height);
			
			actualizarBufferListaPuntos(bufferVertices, verticesModificados);
			
			estado = TDeformEstado.Seleccionar;
			indiceHandleSeleccionado = -1;
		}	
	}

	public boolean handlesVacio()
	{
		return indiceHandles.size == 0;
	}
}
