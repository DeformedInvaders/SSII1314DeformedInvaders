package com.project.data;

import java.nio.FloatBuffer;

import com.lib.utils.FloatArray;

public class Polilinea
{
	private FloatArray puntos;
	private FloatBuffer buffer;
	
	private int color;
	private int size;
	
	/* SECTION Constructora */
	
	public Polilinea(int color, int size, FloatArray puntos, FloatBuffer buffer)
	{	
		this.puntos = new FloatArray();
		
		this.size = size;
		this.color = color;
		
		this.puntos = puntos;
		this.buffer = buffer;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public FloatArray getPuntos()
	{
		return puntos;
	}

	public FloatBuffer getBuffer()
	{
		return buffer;
	}

	public int getColor()
	{
		return color;
	}

	public int getSize()
	{
		return size;
	}	
}
