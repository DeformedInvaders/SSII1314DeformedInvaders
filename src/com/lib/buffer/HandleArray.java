package com.lib.buffer;

import com.lib.utils.FloatArray;

public class HandleArray extends FloatArray
{
	/*
		HANDLE ARRAY
		x, y: Coordenadas de la posición del handle.
		index: Índice del triángulo al que pertenece el handle.
		alfa, beta, gamma: Coordenadas baricéntricas dentro del triángulo.
		selected: Handle Seleccionado.
	*/
	
	private static final int COORD_X_HANDLE = 0;
	private static final int COORD_Y_HANDLE = 1;
	private static final int INDEX_TRIANGLE_HANDLE = 2;
	private static final int ANGLE_ALFA_HANDLE = 3;
	private static final int ANGLE_BETA_HANDLE = 4;
	private static final int ANGLE_GAMMA_HANDLE = 5;
	private static final int SELECTED_HANDLE = 6;
	
	private static final float TRUE = 1.0f;
	private static final float FALSE = 0.0f;
	
	private static final int SIZE_HANDLE = 7;

	public HandleArray()
	{
		super();
	}
	
	public HandleArray(int size)
	{
		super(SIZE_HANDLE * size);
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
		add(FALSE);
	}
	
	public void setCoordsHandle(short handle, float x, float y)
	{
		set(SIZE_HANDLE * handle + COORD_X_HANDLE, x);
		set(SIZE_HANDLE * handle + COORD_Y_HANDLE, y);
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
		set(SIZE_HANDLE * handle + INDEX_TRIANGLE_HANDLE, index);
	}
	
	public void setCoordsVertex(short handle, float alfa, float beta, float gamma)
	{
		set(SIZE_HANDLE * handle + ANGLE_ALFA_HANDLE, alfa);
		set(SIZE_HANDLE * handle + ANGLE_BETA_HANDLE, beta);
		set(SIZE_HANDLE * handle + ANGLE_GAMMA_HANDLE, gamma);
	}
	
	public void setSelectedHandle(short handle, boolean selected)
	{
		if (selected)
		{
			set(SIZE_HANDLE * handle + SELECTED_HANDLE, TRUE);
		}
		else
		{
			set(SIZE_HANDLE * handle + SELECTED_HANDLE, FALSE);
		}
	}
	
	public float getXCoordHandle(short handle)
	{		
		return get(SIZE_HANDLE * handle + COORD_X_HANDLE);
	}
	
	public float getYCoordHandle(short handle)
	{
		return get(SIZE_HANDLE * handle + COORD_Y_HANDLE);
	}
	
	public short getIndexHandle(short handle)
	{
		return (short) get(SIZE_HANDLE * handle + INDEX_TRIANGLE_HANDLE);
	}
	
	public float getAlfaHandle(short handle)
	{
		return get(SIZE_HANDLE * handle + ANGLE_ALFA_HANDLE);
	}
	
	public float getBetaHandle(short handle)
	{
		return get(SIZE_HANDLE * handle + ANGLE_BETA_HANDLE);
	}
	
	public float getGammaHandle(short handle)
	{
		return get(SIZE_HANDLE * handle + ANGLE_GAMMA_HANDLE);
	}
	
	public boolean isSelectedHandle(short handle)
	{
		return get(SIZE_HANDLE * handle + SELECTED_HANDLE) == TRUE;
	}
	
	public void removeHandle(int handle)
	{
		removeIndex(SIZE_HANDLE * handle + SELECTED_HANDLE);
		removeIndex(SIZE_HANDLE * handle + ANGLE_GAMMA_HANDLE);
		removeIndex(SIZE_HANDLE * handle + ANGLE_BETA_HANDLE);
		removeIndex(SIZE_HANDLE * handle + ANGLE_ALFA_HANDLE);
		removeIndex(SIZE_HANDLE * handle + INDEX_TRIANGLE_HANDLE);
		removeIndex(SIZE_HANDLE * handle + COORD_Y_HANDLE);
		removeIndex(SIZE_HANDLE * handle + COORD_X_HANDLE);
	}
	
	public int getNumHandles()
	{
		return size / SIZE_HANDLE;
	}
	
	public HandleArray clone()
	{
		return new HandleArray(super.clone());
	}
	
	public HandleArray interpolar(HandleArray handles, int alfa)
	{
		HandleArray handleInterpolado = clone();
		
		for (short i = 0; i < getNumHandles(); i++)
		{
			float x = handles.getXCoordHandle(i) * alfa + getXCoordHandle(i) * (1 - alfa);
			float y = handles.getYCoordHandle(i) * alfa + getYCoordHandle(i) * (1 - alfa);
			
			handleInterpolado.setCoordsHandle(i, x, y);
		}
		
		return handleInterpolado;
	}
}
