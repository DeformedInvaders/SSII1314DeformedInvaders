package com.example.paint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.GLESUtils;
import com.example.main.OpenGLRenderer;
import com.example.math.GeometryUtils;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class PaintOpenGLRenderer extends OpenGLRenderer
{	
	/* Estructura de datos */
	private List<Polilinea> lista;
	private Polilinea linea;
	
	private TPaintEstado estado;
	
	private int colorPaleta;
	private float sizeLinea;
	
	/* Esqueleto */	
	private FloatArray hull;
	private FloatBuffer bufferHull;
	
	private ShortArray triangulos;
	private FloatArray puntos;
	private ArrayList<FloatBuffer> bufferPuntos;
	
	private int color;
	
	/* Texturas */
	private Bitmap texturaBMP;
	private boolean takeScreenShot;
	
	private FloatArray coordsTextura;
	private ArrayList<FloatBuffer> bufferTextura;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	
	/* Anterior Siguiente Buffers */
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	public PaintOpenGLRenderer(Context context)
	{
        super(context);
        
        this.estado = TPaintEstado.Mano;
        
        this.lista = new ArrayList<Polilinea>();
        this.linea = null;
        
        this.colorPaleta = Color.RED;
        this.sizeLinea = 1.0f;
        
        this.anteriores = new Stack<Accion>();
        this.siguientes = new Stack<Accion>();
        
        this.takeScreenShot = false;
        this.nombreTextura = new int[numeroTexturas];
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		if (takeScreenShot)
		{	
			// Restaurar Camara posición inicial
			float nxLeft = xLeft;
			float nxRight = xRight;
			float nyTop = yTop;
			float nyBot = yBot;
			
			restore();
			
			// Limpiar Buffer Color y Actualizar Camara
			super.onDrawFrame(gl);
			
			// Pintar Elementos a capturar
			GLESUtils.dibujarBuffer(gl, GL10.GL_TRIANGLES, 3.0f, color, bufferPuntos);
			Iterator<Polilinea> it = lista.iterator();
			while(it.hasNext())
			{
				it.next().dibujar(gl);
			}
			
			// Capturar Pantalla
		    this.texturaBMP = capturaPantalla(gl, height, width);
		    
		    // Construir Textura
			this.coordsTextura = GLESUtils.construirTextura(puntos, texturaBMP.getWidth(), texturaBMP.getHeight());
	        this.bufferTextura = GLESUtils.construirTriangulosBuffer(triangulos, coordsTextura);
			
			// Desactivar Modo Captura
			this.takeScreenShot = false;
			this.estado = TPaintEstado.Bitmap;
			
			// Recuperar Camara
			this.xLeft = nxLeft;
			this.xRight = nxRight;
			this.yTop = nyTop;
			this.yBot = nyBot;
		}
		
		super.onDrawFrame(gl);
		
		if(estado == TPaintEstado.Bitmap)
		{
			GLESUtils.cargarTextura(gl, texturaBMP, nombreTextura, 0);
			GLESUtils.dibujarBuffer(gl, bufferPuntos, bufferTextura, nombreTextura, 0);	
			GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 1.0f, Color.DKGRAY, bufferPuntos);
			GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferHull);
		}
		else
		{
			// Esqueleto
			if(puntos != null && triangulos != null)
			{
				GLESUtils.dibujarBuffer(gl, GL10.GL_TRIANGLES, 3.0f, color, bufferPuntos);
			}
			
			// Detalles
			Iterator<Polilinea> it = lista.iterator();
			while(it.hasNext())
			{
				it.next().dibujar(gl);
			}
			
			if(linea != null)
			{
				linea.dibujar(gl);
			}
			
			if(puntos != null && triangulos != null)
			{
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferHull);
			}
		}
	}

	public void seleccionarMano()
	{
		estado = TPaintEstado.Mano;
	}
	
	public void seleccionarPincel()
	{
		estado = TPaintEstado.Pincel;
	}
	
	public void seleccionarCubo()
	{
		estado = TPaintEstado.Cubo;
	}
	
	public void seleccionarColor()
	{
		colorPaleta = GLESUtils.generarColor();
	}
	public void seleccionarColor(int color)
	{
		colorPaleta = color;
	}
	
	public void seleccionarSize()
	{
		sizeLinea = (sizeLinea+5)%15;
	}
	
	public TPaintEstado getEstado()
	{
		return estado;
	}
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.hull = esqueleto.getHull();
		this.puntos = esqueleto.getMesh();
		this.triangulos = esqueleto.getTriangles();
		this.color = esqueleto.getColor();
		
		this.bufferPuntos = GLESUtils.construirTriangulosBuffer(this.triangulos, this.puntos);
		this.bufferHull = GLESUtils.construirBuffer(this.hull);
	}
	
	public void anyadirPunto(float x, float y, float width, float height)
	{	
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		if(estado == TPaintEstado.Pincel)
		{ 
			this.linea.anyadirPunto(new Punto(nx, ny));
		}
		else if(estado == TPaintEstado.Cubo)
		{			
			if(GeometryUtils.isPointInsideMesh(hull, nx, ny))
			{
				if(colorPaleta != color)
				{
					color = colorPaleta;
					this.anteriores.push(new Accion(colorPaleta));
					this.siguientes.clear();
				}
			}
		}
	}
	
	public void crearPolilinea()
	{
		this.linea = new Polilinea(colorPaleta, sizeLinea);
	}
	
	public void guardarPolilinea()
	{
		if(linea != null)
		{
			this.lista.add(linea);
			this.anteriores.push(new Accion(linea));
			this.siguientes.clear();
			this.linea = null;
		}
	}

	public void anteriorAccion()
	{
		if(!anteriores.isEmpty())
		{
			Accion accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public void siguienteAccion()
	{
		if(!siguientes.isEmpty())
		{
			Accion accion = siguientes.lastElement();
			siguientes.remove(siguientes.size()-1);
			anteriores.push(accion);
			actualizarEstado(anteriores);
		}
	}
	
	private void actualizarEstado(Stack<Accion> pila)
	{
		this.color = Color.BLACK;
		this.lista = new ArrayList<Polilinea>();
		
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
				this.lista.add(accion.getLinea());
			}
		}
	}

	public void reiniciar()
	{
		this.linea = null;
		this.lista.clear();
		this.anteriores.clear();
		this.siguientes.clear();
		this.color = Color.BLACK;
	}
	
	public void capturaPantalla()
	{
		this.takeScreenShot = true;
	}
	
	private Bitmap capturaPantalla(GL10 gl, int height, int width)
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

	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
	    return bitmap;
	}
}
