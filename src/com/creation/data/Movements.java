package com.creation.data;

import java.io.Serializable;
import java.util.List;

import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class Movements implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<VertexArray>[] movementsList;

	/* Constructora */

	public Movements()
	{
		movementsList = new List[GamePreferences.NUM_TYPE_MOVEMENTS];
	}

	/* M�todos de Modificaci�n de Informaci�n */

	public void set(List<VertexArray> movimiento, TTypeMovement tipo)
	{
		movementsList[tipo.ordinal()] = movimiento;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public List<VertexArray> get(TTypeMovement tipo)
	{
		return movementsList[tipo.ordinal()];
	}

	public boolean isReady()
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_MOVEMENTS; i++)
		{
			if (movementsList[i] == null)
			{
				return false;
			}
		}
		
		return true;
	}
}
