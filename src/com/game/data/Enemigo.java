package com.game.data;

public class Enemigo extends Rectangulo
{
	// private List<FloatArray> movimiento;

	/* Constructora */

	/*
	public Enemigo(int idEnemigo)
	{
		tipo = TTipoEntidad.Enemigo;
		id = idEnemigo;
	}
	*/
	
	public Enemigo(int indiceTextura, int idEnemigo)
	{
		tipo = TTipoEntidad.Enemigo;
		id = idEnemigo;
		textura = indiceTextura;
	}

	/* Métodos abstractos de Entidad */

	/*
	@Override
	public void avanzar()
	{
		posicionX -= DIST_AVANCE;
	}
	*/
	
	/* Métodos de Animación */

	/*
	public void mover()
	{
		listaVerticesAnimacion = movimiento;
		iniciar();
	}
	*/
	
	/* Métodos de Modificación de Información */

	/*
	public void setMovimientos(List<FloatArray> m)
	{
		movimiento = m
		reposo();
	}
	*/
	
	/* Métodos de Obtención de Información */

	/*
	public List<FloatArray> getMovimientos()
	{
		return movimiento;
	}
	*/
}
