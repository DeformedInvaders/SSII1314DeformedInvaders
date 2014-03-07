package com.game.data;

import java.util.List;

import com.lib.utils.FloatArray;

public class Enemigo extends Malla
{
	private List<FloatArray> movimiento;
	
	/* SECTION Constructora */
	
	public Enemigo(int idEnemigo)
	{
		tipo = TTipoEntidad.Enemigo;
		id = idEnemigo;
	}
	
	/* SECTION M�todos abstractos de Entidad */
	
	@Override
	public void avanzar()
	{
		posicion -= DIST_AVANCE;
	}
	
	/* SECTION M�todos de Animaci�n */
	
	public void mover() 
	{
		listaVerticesAnimacion = movimiento;
		
		iniciar();
	}
	
	/* SECTION M�todos de Modificaci�n de Informaci�n */
	
	public void setMovimientos(List<FloatArray> m)
	{
		movimiento = m;
		
		reposo();
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	public List<FloatArray> getMovimientos()
	{
		return movimiento;
	}
	
}
