package com.creation.data;

import java.nio.FloatBuffer;

import com.creation.paint.TTypeSize;
import com.lib.buffer.VertexArray;

public class Polyline
{
	private VertexArray vertices;
	private FloatBuffer buffer;

	private int color;
	private TTypeSize size;

	/* Constructora */

	public Polyline(int color, TTypeSize size, VertexArray vertices, FloatBuffer buffer)
	{
		this.size = size;
		this.color = color;

		this.vertices = vertices;
		this.buffer = buffer;
	}

	/* Métodos de Obtención de Información */

	public VertexArray getVertices()
	{
		return vertices;
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
