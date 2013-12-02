package com.example.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

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
	private int color;
	private ArrayList<FloatBuffer> buffer;
	private FloatBuffer bufferHull;
	
	/* Anterior Siguiente Buffers */
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	public PaintOpenGLRenderer()
	{
        super();
        
        this.estado = TPaintEstado.Mano;
        
        this.lista = new ArrayList<Polilinea>();
        this.linea = null;
        
        this.colorPincel = Color.RED;
        this.colorCubo = Color.BLUE;
        this.sizeLinea = 3.0f;
        
        this.anteriores = new Stack<Accion>();
        this.siguientes = new Stack<Accion>();
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{			
		super.onDrawFrame(gl);
		
		// Esqueleto
		if(puntos != null && triangulos != null)
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_TRIANGLES, 3.0f, color, buffer);
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
		
		this.buffer = GLESUtils.construirTriangulosBuffer(this.triangulos, this.puntos);
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
			float dx = width/(xRight-xLeft);
			float dy = height/(yTop-yBot);
			
			if(GeometryUtils.isPointInsideMesh(hull, x, y, xLeft, yBot, dx, dy))
			{
				if(colorCubo != color)
				{
					color = colorCubo;
					this.anteriores.push(new Accion(colorCubo));
					this.siguientes.clear();
				}
			}
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
}
