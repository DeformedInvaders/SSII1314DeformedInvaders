package com.creation.paint;


public abstract class Action
{
	protected TTypeAction tipoAccion;

	/* M�todos de Obtenci�n de Informaci�n */

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
