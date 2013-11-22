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
import com.example.math.DelaunayTriangulator;
import com.example.math.EarClippingTriangulator;
import com.example.math.Vector2;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class MyOpenGLRenderer implements Renderer {

	private float xLeft, xRight, yTop, yBot, xCentro, yCentro;
	private float RatioViewPort;
	
	//Estructura de Datos de la Escena
	private TEstado estado;
	
	private BSpline<Vector2> bsplineCalculator;
	private ConvexHull convexHullCalculator;
	private DelaunayTriangulator delaunayCalculator;
	private EarClippingTriangulator earClippingCalculator;
	
	private FloatArray puntos;
	private FloatArray mesh;
	
	private FloatArray bspline;
	private FloatArray convexHull;
	private ShortArray delaunay;
	private ShortArray earClipping;
	private ShortArray meshTriangles;
	
	private FloatBuffer bufferPuntos;
	
	private FloatBuffer bufferBSpline;
	private FloatBuffer bufferConvexHull;
	private ArrayList<FloatBuffer> bufferDelaunay;
	private ArrayList<FloatBuffer> bufferEarClipping;
	private ArrayList<FloatBuffer> bufferMeshTriangles;
	
	public MyOpenGLRenderer() {
        xRight = 760.8f; //width;
        xLeft = 0.0f;
        yTop = 1280.0f; //height;
        yBot = 0.0f;
        xCentro = (xRight + xLeft)/2.0f;
        yCentro = (yTop + yBot)/2.0f;
        RatioViewPort = 1.0f;
        
        estado = TEstado.Dibujar;

        puntos = new FloatArray();
        mesh = null;
        
        bspline = null;
        convexHull = null;
        delaunay = null;
        earClipping = null;
        meshTriangles = null;

        bsplineCalculator = null;
        convexHullCalculator = new ConvexHull();
        delaunayCalculator = new DelaunayTriangulator();
        earClippingCalculator = new EarClippingTriangulator();
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
				
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPointSize(10.0f);
		gl.glLineWidth(3.0f);
				
		// Dibujar Segmentos
		switch(estado) {
			case Dibujar:
				if(puntos.size > 2) {
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
		}
		
		// Dibujar Puntos
		if(puntos.size > 0) {
			dibujarBuffer(gl, GL10.GL_POINTS, 1.0f, 0.0f, 0.0f, 1.0f, bufferPuntos);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		RatioViewPort = (float)width/(float)height;		
		float RatioVolVista = (xRight-xLeft)/(yTop-yBot);
		
		if (RatioVolVista >= RatioViewPort) {
			 //Aumentamos yTop-yBot
			 float altoNew = (xRight-xLeft)/RatioViewPort;
			 yTop = yCentro + altoNew/2.0f;
			 yBot = yCentro - altoNew/2.0f;
		}
		else{
			//Aumentamos xRight-xLeft
			float anchoNew = RatioViewPort*(yTop-yBot);
			xRight = xCentro + anchoNew/2.0f;
			xLeft = xCentro - anchoNew/2.0f;
		}
		
		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
				
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	public void zoom(float factor) {
		
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBot)*factor;
		
		xRight = xCentro + newAncho/2.0f;
		xLeft = xCentro - newAncho/2.0f;
		yTop = yCentro + newAlto/2.0f;
		yBot = yCentro - newAlto/2.0f;
	}
	
	public void anyadirPunto(float x, float y) {
		
		if(estado == TEstado.Dibujar) {
			// Conversión Pixel - Punto
			float nx = xLeft + (xRight - xLeft)*x;
			float ny = yBot + (yTop - yBot)*y;
			
			puntos.add(nx);
			puntos.add(ny);
			
			bufferPuntos = construirBuffer(puntos);
		}
	}
	
	public void calcularBSpline() {
			
		if(bspline == null  && puntos.size > 4) {
			estado = TEstado.BSpline;
			bspline = new FloatArray();
			
			int longControl = puntos.size/2;
			Vector2 puntosControl[] = new Vector2[longControl];
			
			for(int i = 0; i < longControl; i++) {
				float x = puntos.get(2*i);
				float y = puntos.get(2*i+1);
				
				puntosControl[i] = new Vector2(x, y);
			}
			
			bsplineCalculator = new BSpline<Vector2>(puntosControl, 3, true);
						
			float t = 0f;
			float n = 100f;
			
			while(t < 1) {
				Vector2 pos = new Vector2();
				bsplineCalculator.valueAt(pos, t);
				
				bspline.add(pos.x);
				bspline.add(pos.y);
								
				t += (1f/n);
			}
		}
		
		bufferBSpline = construirBuffer(bspline);
	}
	
	public void calcularConvexHull() {
		
		if(convexHull == null && puntos.size > 4) {
			estado = TEstado.ConvexHull;
			convexHull = convexHullCalculator.computePolygon(puntos, true);
			bufferConvexHull = construirBuffer(convexHull);
		}
	}
	
	public void calcularDelaunay() {
		
		if(delaunay == null && puntos.size > 4) {
			estado = TEstado.Delaunay;
			delaunay = delaunayCalculator.computeTriangles(puntos, true);
			bufferDelaunay = construirBuffer(delaunay, puntos);
		}
	}
	
	public void calcularEarClipping() {
		
		if(earClipping == null && puntos.size > 4) {
			estado = TEstado.EarClipping;
			earClipping = earClippingCalculator.computeTriangles(puntos);
			bufferEarClipping = construirBuffer(earClipping, puntos);
		}
	}
	
	public void calcularMeshTriangles() {
				
		if(meshTriangles == null && puntos.size > 4) {
			estado = TEstado.MeshGenerator;
			
			if(delaunay == null) {
				delaunay = delaunayCalculator.computeTriangles(puntos, true);
			}
			
			ShortArray delaunayNonConvex = new ShortArray(delaunay);
			delaunayCalculator.trim(delaunayNonConvex, puntos, puntos, 0, puntos.size);
			mesh = new FloatArray(puntos);
			meshTriangles = new ShortArray();
			delaunayCalculator.mesh(delaunayNonConvex, meshTriangles, mesh, 100, 50.0f);
		
			bufferMeshTriangles = construirBuffer(meshTriangles, mesh);
		}
	}
	
	public void reiniciarPuntos() {
		estado = TEstado.Dibujar;
		puntos = new FloatArray();
		mesh = null;
		bspline = null;
		convexHull = null;
		delaunay = null;
		earClipping = null;
		meshTriangles = null;
	}

	private FloatBuffer construirBuffer(FloatArray lista) {
		
		int arrayLong = lista.size;
		float[] arrayPuntos = new float[arrayLong];
		for(int i = 0; i < lista.size; i++) {
			arrayPuntos[i] = lista.get(i);
		}
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(arrayPuntos);
		buffer.position(0);
		
		return buffer;
	}
	
	private ArrayList<FloatBuffer> construirBuffer(ShortArray lista, FloatArray puntos) {
		
		int arrayLong = 2 * 3;
		ArrayList<FloatBuffer> listabuffer = new ArrayList<FloatBuffer>();
		
		int j = 0;
		while(j < lista.size) {
						
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

	private void dibujarBuffer(GL10 gl, int type, float r, float g, float b, float a, FloatBuffer lista) {
		
		gl.glColor4f(r, g, b, a);
		gl.glFrontFace(GL10.GL_CW);
		
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lista);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDrawArrays(type, 0, lista.capacity() / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	private void dibujarBuffer(GL10 gl, int type, float r, float g, float b, float a, ArrayList<FloatBuffer> lista) {
		
		Iterator<FloatBuffer> it = lista.iterator();
		while(it.hasNext()) {
			FloatBuffer buffer = it.next();
			dibujarBuffer(gl, type, r, g, b, a, buffer);
		}
	}
}
