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

	/* M�todos abstractos de Entidad */

	/*
	@Override
	public void avanzar()
	{
		posicionX -= DIST_AVANCE;
	}
	*/
	
	/* M�todos de Animaci�n */

	/*
	public void mover()
	{
		listaVerticesAnimacion = movimiento;
		iniciar();
	}
	*/
	
	/* M�todos de Modificaci�n de Informaci�n */

	/*
	public void setMovimientos(List<FloatArray> m)
	{
		movimiento = m
		reposo();
	}
	*/
	
	/* M�todos de Obtenci�n de Informaci�n */

	/*
	public List<FloatArray> getMovimientos()
	{
		return movimiento;
	}
	*/
}
