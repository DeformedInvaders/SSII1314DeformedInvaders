package com.lib.buffer;

import com.creation.data.TTipoSticker;
import com.lib.math.Vector2;
import com.lib.utils.FloatArray;
import com.main.model.GamePreferences;

public class StickerArray extends FloatArray
{
	/*
		STICKER ARRAY
		id: Identificador de la pegatina.
		index: Índice del triángulo al que pertenece la pegatina.
		alfa, beta, gamma: Coordenadas baricéntricas dentro del triángulo.
		theta: Ángulo inicial de rotación de la pegatina.
		iota: Ángulo que forma el eje Y con el vector AP. A vértice del triángulo, P posición de la pegatina.
	*/
	
	private static final int SIZE_STICKER = 7;
	
	public StickerArray()
	{
		super(SIZE_STICKER * GamePreferences.NUM_TYPE_STICKERS);
		
		for (int i = 0; i < SIZE_STICKER * GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			add(-1);
		}
	}
	
	public void setSticker(TTipoSticker tipo, int id, float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		short sticker = (short) tipo.ordinal();
		
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
		
		float theta = 0.0f;
		
		Vector2 v = new Vector2(x - aX, y - aY);
		float iota = v.angle();
		
		set(SIZE_STICKER * sticker, id);
		set(SIZE_STICKER * sticker + 1, index);
		set(SIZE_STICKER * sticker + 2, alfa);
		set(SIZE_STICKER * sticker + 3, beta);
		set(SIZE_STICKER * sticker + 4, gamma);
		set(SIZE_STICKER * sticker + 5, theta);
		set(SIZE_STICKER * sticker + 6, iota);
	}
	
	public void setThetaSticker(TTipoSticker tipo, float theta)
	{
		short sticker = (short) tipo.ordinal();
		
		set(SIZE_STICKER * sticker + 5, theta);
	}
	
	public int getIdSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (int) get(SIZE_STICKER * sticker);
	}
	
	public short getIndexSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (short) get(SIZE_STICKER * sticker + 1);
	}
	
	public float getAlfaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();

		return get(SIZE_STICKER * sticker + 2);
	}
	
	public float getBetaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + 3);
	}
	
	public float getGammaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + 4);
	}
	
	public float getXCoords(TTipoSticker tipo, VertexArray vertices, TriangleArray triangulos)
	{		
		short triangle = getIndexSticker(tipo);
		float alfa = getAlfaSticker(tipo);
		float beta = getBetaSticker(tipo);
		float gamma = getGammaSticker(tipo);
		
		short a = triangulos.getAVertex(triangle);
		short b = triangulos.getBVertex(triangle);
		short c = triangulos.getCVertex(triangle);
		
		float aX = vertices.getXVertex(a);
		float bX = vertices.getXVertex(b);
		float cX = vertices.getXVertex(c);
		
		return alfa * aX + beta * bX + gamma * cX;
	}
	
	public float getYCoords(TTipoSticker tipo, VertexArray vertices, TriangleArray triangulos)
	{
		short triangle = getIndexSticker(tipo);
		float alfa = getAlfaSticker(tipo);
		float beta = getBetaSticker(tipo);
		float gamma = getGammaSticker(tipo);
		
		short a = triangulos.getAVertex(triangle);
		short b = triangulos.getBVertex(triangle);
		short c = triangulos.getCVertex(triangle);
		
		float aY = vertices.getYVertex(a);
		float bY = vertices.getYVertex(b);
		float cY = vertices.getYVertex(c);
		
		return alfa * aY + beta * bY + gamma * cY;
	}
	
	public float getThetaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + 4);
	}
	
	public float getIotaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + 5);
	}
	
	public void removeSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		for (int i = 0; i < SIZE_STICKER; i++)
		{
			set(SIZE_STICKER * sticker + i, -1.0f);
		}
	}
	
/*	public void hideStickers(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(SIZE_STICKER * sticker, -1);
	}
	
	public void showSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(SIZE_STICKER * sticker, 0);
	}
*/	
	public boolean isLoadedSticker(TTipoSticker tipo)
	{		
		return getIdSticker(tipo) != -1;
	}
	
	public int getNumSticker()
	{
		return size / SIZE_STICKER;
	}
}
