package com.lib.buffer;

import com.lib.math.Intersector;
import com.lib.utils.ShortArray;

public class EdgeArray extends ShortArray
{
	/*
		EDGE ARRAY
		a, b: Índice de los puntos que forman la arista.
		l, r: Índice de los puntos vecinos izquierdo y derecho de los triángulos que comparten la arista. -1 si no hay vecino.
	*/
	
	private static final int SIZE_EDGE = 4;

	public EdgeArray()
	{
		super();
	}
	
	public EdgeArray(int size)
	{
		super(SIZE_EDGE * size);
	}
	
	public EdgeArray(ShortArray list)
	{
		super(list);
	}
	
	
	public boolean addEdge(short a, short b, short c, VertexArray vertices)
	{
		for (short i = 0; i < getNumEdges(); i++)
		{
			if ((getAVertex(i) == a && getAVertex(i) == b) || (getAVertex(i) == b && getBVertex(i) == a))
			{
				if (getLVertex(i) == -1)
				{
					setLVertex(i, c);
				}
				else
				{
					setRVertex(i, c);
				}
				
				return true;
			}
		}
				
		
		add(a);
		add(b);
		
		int lado = Intersector.pointLineSide(vertices.getXVertex(a), vertices.getYVertex(a), vertices.getXVertex(b), vertices.getYVertex(b), vertices.getXVertex(c), vertices.getYVertex(c));

		if (lado == -1)
		{
			add(c);
			add(-1);
		}
		else
		{
			add(-1);
			add(c);
		}
		
		return false;
	}
	
	public short getAVertex(short edge)
	{
		return get(SIZE_EDGE * edge);
	}
	
	public short getBVertex(short edge)
	{
		return get(SIZE_EDGE * edge + 1);
	}
	
	public short getLVertex(short edge)
	{
		return get(SIZE_EDGE * edge + 2);
	}
	
	public short getRVertex(short edge)
	{
		return get(SIZE_EDGE * edge + 3);
	}
	
	public void setLVertex(short edge, short l)
	{
		set(SIZE_EDGE * edge + 2, l);
	}
	
	public void setRVertex(short edge, short r)
	{
		set(SIZE_EDGE * edge + 3, r);
	}
	
	public int getNumEdges()
	{
		return size / SIZE_EDGE;
	}
}
