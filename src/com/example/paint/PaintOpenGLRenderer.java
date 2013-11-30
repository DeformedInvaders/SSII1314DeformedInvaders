package com.example.paint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.example.main.OpenGLRenderer;

public class PaintOpenGLRenderer extends OpenGLRenderer
{	
	private List<Polilinea> lista;
	private Polilinea linea;
	
	//TODO ShortArray triangulos;
	//TODO FloatArray puntos;
	//TODO FloatArray color;
	
	private TPaintEstado estado;
	private float colorR, colorG, colorB;
	private float size;
	
	public PaintOpenGLRenderer()
	{
        super();
        
        this.estado = TPaintEstado.Mano;
        
        this.lista = new ArrayList<Polilinea>();
        this.linea = null;
        
        this.colorR = 0.0f;
        this.colorG = 0.0f;
        this.colorB = 0.0f;
        this.size = 3.0f;
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{			
		super.onDrawFrame(gl);
		
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
	}
	
	public void seleccionarCubo()
	{
		estado = TPaintEstado.Cubo;
	}
	
	public TPaintEstado getEstado()
	{
		return estado;
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
			// TODO Buscar triangulo que contenga el punto y añadirle el color seleccionado;
		}
	}
	
	public void crearPolilinea()
	{
		this.linea = new Polilinea(colorR, colorG, colorB, size);
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
