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
	private int colorPincel, colorCubo;
	private float sizeLinea;
	
	/* Esqueleto */	
	private FloatArray hull;
	private ShortArray triangulos;
	private FloatArray puntos;
	private ArrayList<FloatBuffer> bufferPuntos;
	private int color;
	private FloatBuffer bufferHull;
	
	/* Texturas */
	private Bitmap textura;
	private boolean takeScreenShot = false;
	
	private float[] coordsTextura = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f};
	private FloatBuffer bufferTextura;
	
	private float[] coordsVertices = new float[8];
	private FloatBuffer bufferVertices;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura = new int[numeroTexturas];
	
	/* Anterior Siguiente Buffers */
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	public PaintOpenGLRenderer(Context context)
	{
        super(context);
        
        this.estado = TPaintEstado.Mano;
        
        this.lista = new ArrayList<Polilinea>();
        this.linea = null;
        
        this.colorPincel = Color.RED;
        this.colorCubo = Color.BLUE;
        this.sizeLinea = 3.0f;
        
        this.anteriores = new Stack<Accion>();
        this.siguientes = new Stack<Accion>();
        
        /* TEST */
        this.bufferTextura = GLESUtils.construirBuffer(coordsTextura);
        this.bufferVertices = GLESUtils.construirBuffer(coordsVertices);
        /* TEST */
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{			
		super.onDrawFrame(gl);
		
		if(estado == TPaintEstado.Bitmap)
		{
			/* TEST */
			GLESUtils.cargarTextura(gl, textura, nombreTextura, 0);
			GLESUtils.dibujarBuffer(gl, bufferVertices, bufferTextura, nombreTextura, 0);			
			/* TEST */
		}
		else
		{
			// Esqueleto
			if(puntos != null && triangulos != null)
			{
				GLESUtils.dibujarBuffer(gl, GL10.GL_TRIANGLES, 3.0f, color, bufferPuntos);
				GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferHull);
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
		}
		
		if (takeScreenShot)
		{			
		    int screenshotSize = width * height;
		    ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
		    bb.order(ByteOrder.nativeOrder());
		    gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
		    int pixelsBuffer[] = new int[screenshotSize];
		    bb.asIntBuffer().get(pixelsBuffer);
		    bb = null;

		    for (int i = 0; i < screenshotSize; ++i) {
		        // The alpha and green channels' positions are preserved while the red and blue are swapped
		        pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
		    }

		    textura = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    textura.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
		    
		    takeScreenShot = false;
		    this.estado = TPaintEstado.Bitmap;
		}
	}

	public void seleccionarMano()
	{
		estado = TPaintEstado.Mano;
	}
	
	public void seleccionarPincel()
	{
		estado = TPaintEstado.Pincel;
		colorPincel = GLESUtils.generarColor();
	}
	
	public void seleccionarCubo()
	{
		estado = TPaintEstado.Cubo;
		colorCubo = GLESUtils.generarColor();
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
				if(colorCubo != color)
				{
					color = colorCubo;
					this.anteriores.push(new Accion(colorCubo));
					this.siguientes.clear();
				}
			}
		}
		else if(estado == TPaintEstado.Bitmap)
		{
			coordsVertices[0] = nx; //bottom left
			coordsVertices[1] = ny;
			coordsVertices[2] = nx; // top left
			coordsVertices[3] = ny + this.height/10;
			coordsVertices[4] = nx + this.width/10; //bottom right
			coordsVertices[5] = ny;
			coordsVertices[6] = nx + this.width/10; // top right
			coordsVertices[7] = ny + this.height/10;
			
			bufferVertices = GLESUtils.construirBuffer(coordsVertices);
		}
	}
	
	public void crearPolilinea()
	{
		this.linea = new Polilinea(colorPincel, sizeLinea);
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
	
	public void testBitMap()
	{
		this.takeScreenShot = true;
	}
}
