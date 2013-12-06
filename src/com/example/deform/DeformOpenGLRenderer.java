package com.example.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.GLESUtils;
import com.example.main.OpenGLRenderer;
import com.example.math.Deformator;
import com.example.math.GeometryUtils;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;
	
	private FloatArray hull, puntos, handles;
	private ShortArray triangulos, indiceHandles;
	private int handleSeleccionado;
	
	//private static final int numeroTexturas = 1;
	//private int[] nombreTextura;
	
	//private Bitmap textura;
	//private FloatArray coords;
	
	private ArrayList<FloatBuffer> bufferPuntos;
	//private ArrayList<FloatBuffer> bufferCoords;
	private FloatBuffer bufferHandles;
	
	private TDeformEstado estado;
	
	public DeformOpenGLRenderer(Context context)
	{
        super(context);
        
        estado = TDeformEstado.Nada;
        
        handles = new FloatArray();
        indiceHandles = new ShortArray();
        handleSeleccionado = -1;
        
        //nombreTextura = new int[numeroTexturas];
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Dibujar Mesh		
		GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferPuntos);
		
		// Dibujar Handles		
		if(indiceHandles.size > 0)
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_POINTS, 10.0f, Color.BLUE, bufferHandles);
		}
		
		// Dibujar Handle Seleccionado
		if(handleSeleccionado != -1)
		{
			FloatArray seleccion = new FloatArray();
			seleccion.add(handles.get(2*handleSeleccionado));
			seleccion.add(handles.get(2*handleSeleccionado+1));
			FloatBuffer bufferSeleccion = GLESUtils.construirBuffer(seleccion);
			
			GLESUtils.dibujarBuffer(gl, GL10.GL_POINTS, 10.0f, Color.RED, bufferSeleccion);
		}		
	}
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.hull = esqueleto.getMesh();
		this.puntos = hull.clone();
		this.triangulos = esqueleto.getTriangles();
		//this.textura = esqueleto.getTexture().getBitmap();
		//this.coords = esqueleto.getCoords();
				
		this.deformator = new Deformator(hull, triangulos, handles, indiceHandles);

		this.bufferPuntos = GLESUtils.construirTriangulosBuffer(triangulos, hull);
		//this.bufferCoords = GLESUtils.construirTriangulosBuffer(triangulos, coords);
	}

	public void seleccionarPunto(float x, float y, float width, float height)
	{
		// Conversión Pixel - Punto	
		//float nx = xLeft + (xRight-xLeft)*x/width;
		//float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		float dx = width/(xRight-xLeft);
		float dy = height/(yTop-yBot);
		
		if(estado == TDeformEstado.Anyadir)
		{
			short j = (short) GeometryUtils.isPointInMesh(puntos, x, height-y, xLeft, yBot, dx, dy);
			
			if(j != -1)
			{
				if(!indiceHandles.contains((short) j))
				{
					indiceHandles.add(j);
					handles.add(hull.get(2*j));
					handles.add(hull.get(2*j+1));
					
					// Añadir Handle Nuevo
					deformator.computeDeformation(handles, indiceHandles);
					bufferHandles = GLESUtils.construirBuffer(handles);
				}
			}
		}
		else if(estado == TDeformEstado.Eliminar)
		{
			// TODO
		}
		else if(estado == TDeformEstado.Seleccionar)
		{
			short j = (short) GeometryUtils.isPointInMesh(puntos, x, height-y, xLeft, yBot, dx, dy);
						
			if(j != -1)
			{
				if(indiceHandles.contains((short) j))
				{
					// Seleccionar Handle
					handleSeleccionado = indiceHandles.indexOf(j);
					estado = TDeformEstado.Mover;
				}
			}
		}
	}

	public void deseleccionarPunto(float x, float y, float width, float height)
	{
		if(estado == TDeformEstado.Mover)
		{
			// Conversión Pixel - Punto	
			float nx = xLeft + (xRight-xLeft)*x/width;
			float ny = yBot + (yTop-yBot)*(height-y)/height;
			
			handles.set(2*handleSeleccionado, nx);
			handles.set(2*handleSeleccionado+1, ny);
			
			// Cambiar Posicion de los Handles
			puntos = deformator.computeDeformation(handles);
			bufferPuntos = GLESUtils.construirTriangulosBuffer(this.triangulos, this.puntos);
			bufferHandles = GLESUtils.construirBuffer(this.handles);
			
			estado = TDeformEstado.Seleccionar;
			handleSeleccionado  = -1;
		}

	}

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
}
