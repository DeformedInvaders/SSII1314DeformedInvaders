package com.lib.buffer;

import com.lib.utils.ShortArray;

public class HullArray extends ShortArray
{
	/*
		HULL ARRAY
		a: Índice de los puntos que forman el contorno.
	*/
	
	private static final int INDEX_POINT_HULL = 0;
	
	private static final int SIZE_HULL = 1;
	
	public HullArray()
	{
		super();
	}
	
	public HullArray(int size)
	{
		super(size);
	}
	
	public HullArray(ShortArray list)
	{
		super(list);
	}
	
	public void addVertex(short a)
	{
		add(a);
	}
	
	public short getVertex(short vertex)
	{
		return get(SIZE_HULL * vertex + INDEX_POINT_HULL);
	}
	
	public int getNumVertices()
	{
		return size / SIZE_HULL;
	}
}
