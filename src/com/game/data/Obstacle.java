package com.game.data;

public class Obstacle extends Rectangle
{
	/* Constructora */

	public Obstacle(int indiceTextura, int idObstaculo)
	{
		tipoEntidad = TTypeEntity.Obstacle;
		idEntidad = idObstaculo;
		texturaEntidad = indiceTextura;
	}
}
