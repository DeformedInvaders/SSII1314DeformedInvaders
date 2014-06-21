package com.creation.paint;

import com.creation.data.TTypeSticker;

public class ActionSticker extends Action
{
	private TTypeSticker stickerType;
	private int stickerId;
	private float stickerCoordX, stickerCoordY;
	private short stickerIndex;
	private float stickerFactor, stickerRotation;
	
	public ActionSticker(TTypeSticker tipo)
	{
		typeAction = TTypeAction.Sticker;
		
		stickerType = tipo;
		stickerId = -1;
	}
	
	public ActionSticker(TTypeSticker sticker, int id, float x, float y, short index, float factor, float angle)
	{
		typeAction = TTypeAction.Sticker;
		
		stickerType = sticker;
		stickerId = id;
		stickerCoordX = x;
		stickerCoordY = y;
		stickerIndex = index;
		stickerFactor = factor;
		stickerRotation = angle;
	}
	
	/* Métodos de Obtención de Información */

	public TTypeSticker getStickerType()
	{
		return stickerType;
	}

	public int getStickerId()
	{
		return stickerId;
	}

	public short getStickerIndex()
	{
		return stickerIndex;
	}

	public float getStickerCoordX()
	{
		return stickerCoordX;
	}

	public float getStickerCoordY()
	{
		return stickerCoordY;
	}

	public float getStickerFactor()
	{
		return stickerFactor;
	}

	public float getStickerRotation()
	{
		return stickerRotation;
	}	
}