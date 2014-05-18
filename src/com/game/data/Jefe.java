package com.game.data;

public class Jefe extends Enemigo
{
	public Jefe(int indiceTextura, int idEnemigo)
	{
		super(indiceTextura, idEnemigo);
		
		tipoEntidad = TTipoEntidad.Jefe;
	}
}
