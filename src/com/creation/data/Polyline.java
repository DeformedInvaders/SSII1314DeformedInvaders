package com.creation.data;

import java.nio.FloatBuffer;

import com.creation.paint.TTypeSize;
import com.lib.buffer.VertexArray;

public class Polyline
{
	private VertexArray puntos;
	private FloatBuffer buffer;

	private int color;
	private TTypeSize size;

	/* Constructora */

	public Polyline(int color, TTypeSize size, VertexArray puntos, FloatBuffer buffer)
	{
		this.puntos = new VertexArray();

		this.size = size;
		this.color = color;

		this.puntos = puntos;
		this.buffer = buffer;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public VertexArray getPuntos()
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

	public TTypeSize getSize()
	{
		return size;
	}
}
