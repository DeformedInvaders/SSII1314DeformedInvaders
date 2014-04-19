package com.lib.buffer;

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
	
	public short getAVertex(short triangle)
	{
		return get(3 * triangle);
	}
	
	public short getBVertex(short triangle)
	{
		return get(3 * triangle + 1);
	}
	
	public short getCVertex(short triangle)
	{
		return get(3 * triangle + 2);
	}
	
	public int getNumTriangles()
	{
		return size / 3;
	}
}
