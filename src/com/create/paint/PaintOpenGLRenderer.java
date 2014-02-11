package com.create.paint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.data.MapaBits;
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class PaintOpenGLRenderer extends OpenGLRenderer
{		
	// Estructura de datos
	private List<Polilinea> listaLineas;
	private FloatArray lineaActual;
	private FloatBuffer bufferLineaActual;
	
	private TPaintEstado estado;
	
	private int colorPaleta;
	private int sizeLinea;
	
	// Esqueleto
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	private FloatBuffer bufferVertices;
	
	private ShortArray triangulos;
	
	private int color;
	
	// Texturas
	private int canvasHeight, canvasWidth;
	
	private boolean modoCaptura;
	private boolean capturaTerminada;
	
	private MapaBits textura;
	private FloatArray coordsTextura;
	
	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	/* Constructora*/	
	
	public PaintOpenGLRenderer(Context context, Esqueleto esqueleto)
	{
        super(context);
        
        this.estado = TPaintEstado.Nada;
        
        this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		this.triangulos = esqueleto.getTriangulos();
		
		this.bufferVertices = construirBufferListaTriangulosRellenos(triangulos, vertices);
		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
        
        this.listaLineas = new ArrayList<Polilinea>();
        this.lineaActual = null;
        
        this.color = Color.WHITE;
        
        this.colorPaleta = Color.GREEN;
        this.sizeLinea = 6;
        
        this.anteriores = new Stack<Accion>();
        this.siguientes = new Stack<Accion>();
        
        this.modoCaptura = false;
        this.capturaTerminada = false;
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		if (modoCaptura)
		{	
			// Guardar posición actual de la Cámara
			salvarCamara();
			
			// Restaurar Cámara posición inicial
			restore();
			
			dibujarEsqueleto(gl);
			
			// Capturar Pantalla
		    this.textura = capturaPantalla(gl, canvasHeight, canvasWidth);
		    
		    // Construir Textura
			this.coordsTextura = construirTextura(vertices, textura.getWidth(), textura.getHeight());
			
			// Desactivar Modo Captura
			this.modoCaptura = false;
			this.capturaTerminada = true;
			
			// Restaurar posición anterior de la Cámara
			recuperarCamara();
		}
		
		dibujarEsqueleto(gl);
		
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
	}
	
	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		// Esqueleto
		dibujarBuffer(gl, GL10.GL_TRIANGLES, SIZELINE, color, bufferVertices);
		
		// Detalles
		Iterator<Polilinea> it = listaLineas.iterator();
		while(it.hasNext())
		{
			Polilinea polilinea = it.next();
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize(), polilinea.getColor(), polilinea.getBuffer());
		}
		
		if(lineaActual != null)
		{
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, sizeLinea, colorPaleta, bufferLineaActual);
		}
	}
	

	/* Selección de Estado */
	
	public TPaintEstado getEstado()
	{
		return estado;
	}
	
	public void seleccionarMano()
	{
		guardarPolilinea();
		estado = TPaintEstado.Mano;
	}
	
	public void seleccionarPincel()
	{
		guardarPolilinea();
		estado = TPaintEstado.Pincel;
	}
	
	public void seleccionarCubo()
	{
		guardarPolilinea();
		estado = TPaintEstado.Cubo;
	}
	
	public void seleccionarColor(int color)
	{
		colorPaleta = color;
	}
	
	public int getColorPaleta()
	{
		return colorPaleta;
	}

	public void setColorPaleta(int colorPaleta)
	{
		this.colorPaleta = colorPaleta;
	}
	
	public void seleccionarSize(int pos)
	{
		if(pos == 0)
		{
			sizeLinea = 6;
		}
		else if(pos == 1)
		{
			sizeLinea = 11;
		}
		else if(pos == 2)
		{
			sizeLinea = 16;
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		this.lineaActual = null;
		this.listaLineas.clear();
		
		this.anteriores.clear();
		this.siguientes.clear();
		
		this.estado = TPaintEstado.Nada;
		this.color = Color.WHITE;
		this.sizeLinea = 6;
	}
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(estado == TPaintEstado.Pincel)
		{
			anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if(estado == TPaintEstado.Cubo)
		{			
			pintarEsqueleto(pixelX, pixelY, screenWidth, screenHeight);
		}
	}
	
	private synchronized void anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		if(lineaActual == null)
		{
			lineaActual = new FloatArray();
		}
		
		boolean anyadir = true;
		
		if(lineaActual.size > 0)
		{
			float lastWorldX = lineaActual.get(lineaActual.size-2);
			float lastWorldY = lineaActual.get(lineaActual.size-1);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);
			
			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > EPSILON;
		}
		
		if(anyadir)
		{
			lineaActual.add(worldX);
			lineaActual.add(worldY);
			
			bufferLineaActual = construirBufferListaPuntos(lineaActual);
		}
	}
	
	private synchronized void pintarEsqueleto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
				
		if(GeometryUtils.isPointInsideMesh(contorno, vertices, worldX, worldY))
		{
			if(colorPaleta != color)
			{
				color = colorPaleta;
				this.anteriores.push(new Accion(colorPaleta));
				this.siguientes.clear();
			}
		}
	}
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TPaintEstado.Pincel)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TPaintEstado.Pincel)
		{
			guardarPolilinea();
		}		
	}
	
	public void onMultiTouchEvent() {}
	
	/* Métodos de modificación de Linea Actual */
	
	private synchronized void guardarPolilinea()
	{
		if(lineaActual != null)
		{
			Polilinea polilinea = new Polilinea(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);
			
			this.listaLineas.add(polilinea);
			this.anteriores.push(new Accion(polilinea));
			this.siguientes.clear();
			this.lineaActual = null;
		}
	}
	
	/* Métodos de modificación de Buffers de estado */

	public synchronized void anteriorAccion()
	{
		guardarPolilinea();
		
		if(!anteriores.isEmpty())
		{
			Accion accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public synchronized void siguienteAccion()
	{
		guardarPolilinea();
		
		if(!siguientes.isEmpty())
		{
			Accion accion = siguientes.lastElement();
			siguientes.remove(siguientes.size()-1);
			anteriores.push(accion);
			actualizarEstado(anteriores);
		}
	}
	
	private synchronized void actualizarEstado(Stack<Accion> pila)
	{
		this.color = Color.WHITE;
		this.listaLineas = new ArrayList<Polilinea>();
		
		Iterator<Accion> it = pila.iterator();
		while(it.hasNext())
		{
			Accion accion = it.next();
			if(accion.getTipo() == 0)
			{
				this.color = accion.getColor();
			}
			else
			{
				this.listaLineas.add(accion.getLinea());
			}
		}
	}
	
	public boolean bufferSiguienteVacio()
	{
		return siguientes.isEmpty();
	}

	public boolean bufferAnteriorVacio()
	{
		return anteriores.isEmpty();
	}
	
	/* Captura de Textura */
	
	public void capturaPantalla(int height, int width)
	{
		guardarPolilinea();
		
		this.canvasHeight = height;
		this.canvasWidth = width;
		this.modoCaptura = true;
	}
	
	public Textura getTextura()
	{
		while(!capturaTerminada);
		
		return new Textura(textura, coordsTextura);
	}
	
	private MapaBits capturaPantalla(GL10 gl, int height, int width)
	{
	    int screenshotSize = width * height;
	    ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
	    bb.order(ByteOrder.nativeOrder());
	    
	    gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
	    
	    int pixelsBuffer[] = new int[screenshotSize];
	    bb.asIntBuffer().get(pixelsBuffer);
	    bb = null;

	    for (int i = 0; i < screenshotSize; ++i) {
	        pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
	    }
	    
	    MapaBits textura = new MapaBits();
	    textura.setBitmap(pixelsBuffer, width, height);
	    return textura;
	}
}
