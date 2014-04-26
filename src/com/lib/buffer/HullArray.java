package com.lib.buffer;

import com.lib.utils.ShortArray;

public class HullArray extends ShortArray
{
	/*
		HULL ARRAY
		a: Índice de los puntos que forman el contorno.
	*/
	
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
		return get(vertex);
	}
	
	public int getNumVertex()
	{
		return size;
	}
}
