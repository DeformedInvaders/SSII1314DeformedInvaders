package com.creation.paint;

import com.creation.data.TTipoAccion;

public abstract class Accion
{
	protected TTipoAccion tipoAccion;

	/* Métodos de Obtención de Información */

	public boolean isTipoColor()
	{
		return tipoAccion == TTipoAccion.Color;
	}

	public boolean isTipoPolilinea()
	{
		return tipoAccion == TTipoAccion.Polilinea;
	}

	public boolean isTipoPegatina()
	{
		return tipoAccion == TTipoAccion.Pegatina;
	}
}
