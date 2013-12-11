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
	
	private FloatArray handles;
	private ShortArray indiceHandles;
	private FloatBuffer bufferHandles;
	
	private int handleSeleccionado;
	private FloatBuffer bufferHandleSeleccionado;
	
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
        handleSeleccionado = -1;
        
        nombreTextura = new int[numeroTexturas];
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
		
		if(estado != TDeformEstado.Mover)
		{
			dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferVertices);
		}
		
		// Handles		
		if(indiceHandles.size > 0)
		{
			dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.BLACK, bufferHandles);
		}
		
		// Seleccionado
		if(handleSeleccionado != -1)
		{			
			dibujarBuffer(gl, GL10.GL_POINTS, 2*POINTWIDTH, Color.RED, bufferHandleSeleccionado);
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
		handleSeleccionado = -1;
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
					bufferHandles = construirBufferListaPuntos(handles);
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
					actualizarBufferListaPuntos(bufferHandles, handles);
				}
			}
			else if(estado == TDeformEstado.Seleccionar)
			{						
				if(indiceHandles.contains((short) j))
				{
					// Seleccionar Handle
					handleSeleccionado = indiceHandles.indexOf(j);
					
					float[] array = new float[2];
					array[0] = handles.get(2*handleSeleccionado);
					array[1] = handles.get(2*handleSeleccionado+1);
					bufferHandleSeleccionado = construirBufferListaPuntos(array);
					
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
			
			float lastX = handles.get(2*handleSeleccionado);
			float lastY = handles.get(2*handleSeleccionado);
			
			if(Math.abs(Intersector.distancePoints(nx, ny, lastX, lastY)) > EPSILON)
			{
				handles.set(2*handleSeleccionado, nx);
				handles.set(2*handleSeleccionado+1, ny);
				
				// Cambiar Posicion de los Handles
				verticesModificados = deformator.computeDeformation(handles);
				
				bufferHandleSeleccionado.put(0, nx);
				bufferHandleSeleccionado.put(1, ny);
				
				actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
				actualizarBufferListaPuntos(bufferHandles, handles);
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
			handleSeleccionado = -1;
		}	
	}
}
