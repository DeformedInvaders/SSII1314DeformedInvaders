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
	
	/* SECTION Métodos abstractos de Entidad */
	
	@Override
	public void avanzar()
	{
		posicion -= DIST_AVANCE;
	}
	
	/* SECTION Métodos de Animación */
	
	public void mover() 
	{
		listaVerticesAnimacion = movimiento;
		
		iniciar();
	}
	
	/* SECTION Métodos de Modificación de Información */
	
	public void setMovimientos(List<FloatArray> m)
	{
		movimiento = m;
		
		reposo();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public List<FloatArray> getMovimientos()
	{
		return movimiento;
	}
	
}
