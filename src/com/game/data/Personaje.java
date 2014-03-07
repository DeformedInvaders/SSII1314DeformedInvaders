package com.game.data;

import com.creation.data.Movimientos;

public class Personaje extends Malla
{	
	private Movimientos movimientos;
	
	/* SECTION Constructora */
	
	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
	}
	
	/* SECTION Métodos abstractos de Entidad */
	
	@Override
	public void avanzar()
	{
		posicion += DIST_AVANCE;
	}
	
	/* SECTION Métodos de Animación */
	
	public void mover() 
	{
		listaVerticesAnimacion = movimientos.get(0);
		
		iniciar();
	}
	
	public void saltar() 
	{
		listaVerticesAnimacion = movimientos.get(1);
		
		iniciar();
	}
	
	public void agachar() 
	{
		listaVerticesAnimacion = movimientos.get(2);
		
		iniciar();
	}
	
	public void atacar() 
	{
		listaVerticesAnimacion = movimientos.get(3);
		
		iniciar();
	}
	
	/* SECTION Métodos de Modificación de Información */
	
	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		
		reposo();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public Movimientos getMovimientos()
	{
		return movimientos;
	}
}
