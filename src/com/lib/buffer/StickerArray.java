package com.lib.buffer;

import com.creation.data.TTipoSticker;
import com.lib.utils.FloatArray;
import com.main.model.GamePreferences;

public class StickerArray extends FloatArray
{
	public StickerArray()
	{
		super(6 * GamePreferences.NUM_TYPE_STICKERS);
		
		for (int i = 0; i < 6 * GamePreferences.NUM_TYPE_STICKERS; i++)
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
		
		set(6 * sticker, 0);
		set(6 * sticker + 1, id);
		set(6 * sticker + 2, index);
		set(6 * sticker + 3, alfa);
		set(6 * sticker + 4, beta);
		set(6 * sticker + 5, gamma);
	}
	
	public int getIdSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (int) get(6 * sticker + 1);
	}
	
	public short getIndexSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (short) get(6 * sticker + 2);
	}
	
	public float getAlfaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();

		return get(6 * sticker + 3);
	}
	
	public float getBetaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(6 * sticker + 4);
	}
	
	public float getGammaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(6 * sticker + 5);
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
	
	public void removeSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(6 * sticker, -1);
		set(6 * sticker + 1, -1);
		set(6 * sticker + 2, -1);
		set(6 * sticker + 3, -1);
		set(6 * sticker + 4, -1);
		set(6 * sticker + 5, -1);
	}
	
	public void hideStickers(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(6 * sticker, -1);
	}
	
	public void showSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(6 * sticker, 0);
	}
	
	public boolean isLoadedSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(6 * sticker) != -1 && getIdSticker(tipo) != -1;
	}
	
	public int getNumSticker()
	{
		return size / 5;
	}
}
