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
		iota: Ángulo que forma el eje Y con el vector AP. A vértice del triángulo, P posición de la pegatina.
		kappa: Ángulo iota actual.
		theta: Ángulo de rotación de la pegatina.
		factor: Factor de escalado de la pegatina.
	*/
	
	private static final int ID_STICKER = 0;
	private static final int INDEX_TRIANGLE_STICKER = 1;
	private static final int ANGLE_ALFA_STICKER = 2;
	private static final int ANGLE_BETA_STICKER = 3;
	private static final int ANGLE_GAMMA_STICKER = 4;
	private static final int ANGLE_IOTA_STICKER = 5;
	private static final int ANGLE_THETA_STICKER = 6;
	private static final int FACTOR_SCALE_STICKER = 7;
	
	private static final float NULL_ROTATE = 0.0f;
	private static final float NULL_SCALE = 1.0f;
	private static final short NULL_INDEX = -1;
	
	private static final int SIZE_STICKER = 8;
	
	public StickerArray()
	{
		super(SIZE_STICKER * GamePreferences.NUM_TYPE_STICKERS);
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			add(NULL_INDEX);
			add(NULL_INDEX);
			add(NULL_INDEX);
			add(NULL_INDEX);
			add(NULL_INDEX);
			add(NULL_ROTATE);
			add(NULL_ROTATE);
			add(NULL_SCALE);
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
		
		Vector2 v = new Vector2(x - aX, y - aY);
		float iota = v.angle() - 90;
		
		set(SIZE_STICKER * sticker + ID_STICKER, id);
		set(SIZE_STICKER * sticker + INDEX_TRIANGLE_STICKER, index);
		set(SIZE_STICKER * sticker + ANGLE_ALFA_STICKER, alfa);
		set(SIZE_STICKER * sticker + ANGLE_BETA_STICKER, beta);
		set(SIZE_STICKER * sticker + ANGLE_GAMMA_STICKER, gamma);
		set(SIZE_STICKER * sticker + ANGLE_IOTA_STICKER, -iota);
		set(SIZE_STICKER * sticker + ANGLE_THETA_STICKER, NULL_ROTATE);
		set(SIZE_STICKER * sticker + FACTOR_SCALE_STICKER, NULL_SCALE);
	}
	
	public void setCoords(TTipoSticker tipo, float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		int id = getIdSticker(tipo);
		setSticker(tipo, id, x, y, index, vertices, triangulos);
	}
	
	public void setThetaSticker(TTipoSticker tipo, float ang)
	{
		short sticker = (short) tipo.ordinal();
		float oldAng = getThetaSticker(tipo);
		
		set(SIZE_STICKER * sticker + ANGLE_THETA_STICKER, (float) (oldAng + ang));
	}
	
	public void setFactorSticker(TTipoSticker tipo, float factor)
	{
		short sticker = (short) tipo.ordinal();
		float oldFactor = getFactorSticker(tipo);
		
		set(SIZE_STICKER * sticker + FACTOR_SCALE_STICKER, oldFactor * factor);
	}
	
	public int getIdSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (int) get(SIZE_STICKER * sticker + ID_STICKER);
	}
	
	public short getIndexSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return (short) get(SIZE_STICKER * sticker + INDEX_TRIANGLE_STICKER);
	}
	
	public float getAlfaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();

		return get(SIZE_STICKER * sticker + ANGLE_ALFA_STICKER);
	}
	
	public float getBetaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + ANGLE_BETA_STICKER);
	}
	
	public float getGammaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + ANGLE_GAMMA_STICKER);
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
	
	public float getIotaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + ANGLE_IOTA_STICKER);
	}
	
	public float getKappaSticker(TTipoSticker tipo, VertexArray vertices, TriangleArray triangulos)
	{
		float x = getXCoords(tipo, vertices, triangulos);
		float y = getYCoords(tipo, vertices, triangulos);
		short index = getIndexSticker(tipo);
		
		short a = triangulos.getAVertex(index);
		
		float aX = vertices.getXVertex(a);
		float aY = vertices.getYVertex(a);
		
		Vector2 v = new Vector2(x - aX, y - aY);
		return v.angle() - 90;
	}
	
	public float getThetaSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + ANGLE_THETA_STICKER);
	}
	
	public float getFactorSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		return get(SIZE_STICKER * sticker + FACTOR_SCALE_STICKER);
	}
	
	public void removeSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		for (int i = 0; i < SIZE_STICKER; i++)
		{
			set(SIZE_STICKER * sticker + i, NULL_INDEX);
		}
		
		restoreSticker(tipo);
	}
	
	public void restoreSticker(TTipoSticker tipo)
	{
		short sticker = (short) tipo.ordinal();
		
		set(SIZE_STICKER * sticker + ANGLE_THETA_STICKER, NULL_ROTATE);
		set(SIZE_STICKER * sticker + FACTOR_SCALE_STICKER, NULL_SCALE);
	}
	
	public boolean isLoadedSticker(TTipoSticker tipo)
	{		
		return getIdSticker(tipo) != NULL_INDEX;
	}
	
	public int getNumSticker()
	{
		return size / SIZE_STICKER;
	}
}
