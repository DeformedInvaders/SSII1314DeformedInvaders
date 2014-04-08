package com.creation.data;

import java.io.Serializable;
import java.util.List;

import com.lib.utils.FloatArray;

public class Movimientos implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List<FloatArray> attack, jump, crouch, run;

	/* Constructora */

	public Movimientos()
	{

	}

	/* Métodos de Modificación de Información */

	public void set(List<FloatArray> movimiento, int pos)
	{
		switch (pos)
		{
			case 0:
				run = movimiento;
			break;
			case 1:
				jump = movimiento;
			break;
			case 2:
				crouch = movimiento;
			break;
			case 3:
				attack = movimiento;
			break;
		}
	}

	/* Métodos de Obtención de Información */

	public List<FloatArray> get(int pos)
	{
		switch (pos)
		{
			case 0:
				return run;
			case 1:
				return jump;
			case 2:
				return crouch;
			case 3:
				return attack;
			default:
				return null;
		}
	}

	public boolean isReady()
	{
		return (run != null && jump != null && crouch != null && attack != null);
	}
}
