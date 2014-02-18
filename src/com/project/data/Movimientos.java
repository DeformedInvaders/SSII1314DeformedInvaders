package com.project.data;

import java.io.Serializable;
import java.util.List;

import com.lib.utils.FloatArray;

public class Movimientos implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private List<FloatArray> attack, jump, down, run;
	
	public Movimientos()
	{
		
	}

	public void set(List<FloatArray> movimiento, int pos) 
	{
		switch(pos)
		{
			case 0: run = movimiento;
			break;
			case 1: jump = movimiento;
			break;
			case 2: down = movimiento;
			break;
			case 3: attack = movimiento;
			break;
		}
	}
	
	public List<FloatArray> movimientoRun()
	{
		return run;
	}
	
	public List<FloatArray> movimientoJump()
	{
		return jump;
	}
	
	public List<FloatArray> movimientoDown()
	{
		return down;
	}
	
	public List<FloatArray> movimientoAttack()
	{
		return attack;
	}
	
	public boolean isReady()
	{
		return (run != null && jump != null && down != null && attack != null);
	}
}
