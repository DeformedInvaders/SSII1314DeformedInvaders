package com.creation.data;

import java.nio.FloatBuffer;

import com.lib.utils.FloatArray;

public class Polilinea
{
	private FloatArray puntos;
	private FloatBuffer buffer;

	private int color;
	private int size;

	/* Constructora */

	public Polilinea(int color, int size, FloatArray puntos, FloatBuffer buffer)
	{
		this.puntos = new FloatArray();

		this.size = size;
		this.color = color;

		this.puntos = puntos;
		this.buffer = buffer;
	}

	/* M�todos de Obtenci�n de Informaci�n */

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
