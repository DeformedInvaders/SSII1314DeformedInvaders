package com.lib.opengl;

import com.lib.utils.ShortArray;

public class EdgeArray extends ShortArray
{
	public EdgeArray()
	{
		super();
	}
	
	public EdgeArray(int size)
	{
		super(4 * size);
	}
	
	public EdgeArray(ShortArray list)
	{
		super(list);
	}
	
	public void addEdge(short a, short b, short l, short r)
	{
		add(a);
		add(b);
		add(l);
		add(r);
	}
	
	public short getAVertex(int edge)
	{
		return get(4 * edge);
	}
	
	public short getBVertex(int edge)
	{
		return get(4 * edge + 1);
	}
	
	public short getLVertex(int edge)
	{
		return get(4 * edge + 2);
	}
	
	public short getRVertex(int edge)
	{
		return get(4 * edge + 3);
	}
	
	public int getNumEdges()
	{
		return size / 4;
	}
}
