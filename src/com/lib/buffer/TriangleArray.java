package com.lib.buffer;

import com.lib.utils.ShortArray;

public class TriangleArray extends ShortArray
{
	/*
		TRIANGLE ARRAY
		a, b, c: Índice de los puntos que forman el triángulo.
	*/
	
	private static final int TRIANGLE_SIZE = 3;
	
	public TriangleArray()
	{
		super();
	}
	
	public TriangleArray(int size)
	{
		super(TRIANGLE_SIZE * size);
	}
	
	public TriangleArray(ShortArray list)
	{
		super(list);
	}
	
	public void addTriangle(short a, short b, short c)
	{
		add(a);
		add(b);
		add(c);
	}
	
	public short getAVertex(short triangle)
	{
		return get(TRIANGLE_SIZE * triangle);
	}
	
	public short getBVertex(short triangle)
	{
		return get(TRIANGLE_SIZE * triangle + 1);
	}
	
	public short getCVertex(short triangle)
	{
		return get(TRIANGLE_SIZE * triangle + 2);
	}
	
	public int getNumTriangles()
	{
		return size / TRIANGLE_SIZE;
	}
}
