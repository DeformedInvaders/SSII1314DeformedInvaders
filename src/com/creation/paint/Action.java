package com.creation.paint;


public abstract class Action
{
	protected TTypeAction tipoAccion;

	/* Métodos de Obtención de Información */

	public boolean isTipoColor()
	{
		return tipoAccion == TTypeAction.Color;
	}

	public boolean isTipoPolilinea()
	{
		return tipoAccion == TTypeAction.Polyline;
	}

	public boolean isTipoPegatina()
	{
		return tipoAccion == TTypeAction.Sticker;
	}
}
