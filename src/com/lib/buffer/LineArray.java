package com.lib.buffer;

import com.lib.utils.ShortArray;

public class LineArray extends ShortArray
{
	public LineArray()
	{
		super();
	}
	
	public LineArray(int size)
	{
		super(2 * size);
	}
	
	public LineArray(ShortArray list)
	{
		super(list);
	}
	
	public void addLine(short a, short b)
	{
		add(a);
		add(b);
	}
	
	public short getAVertex(short line)
	{
		return get(2 * line);
	}
	
	public short getBVertex(short line)
	{
		return get(2 * line + 1);
	}
	
	public int getNumLines()
	{
		return size / 2;
	}
}
