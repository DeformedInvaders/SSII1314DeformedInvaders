package com.creation.data;

import java.nio.FloatBuffer;

import com.lib.opengl.VertexArray;

public class Polilinea
{
	private VertexArray puntos;
	private FloatBuffer buffer;

	private int color;
	private int size;

	/* Constructora */

	public Polilinea(int color, int size, VertexArray puntos, FloatBuffer buffer)
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

	public int getSize()
	{
		return size;
	}
}
