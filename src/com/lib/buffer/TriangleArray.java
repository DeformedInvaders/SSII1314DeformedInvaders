package com.lib.buffer;

import com.lib.utils.ShortArray;

public class TriangleArray extends ShortArray
{
	private static final long serialVersionUID = 7233680263696488502L;
	
	/*
		TRIANGLE ARRAY
		a, b, c: Índice de los puntos que forman el triángulo.
	*/
	
	private static final int INDEX_A_TRIANGLE = 0;
	private static final int INDEX_B_TRIANGLE = 1;
	private static final int INDEX_C_TRIANGLE = 2;
	
	private static final int SIZE_TRIANGLE = 3;
	
	public TriangleArray()
	{
		super();
	}
	
	public TriangleArray(int size)
	{
		super(SIZE_TRIANGLE * size);
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
	
	public void setVertex(short triangle, short a, short b, short c)
	{
		set(SIZE_TRIANGLE * triangle + INDEX_A_TRIANGLE, a);
		set(SIZE_TRIANGLE * triangle + INDEX_B_TRIANGLE, b);
		set(SIZE_TRIANGLE * triangle + INDEX_C_TRIANGLE, c);
	}
	
	public short getAVertex(short triangle)
	{
		return get(SIZE_TRIANGLE * triangle + INDEX_A_TRIANGLE);
	}
	
	public short getBVertex(short triangle)
	{
		return get(SIZE_TRIANGLE * triangle + INDEX_B_TRIANGLE);
	}
	
	public short getCVertex(short triangle)
	{
		return get(SIZE_TRIANGLE * triangle + INDEX_C_TRIANGLE);
	}
	
	public int getNumTriangles()
	{
		return size / SIZE_TRIANGLE;
	}
	
	public TriangleArray clone()
	{
		return new TriangleArray(super.clone());
	}
}
