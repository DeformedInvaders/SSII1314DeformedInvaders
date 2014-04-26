package com.lib.buffer;

import com.lib.utils.FloatArray;

public class VertexArray extends FloatArray
{
	/*
		VERTEX ARRAY
		x, y: Coordenadas de los puntos.
	*/
	
	private static final int VERTEX_SIZE = 2;

	public VertexArray()
	{
		super();
	}
	
	public VertexArray(int size)
	{
		super(VERTEX_SIZE * size);
	}
	
	public VertexArray(FloatArray list)
	{
		super(list);
	}
	
	public void addVertex(float x, float y)
	{
		add(x);
		add(y);
	}
	
	public void setVertex(short vertex, float x, float y)
	{
		set(VERTEX_SIZE * vertex, x);
		set(VERTEX_SIZE * vertex + 1, y);
	}
	
	public float getXVertex(short vertex)
	{
		return get(VERTEX_SIZE * vertex);
	}
	
	public float getYVertex(short vertex)
	{
		return get(VERTEX_SIZE * vertex + 1);
	}
	
	public float getLastXVertex()
	{
		return get(size - 2);
	}
	
	public float getLastYVertex()
	{
		return get(size - 1);
	}
	
	public void removeVertex(int vertex)
	{
		removeIndex(VERTEX_SIZE * vertex + 1);
		removeIndex(VERTEX_SIZE * vertex);
	}
	
	public int getNumVertices()
	{
		return size / VERTEX_SIZE;
	}
	
	public VertexArray clone()
	{
		return new VertexArray(super.clone());
	}
}
