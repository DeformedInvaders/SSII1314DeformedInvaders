package com.example.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.OpenGLRenderer;
import com.example.math.Deformator;
import com.example.math.GeometryUtils;
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
	private ArrayList<FloatBuffer> bufferTriangulos;
	
	private FloatArray handles;
	private ShortArray indiceHandles;
	private FloatBuffer bufferHandles;
	
	private int handleSeleccionado;
	private FloatBuffer bufferHandleSeleccionado;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	
	private Bitmap textura;
	private FloatArray coords;
	private ArrayList<FloatBuffer> bufferCoords;
	
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
		dibujarListaTextura(gl, bufferTriangulos, bufferCoords, nombreTextura, 0);
		
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
				
		if(estado != TDeformEstado.Mover)
		{	// Edges Mesh
			dibujarListaBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.GRAY, bufferTriangulos);
			
			// Point Mesh
			dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.RED, bufferVertices);
		}
		
		// Handles		
		if(indiceHandles.size > 0)
		{
			dibujarBuffer(gl, GL10.GL_POINTS, POINTWIDTH, Color.YELLOW, bufferHandles);
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
		this.bufferVertices = construirBufferListaPuntos(verticesModificados);
		this.bufferTriangulos = construirBufferListaTriangulos(triangulos, vertices);
		this.bufferCoords = construirBufferListaTriangulos(triangulos, coords);
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
		float dx = width/(xRight-xLeft);
		float dy = height/(yTop-yBot);
		
		short j = (short) GeometryUtils.isPointInMesh(verticesModificados, x, height-y, xLeft, yBot, dx, dy);
		if(j != -1)
		{
			if(estado == TDeformEstado.Anyadir)
			{			
				if(!indiceHandles.contains((short) j))
				{
					indiceHandles.add(j);
					handles.add(vertices.get(2*j));
					handles.add(vertices.get(2*j+1));
					
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
					
					handles.removeIndex(pos+1);
					handles.removeIndex(pos);
					
					deformator.computeDeformation(handles, indiceHandles);
					bufferHandles = construirBufferListaPuntos(handles);
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
			//onTouchDown(x, y, width, height);
		}
		else if(estado == TDeformEstado.Mover)
		{
			// Conversión Pixel - Punto	
			float nx = xLeft + (xRight-xLeft)*x/width;
			float ny = yBot + (yTop-yBot)*(height-y)/height;
			
			handles.set(2*handleSeleccionado, nx);
			handles.set(2*handleSeleccionado+1, ny);
			
			// Cambiar Posicion de los Handles
			verticesModificados = deformator.computeDeformation(handles);
			
			bufferContorno = construirBufferListaIndicePuntos(contorno, verticesModificados);
			bufferTriangulos = construirBufferListaTriangulos(triangulos, verticesModificados);
			bufferHandles = construirBufferListaIndicePuntos(indiceHandles, verticesModificados);
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height)
	{	
		if(estado == TDeformEstado.Mover)
		{
			onTouchMove(x, y, width, height);
			
			bufferVertices = construirBufferListaPuntos(verticesModificados);
			
			estado = TDeformEstado.Seleccionar;
			handleSeleccionado  = -1;
		}	
	}
}
