package com.lib.buffer;

import com.lib.utils.FloatArray;

public class HandleArray extends FloatArray
{
	public HandleArray()
	{
		super();
	}
	
	public HandleArray(int size)
	{
		super(7 * size);
	}
	
	public HandleArray(FloatArray list)
	{
		super(list);
	}
	
	public void addHandle(float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		short a = triangulos.getAVertex(index);
		short b = triangulos.getBVertex(index);
		short c = triangulos.getCVertex(index);
		
		float aX = vertices.getXVertex(a);
		float aY = vertices.getYVertex(a);
		float bX = vertices.getXVertex(b);
		float bY = vertices.getYVertex(b);
		float cX = vertices.getXVertex(c);
		float cY = vertices.getYVertex(c);
		
		float alfa = ((bY - cY) * (x - cX) + (cX - bX) * (y - cY))/((bY - cY) * (aX - cX) + (cX - bX) * (aY - cY));
		float beta = ((cY - aY) * (x - cX) + (aX - cX) * (y - cY))/((bY - cY) * (aX - cX) + (cX - bX) * (aY - cY));
		float gamma = 1 - alfa - beta;
		
		add(x);
		add(y);
		add(index);
		add(alfa);
		add(beta);
		add(gamma);
		add(0.0f);
	}
	
	public void setCoordsHandle(short handle, float x, float y)
	{
		set(7 * handle, x);
		set(7 * handle + 1, y);
	}
	
	public void setCoordsHandle(short handle, VertexArray vertices, TriangleArray triangulos)
	{
		short triangle = getIndexHandle(handle);
		float alfa = getAlfaHandle(handle);
		float beta = getBetaHandle(handle);
		float gamma = getGammaHandle(handle);
		
		short a = triangulos.getAVertex(triangle);
		short b = triangulos.getBVertex(triangle);
		short c = triangulos.getCVertex(triangle);
		
		float aX = vertices.getXVertex(a);
		float aY = vertices.getYVertex(a);
		float bX = vertices.getXVertex(b);
		float bY = vertices.getYVertex(b);
		float cX = vertices.getXVertex(c);
		float cY = vertices.getYVertex(c);
		
		float x = alfa * aX + beta * bX + gamma * cX;
		float y = alfa * aY + beta * bY + gamma * cY;
		
		setCoordsHandle(handle, x, y);
	}
	
	public void setIndexHandle(short handle, short index)
	{
		set(7 * handle + 2, index);
	}
	
	public void setCoordsVertex(short handle, float alfa, float beta, float gamma)
	{
		set(7 * handle + 3, alfa);
		set(7 * handle + 4, beta);
		set(7 * handle + 5, gamma);
	}
	
	public void setSelectedHandle(short handle, boolean selected)
	{
		if (selected)
		{
			set(7 * handle + 6, 1.0f);
		}
		else
		{
			set(7 * handle + 6, 0.0f);
		}
	}
	
	public float getXCoordHandle(short handle)
	{		
		return get(7 * handle);
	}
	
	public float getYCoordHandle(short handle)
	{
		return get(7 * handle + 1);
	}
	
	public short getIndexHandle(short handle)
	{
		return (short) get(7 * handle + 2);
	}
	
	public float getAlfaHandle(short handle)
	{
		return get(7 * handle + 3);
	}
	
	public float getBetaHandle(short handle)
	{
		return get(7 * handle + 4);
	}
	
	public float getGammaHandle(short handle)
	{
		return get(7 * handle + 5);
	}
	
	public boolean isSelectedHandle(short handle)
	{
		return get(7 * handle + 6) == 1.0f;
	}
	
	public void removeHandle(int handle)
	{
		removeIndex(7 * handle + 6);
		removeIndex(7 * handle + 5);
		removeIndex(7 * handle + 4);
		removeIndex(7 * handle + 3);
		removeIndex(7 * handle + 2);
		removeIndex(7 * handle + 1);
		removeIndex(7 * handle);
	}
	
	public int getNumHandles()
	{
		return size / 7;
	}
	
	public HandleArray clone()
	{
		return new HandleArray(super.clone());
	}
}
