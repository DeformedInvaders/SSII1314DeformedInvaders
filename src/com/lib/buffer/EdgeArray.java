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
	
	private static final int INDEX_A_EDGE = 0;
	private static final int INDEX_B_EDGE = 1;
	private static final int INDEX_L_EDGE = 2;
	private static final int INDEX_R_EDGE = 3;
	
	private static final short NULL_INDEX = -1;
	
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
				if (getLVertex(i) == NULL_INDEX)
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
			add(NULL_INDEX);
		}
		else
		{
			add(NULL_INDEX);
			add(c);
		}
		
		return false;
	}
	
	public short getAVertex(short edge)
	{
		return get(SIZE_EDGE * edge + INDEX_A_EDGE);
	}
	
	public short getBVertex(short edge)
	{
		return get(SIZE_EDGE * edge + INDEX_B_EDGE);
	}
	
	public short getLVertex(short edge)
	{
		return get(SIZE_EDGE * edge + INDEX_L_EDGE);
	}
	
	public short getRVertex(short edge)
	{
		return get(SIZE_EDGE * edge + INDEX_R_EDGE);
	}
	
	public void setLVertex(short edge, short l)
	{
		set(SIZE_EDGE * edge + INDEX_L_EDGE, l);
	}
	
	public void setRVertex(short edge, short r)
	{
		set(SIZE_EDGE * edge + INDEX_R_EDGE, r);
	}
	
	public int getNumEdges()
	{
		return size / SIZE_EDGE;
	}
}
