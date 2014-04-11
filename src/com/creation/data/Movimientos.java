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

	/* M�todos de Modificaci�n de Informaci�n */

	public void set(List<FloatArray> movimiento, TTipoMovimiento tipo)
	{
		switch (tipo)
		{
			case Run:
				run = movimiento;
			break;
			case Jump:
				jump = movimiento;
			break;
			case Crouch:
				crouch = movimiento;
			break;
			case Attack:
				attack = movimiento;
			break;
		}
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public List<FloatArray> get(TTipoMovimiento tipo)
	{
		switch (tipo)
		{
			case Run:
				return run;
			case Jump:
				return jump;
			case Crouch:
				return crouch;
			case Attack:
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
