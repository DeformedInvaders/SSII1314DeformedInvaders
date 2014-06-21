package com.creation.paint;

public abstract class Action
{
	protected TTypeAction typeAction;

	/* Métodos de Obtención de Información */

	public boolean isTypeColor()
	{
		return typeAction == TTypeAction.Color;
	}

	public boolean isTypePolyline()
	{
		return typeAction == TTypeAction.Polyline;
	}

	public boolean isTypeSticker()
	{
		return typeAction == TTypeAction.Sticker;
	}
}
