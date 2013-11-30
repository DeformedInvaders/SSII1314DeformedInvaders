package com.example.paint;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import com.example.main.GLESUtils;
import com.example.utils.FloatArray;

public class Polilinea
{
	private FloatArray puntos;
	private FloatBuffer buffer;
	
	private float size, colorR, colorG, colorB;
	
	public Polilinea(float colorR, float colorG, float colorB, float size)
	{	
		this.puntos = new FloatArray();
		
		this.size = size;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
	}
	
	public Polilinea(Punto p, float colorR, float colorG, float colorB, float size)
	{	
		this.puntos = new FloatArray();
		this.anyadirPunto(p);
		
		this.size = size;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
	}
	
	public Polilinea(Collection<Punto> c, float colorR, float colorG, float colorB, float size)
	{	
		this.puntos = new FloatArray();
		
		Iterator<Punto> it = c.iterator();
		while(it.hasNext())
		{
			this.anyadirPunto(it.next());
		}
		
		this.size = size;
		this.colorR = colorR;
		this.colorG = colorG;
		this.colorB = colorB;
	}
	
	public boolean anyadirPunto(Punto p)
	{	
		this.puntos.add(p.getX());
		this.puntos.add(p.getY());
		
		this.buffer = GLESUtils.construirBuffer(puntos);
		
		return true;
	}
	
	public void dibujar(GL10 gl)
	{		
		if(puntos.size > 1)
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_STRIP, size, colorR, colorG, colorB, 1.0f, buffer);
		}
	}
}
