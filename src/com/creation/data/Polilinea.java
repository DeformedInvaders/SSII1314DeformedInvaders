package com.creation.data;

import java.nio.FloatBuffer;

import com.creation.paint.TTipoSize;
import com.lib.buffer.VertexArray;

public class Polilinea
{
	private VertexArray puntos;
	private FloatBuffer buffer;

	private int color;
	private TTipoSize size;

	/* Constructora */

	public Polilinea(int color, TTipoSize size, VertexArray puntos, FloatBuffer buffer)
	{
		this.puntos = new VertexArray();

		this.size = size;
		this.color = color;

		this.puntos = puntos;
		this.buffer = buffer;
	}

	/* Métodos de Obtención de Información */

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

	public TTipoSize getSize()
	{
		return size;
	}
}
