package com.creation.data;

import java.io.Serializable;
import java.util.List;

import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class Movimientos implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<VertexArray>[] movimientos;

	/* Constructora */

	public Movimientos()
	{
		movimientos = new List[GamePreferences.NUM_TYPE_MOVIMIENTOS];
	}

	/* M�todos de Modificaci�n de Informaci�n */

	public void set(List<VertexArray> movimiento, TTipoMovimiento tipo)
	{
		movimientos[tipo.ordinal()] = movimiento;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public List<VertexArray> get(TTipoMovimiento tipo)
	{
		return movimientos[tipo.ordinal()];
	}

	public boolean isReady()
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_MOVIMIENTOS; i++)
		{
			if (movimientos[i] == null)
			{
				return false;
			}
		}
		
		return true;
	}
}
