package com.game.data;


public class Enemigo extends Rectangulo
{
	//private List<FloatArray> movimiento;
	
	/* SECTION Constructora */
	
	/*public Enemigo(int idEnemigo)
	{
		tipo = TTipoEntidad.Enemigo;
		id = idEnemigo;
	}*/
	
	public Enemigo(int indiceTextura, int idEnemigo, float posObstaculo)
	{
		tipo = TTipoEntidad.Enemigo;
		id = idEnemigo;
		textura = indiceTextura;
		posicionX = posObstaculo;
	}
	
	/* SECTION Métodos abstractos de Entidad */
	
	/*@Override
	public void avanzar()
	{
		posicion -= DIST_AVANCE;
	}*/
	
	/* SECTION Métodos de Animación */
	
	/*public void mover() 
	{
		listaVerticesAnimacion = movimiento;
		
		iniciar();
	}*/
	
	/* SECTION Métodos de Modificación de Información */
	
	/*public void setMovimientos(List<FloatArray> m)
	{
		movimiento = m;
		
		reposo();
	}*/
	
	/* SECTION Métodos de Obtención de Información */
	
	/*public List<FloatArray> getMovimientos()
	{
		return movimiento;
	}*/
	
}
