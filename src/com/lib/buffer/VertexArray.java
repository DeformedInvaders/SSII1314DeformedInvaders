package com.lib.buffer;

import com.lib.utils.FloatArray;

public class VertexArray extends FloatArray
{
	/*
		VERTEX ARRAY
		x, y: Coordenadas de los puntos.
	*/
	
	private static final int COORD_X_VERTEX = 0;
	private static final int COORD_Y_VERTEX = 1;
	
	private static final int SIZE_VERTEX = 2;

	public VertexArray()
	{
		super();
	}
	
	public VertexArray(int size)
	{
		super(SIZE_VERTEX * size);
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
		set(SIZE_VERTEX * vertex + COORD_X_VERTEX, x);
		set(SIZE_VERTEX * vertex + COORD_Y_VERTEX, y);
	}
	
	public float getXVertex(short vertex)
	{
		return get(SIZE_VERTEX * vertex + COORD_X_VERTEX);
	}
	
	public float getYVertex(short vertex)
	{
		return get(SIZE_VERTEX * vertex + COORD_Y_VERTEX);
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
		removeIndex(SIZE_VERTEX * vertex + COORD_Y_VERTEX);
		removeIndex(SIZE_VERTEX * vertex + COORD_X_VERTEX);
	}
	
	public int getNumVertices()
	{
		return size / SIZE_VERTEX;
	}
	
	public VertexArray clone()
	{
		return new VertexArray(super.clone());
	}
}
