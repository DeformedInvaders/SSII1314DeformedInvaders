package com.example.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.example.main.GLESUtils;
import com.example.main.OpenGLRenderer;
import com.example.math.GeometryUtils;
import com.example.math.Vector2;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class PaintOpenGLRenderer extends OpenGLRenderer
{	
	private List<Polilinea> lista;
	private Polilinea linea;
	
	private ShortArray triangulos;
	private FloatArray puntos;
	private int color;
	private ArrayList<FloatBuffer> buffer;
	
	private TPaintEstado estado;
	private int colorPincel;
	private int colorCubo;
	private float sizeLinea;
	
	public PaintOpenGLRenderer()
	{
        super();
        
        this.estado = TPaintEstado.Mano;
        
        this.lista = new ArrayList<Polilinea>();
        this.linea = null;
        
        this.colorPincel = Color.RED;
        this.colorCubo = Color.BLUE;
        this.color = Color.BLACK;
        this.sizeLinea = 3.0f;
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{			
		super.onDrawFrame(gl);
		
		// Esqueleto
		if(puntos != null && triangulos != null)
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_TRIANGLES, 3.0f, color, buffer);
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
	
	public void setEsqueleto(FloatArray puntos, ShortArray triangulos)
	{
		this.puntos = puntos;
		this.triangulos = triangulos;
		
		this.buffer = GLESUtils.construirTriangulosBuffer(this.triangulos, this.puntos);
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
			
			Vector2 v = GeometryUtils.isPointInMesh(puntos, x, y, xLeft, yBot, dx, dy);
			
			if(v != null)
			{
				color = colorCubo;
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
			this.linea = null;
		}
	}
}
