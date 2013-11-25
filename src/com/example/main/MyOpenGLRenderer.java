package com.example.main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.example.math.BSpline;
import com.example.math.ConvexHull;
import com.example.math.DelaunayMeshGenerator;
import com.example.math.DelaunayTriangulator;
import com.example.math.EarClippingTriangulator;
import com.example.math.GeometryUtils;
import com.example.math.Vector2;
import com.example.utils.FloatArray;
import com.example.utils.Mesh;
import com.example.utils.ShortArray;

public class MyOpenGLRenderer implements Renderer
{

	// Parámetros de la Cámara
	private float xLeft, xRight, yTop, yBot, xCentro, yCentro;
	
	// Parámetros del Puerto de Vista
	private int height = 1280;
	private int width = 760;
	
	//Estructura de Datos de la Escena
	private TEstado estado;
	
	private FloatArray puntos;
	private FloatArray puntosDelaunay;
	private FloatArray puntosMesh;
	private FloatArray handles;
	
	private FloatArray lineasBSpline;
	private FloatArray lineasConvexHull;
	private ShortArray triangulosDelaunay;
	private ShortArray triangulosEarClipping;
	private ShortArray triangulosMesh;
	private ShortArray lineasSimple;
	
	/* TEST */
	private FloatArray puntosTest;
	private ShortArray triangulosTest;
	/* TEST */
	
	//Estructura de Datos para OpenGL ES
	private FloatBuffer bufferPuntos;
	private FloatBuffer bufferHandles;
	
	private FloatBuffer bufferBSpline;
	private FloatBuffer bufferConvexHull;
	private ArrayList<FloatBuffer> bufferDelaunay;
	private ArrayList<FloatBuffer> bufferEarClipping;
	private ArrayList<FloatBuffer> bufferMeshTriangles;
	private ArrayList<FloatBuffer> bufferSimple;
	
	/* TEST */
	private ArrayList<FloatBuffer> bufferTest;
	/* TEST*/
	
	public MyOpenGLRenderer()
	{
        xRight = width;
        xLeft = 0.0f;
        yTop = height;
        yBot = 0.0f;
        xCentro = (xRight + xLeft)/2.0f;
        yCentro = (yTop + yBot)/2.0f;
        
        estado = TEstado.Dibujar;

        puntos = new FloatArray();
        handles = new FloatArray();
	}
	
	public float getxLeft()
	{
		return this.xLeft;
	}
	public float getxRight()
	{
		return this.xRight;
	}
	public float getyTop()
	{
		return this.yTop;
	}
	public float getyBot()
	{
		return this.yBot;
	}
	public float getwidth()
	{
		return this.width;
	}
	public float getheight()
	{
		return this.height;
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPointSize(10.0f);
		gl.glLineWidth(3.0f);
				
		// Dibujar Segmentos
		switch(estado)
		{
			case Dibujar:
				if(puntos.size > 2)
				{
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, 1.0f, 1.0f, 1.0f, bufferPuntos);
				}
			break;
			case BSpline:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, 1.0f, 0.0f, 1.0f, bufferBSpline);
			break;
			case ConvexHull:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 0.0f, 0.0f, 1.0f, 1.0f, bufferConvexHull);
			break;
			case Delaunay:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 0.0f, 1.0f, 0.0f, 1.0f, bufferDelaunay);
			break;
			case EarClipping:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 0.5f, 0.5f, 0.5f, 1.0f, bufferEarClipping);
			break;
			case MeshGenerator:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 0.0f, 1.0f, 1.0f, 1.0f, bufferMeshTriangles);
			break;
			case Simple:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, 0.0f, 1.0f, 1.0f, bufferPuntos);
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, 0.0f, 0.0f, 1.0f, bufferSimple);
			break;
			case Test:
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, 1.0f, 1.0f, 1.0f, bufferTest);
			break;
		}
		
		// Dibujar Puntos
		if(puntos.size > 0)
		{
			dibujarBuffer(gl, GL10.GL_POINTS, 1.0f, 0.0f, 0.0f, 1.0f, bufferPuntos);
		}
		
		// Dibujar Handles
		if(estado != TEstado.Dibujar)
		{
			if(handles.size > 0)
			{
				dibujarBuffer(gl, GL10.GL_POINTS, 1.0f, 1.0f, 0.0f, 1.0f, bufferHandles);
			}
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		this.xRight = this.xLeft + width;
		this.yTop = this.yBot + height;
		this.width = width;
		this.height = height;
		
		gl.glViewport(0, 0, (int)this.width, (int)this.height); 	//Reset The Current Viewport
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, this.xLeft, this.xRight, this.yBot, this.yTop);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, this.xLeft, this.xRight, this.yBot, this.yTop);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void zoom(float factor)
	{	
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBot)*factor;
		
		this.xRight = xCentro + newAncho/2.0f;
		this.xLeft = xCentro - newAncho/2.0f;
		this.yTop = yCentro + newAlto/2.0f;
		this.yBot = yCentro - newAlto/2.0f;
	}
	
	public void drag(float width, float height, float dx, float dy)
	{	
		this.xLeft += dx * width;
		this.xRight += dx * width;
		this.yBot += dy * height;
		this.yTop += dy * height;
		
		this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}
	
	public void restore()
	{
		
        this.xRight = width; 
        this.xLeft = 0.0f;
        this.yTop = height;
        this.yBot = 0.0f;
        
        this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}
	
	public void anyadirPunto(float x, float y, float width, float height)
	{
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		float dx = width/(xRight-xLeft);
		float dy = height/(yTop-yBot);
		
		if(estado == TEstado.Dibujar)
		{		
			puntos.add(nx);
			puntos.add(ny);
			
			bufferPuntos = construirBuffer(puntos);
		}
		else
		{ 
			Vector2 p = GeometryUtils.isPointInMesh(puntos, x, height-y, xLeft, yBot, dx, dy);
			
			if(p != null)
			{
				handles.add(p.x);	
				handles.add(p.y);
				
				bufferHandles = construirBuffer(handles);
				return;
			}
			
			if(estado == TEstado.MeshGenerator)
			{
				p = GeometryUtils.isPointInMesh(puntosMesh, x, height-y, xLeft, yBot, dx, dy);
				
				if(p != null)
				{
					handles.add(p.x);	
					handles.add(p.y);
					
					bufferHandles = construirBuffer(handles);
				}
			}
		}
	}
	
	public void bSpline()
	{
		if(puntos.size > 4)
		{
			estado = TEstado.BSpline;
			handles.clear();
			
			if(lineasBSpline == null)
			{
				lineasBSpline = calcularBSpline(puntos, 3, 100f);
				bufferBSpline = construirBuffer(lineasBSpline);
			}
		}
	}
	
	private FloatArray calcularBSpline(FloatArray vertices, int grado, float iter)
	{
		BSpline<Vector2> bsplineCalculator = new BSpline<Vector2>(vertices, grado, true);
		return bsplineCalculator.computeBSpline(0.0f, iter);
	}
	
	public void convexHull()
	{		
		if(puntos.size > 4)
		{
			estado = TEstado.ConvexHull;
			handles.clear();
			
			if(lineasConvexHull == null)
			{
				lineasConvexHull = calcularConvexHull(puntos, false);
				bufferConvexHull = construirBuffer(lineasConvexHull);
			}
		}
	}
	
	private FloatArray calcularConvexHull(FloatArray vertices, boolean ordenados)
	{
		ConvexHull convexHullCalculator = new ConvexHull();
		return convexHullCalculator.computePolygon(vertices, ordenados);
	}
	
	public void delaunay()
	{
		if(puntos.size > 4)
		{
			estado = TEstado.Delaunay;
			handles.clear();
			
			if(triangulosDelaunay == null)
			{
				puntosDelaunay = new FloatArray(puntos);
				triangulosDelaunay = calcularDelaunay(puntosDelaunay, false);
				bufferDelaunay = construirTriangulosBuffer(triangulosDelaunay, puntosDelaunay);
			}
		}
	}
	
	private ShortArray calcularDelaunay(FloatArray vertices, boolean ordenados)
	{
		DelaunayTriangulator delaunayCalculator = new DelaunayTriangulator();
		return delaunayCalculator.computeTriangles(vertices, ordenados);
	}

	public void earClipping()
	{
		if(puntos.size > 4) 
		{
			estado = TEstado.EarClipping;
			handles.clear();
			
			if(triangulosEarClipping == null)
			{
				triangulosEarClipping = calcularEarClipping(puntos);
				bufferEarClipping = construirTriangulosBuffer(triangulosEarClipping, puntos);
			}
		}
	}
	
	private ShortArray calcularEarClipping(FloatArray vertices)
	{
		EarClippingTriangulator earClippingCalculator = new EarClippingTriangulator();
		return earClippingCalculator.computeTriangles(vertices);
	}
	
	public void meshGenerator()
	{
		if(puntos.size > 4)
		{
			estado = TEstado.MeshGenerator;
			handles.clear();
			
			if(triangulosMesh == null)
			{
				Mesh m = calcularMeshGenerator(puntos, 3, 10.0f);
				puntosMesh = m.getVertices();
				triangulosMesh = m.getTriangulos();
				bufferMeshTriangles = construirTriangulosBuffer(triangulosMesh, puntosMesh);
			}
		}
	}
	
	private Mesh calcularMeshGenerator(FloatArray vertices, int profundidad, float longitud)
	{
		DelaunayMeshGenerator delaunayMeshGenerator = new DelaunayMeshGenerator();
		return delaunayMeshGenerator.computeMesh(vertices, profundidad, longitud);
	}
	
	public boolean testSimple()
	{
		if(puntos.size > 4)
		{
			estado = TEstado.Simple;
			handles.clear();
			
			if(lineasSimple == null)
			{
				lineasSimple = calcularPoligonoSimple(puntos, false);
				bufferSimple = construirLineasBuffer(lineasSimple, puntos);
			}
			
			return lineasSimple.size == 0;
		}
		
		return false;
	}
	
	private ShortArray calcularPoligonoSimple(FloatArray vertices, boolean continuo)
	{
		return GeometryUtils.isPolygonSimple(vertices, continuo);
	}
	
	/* TEST */
	public boolean test()
	{
		if(puntos.size > 4)
		{
			estado = TEstado.Test;
			
			//if(testSimple()) {
				// TODO Calcular Iteraciones en función del Area del Poligono
				FloatArray bsplineVertices = calcularBSpline(puntos, 3, 100.0f);
				Mesh m = calcularMeshGenerator(bsplineVertices, 3, 10.0f);
				puntosTest = m.getVertices();
				triangulosTest = m.getTriangulos();
				
				bufferTest = this.construirTriangulosBuffer(triangulosTest, puntosTest);
				
				return true;
			//}
		}
		
		return false;
	}
	/* TEST */
	
	public void reiniciarPuntos()
	{
		estado = TEstado.Dibujar;
		
		puntos.clear();
		puntosMesh = null;
		puntosDelaunay = null;
		handles.clear();
		
		lineasBSpline = null;
		lineasConvexHull = null;
		triangulosDelaunay = null;
		triangulosEarClipping = null;
		triangulosMesh = null;
		lineasSimple = null;
		
		/* TEST */
		puntosTest = null;
		triangulosTest = null;
		/* TEST */ 
	}

	private FloatBuffer construirBuffer(FloatArray lista)
	{	
		int arrayLong = lista.size;
		float[] arrayPuntos = new float[arrayLong];
		for(int i = 0; i < lista.size; i++)
		{
			arrayPuntos[i] = lista.get(i);
		}
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(arrayPuntos);
		buffer.position(0);
		
		return buffer;
	}
	
	private ArrayList<FloatBuffer> construirLineasBuffer(ShortArray lista, FloatArray puntos)
	{
		int arrayLong = 2 * 2;
		ArrayList<FloatBuffer> listabuffer = new ArrayList<FloatBuffer>();
		
		int j = 0;
		while(j < lista.size)
		{
			short a = lista.get(j);
			short b = lista.get(j+1);
			
			float[] arrayPuntos = new float[arrayLong];
			
			arrayPuntos[0] = puntos.get(2*a);
			arrayPuntos[1] = puntos.get(2*a+1);
			
			arrayPuntos[2] = puntos.get(2*b);
			arrayPuntos[3] = puntos.get(2*b+1);		
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			FloatBuffer buffer = byteBuf.asFloatBuffer();
			buffer.put(arrayPuntos);
			buffer.position(0);

			listabuffer.add(buffer);
			
			j = j+2;
		}		
		
		return listabuffer;
	}
	
	private ArrayList<FloatBuffer> construirTriangulosBuffer(ShortArray lista, FloatArray puntos)
	{
		int arrayLong = 2 * 3;
		ArrayList<FloatBuffer> listabuffer = new ArrayList<FloatBuffer>();
		
		int j = 0;
		while(j < lista.size)
		{
			short a = lista.get(j);
			short b = lista.get(j+1);
			short c = lista.get(j+2);
			
			float[] arrayPuntos = new float[arrayLong];
			
			arrayPuntos[0] = puntos.get(2*a);
			arrayPuntos[1] = puntos.get(2*a+1);
			
			arrayPuntos[2] = puntos.get(2*b);
			arrayPuntos[3] = puntos.get(2*b+1);
			
			arrayPuntos[4] = puntos.get(2*c);
			arrayPuntos[5] = puntos.get(2*c+1);			
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			FloatBuffer buffer = byteBuf.asFloatBuffer();
			buffer.put(arrayPuntos);
			buffer.position(0);

			listabuffer.add(buffer);
			
			j = j+3;
		}		
		
		return listabuffer;
	}

	private void dibujarBuffer(GL10 gl, int type, float r, float g, float b, float a, FloatBuffer lista)
	{	
		gl.glColor4f(r, g, b, a);
		gl.glFrontFace(GL10.GL_CW);
		
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lista);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDrawArrays(type, 0, lista.capacity() / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	private void dibujarBuffer(GL10 gl, int type, float r, float g, float b, float a, ArrayList<FloatBuffer> lista)
	{
		Iterator<FloatBuffer> it = lista.iterator();
		while(it.hasNext())
		{
			FloatBuffer buffer = it.next();
			dibujarBuffer(gl, type, r, g, b, a, buffer);
		}
	}
}
