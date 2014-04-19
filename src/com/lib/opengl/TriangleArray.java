package com.lib.opengl;

import com.lib.utils.ShortArray;

public class TriangleArray extends ShortArray
{
	public TriangleArray()
	{
		super();
	}
	
	public TriangleArray(int size)
	{
		super(3 * size);
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
	
	public short getAVertex(int triangle)
	{
		return get(3 * triangle);
	}
	
	public short getBVertex(int triangle)
	{
		return get(3 * triangle + 1);
	}
	
	public short getCVertex(int triangle)
	{
		return get(3 * triangle + 2);
	}
	
	public int getNumTriangles()
	{
		return size / 3;
	}
}
