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

import com.example.data.Esqueleto;
import com.example.data.TexturaBMP;
import com.example.main.OpenGLRenderer;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

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
	
	private TexturaBMP textura;
	private FloatArray coordsTextura;
	
	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	/* Constructora*/	
	
	public PaintOpenGLRenderer(Context context)
	{
        super(context);
        
        this.estado = TPaintEstado.Mano;
        
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
			// Restaurar Camara posición inicial
			float nxLeft = xLeft;
			float nxRight = xRight;
			float nyTop = yTop;
			float nyBot = yBot;
			
			restore();
			
			// Limpiar Buffer Color y Actualizar Camara
			super.onDrawFrame(gl);
			
			// Pintar Esqueleto
			dibujarBuffer(gl, GL10.GL_TRIANGLES, SIZELINE, color, bufferVertices);
			
			// Pintar Polilineas
			Iterator<Polilinea> it = listaLineas.iterator();
			while(it.hasNext())
			{
				Polilinea polilinea = it.next();
				dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize(), polilinea.getColor(), polilinea.getBuffer());
			}
			
			// Capturar Pantalla
		    this.textura = capturaPantalla(gl, canvasHeight, canvasWidth);
		    
		    // Construir Textura
			this.coordsTextura = construirTextura(vertices, textura.getWidth(), textura.getHeight());
			
			// Desactivar Modo Captura
			this.modoCaptura = false;
			this.capturaTerminada = true;
			
			// Recuperar Camara
			this.xLeft = nxLeft;
			this.xRight = nxRight;
			this.yTop = nyTop;
			this.yBot = nyBot;
		}
		
		super.onDrawFrame(gl);
		
		// Esqueleto
		if(vertices != null && triangulos != null)
		{
			dibujarBuffer(gl, GL10.GL_TRIANGLES, SIZELINE, color, bufferVertices);
		}
		
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
		
		// Contorno
		if(vertices != null && triangulos != null)
		{
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
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
	
	/*public void seleccionarColor()
	{
		colorPaleta = generarColorAleatorio();
	}*/
	
	public void seleccionarColor(int color)
	{
		colorPaleta = color;
		estado = TPaintEstado.Pincel;
	}
	
	public int getColorPaleta() {
		return colorPaleta;
	}

	public void setColorPaleta(int colorPaleta) {
		this.colorPaleta = colorPaleta;
		estado = TPaintEstado.Pincel;
	}
	
	/*public void seleccionarSize()
	{
		sizeLinea = (sizeLinea+5)%15;
	}*/
	
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
		estado = TPaintEstado.Pincel;
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		this.lineaActual = null;
		this.listaLineas.clear();
		
		this.anteriores.clear();
		this.siguientes.clear();
		
		this.color = Color.WHITE;
		this.sizeLinea = 6;
	}
	
	public void onTouchDown(float x, float y, float width, float height)
	{	
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		if(estado == TPaintEstado.Pincel)
		{
			crearPolilinea();
			
			boolean anyadir = true;
			
			if(lineaActual.size > 0)
			{
				float lastX = lineaActual.get(lineaActual.size-2);
				float lastY = lineaActual.get(lineaActual.size-1);
				
				anyadir = Math.abs(Intersector.distancePoints(nx, ny, lastX, lastY)) > EPSILON;
			}
			
			if(anyadir)
			{
				lineaActual.add(nx);
				lineaActual.add(ny);
				
				bufferLineaActual = construirBufferListaPuntos(lineaActual);
			}
		}
		else if(estado == TPaintEstado.Cubo)
		{			
			if(GeometryUtils.isPointInsideMesh(contorno, vertices, nx, ny))
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
	
	public void onTouchMove(float x, float y, float width, float height)
	{
		if(estado == TPaintEstado.Pincel)
		{
			onTouchDown(x, y, width, height);
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height)
	{
		if(estado == TPaintEstado.Pincel)
		{
			guardarPolilinea();
		}		
	}
	
	/* Métodos de modificación de Linea Actual */
	
	private void crearPolilinea()
	{
		if(lineaActual == null)
		{
			lineaActual = new FloatArray();
		}
	}
	
	private void guardarPolilinea()
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

	public void anteriorAccion()
	{
		guardarPolilinea();
		
		if(!anteriores.isEmpty())
		{
			Accion accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public void siguienteAccion()
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
	
	private void actualizarEstado(Stack<Accion> pila)
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
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		this.triangulos = esqueleto.getTriangulos();
		
		this.bufferVertices = construirBufferListaTriangulosRellenos(triangulos, vertices);
		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
	}
	
	public Esqueleto getEsqueleto()
	{
		while(!capturaTerminada);
		
		Esqueleto esqueleto = new Esqueleto(contorno, vertices, triangulos);
		esqueleto.setTexture(textura, coordsTextura);
		return esqueleto;
	}
	
	private TexturaBMP capturaPantalla(GL10 gl, int height, int width)
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
	    
	    TexturaBMP textura = new TexturaBMP();
	    textura.setBitmap(pixelsBuffer, width, height);
	    return textura;
	}
}
