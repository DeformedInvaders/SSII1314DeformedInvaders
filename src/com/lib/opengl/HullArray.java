package com.lib.opengl;

import com.lib.utils.ShortArray;

public class HullArray extends ShortArray
{
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
	
	public short getVertex(int vertex)
	{
		return get(vertex);
	}
	
	public int getNumVertex()
	{
		return size;
	}
}