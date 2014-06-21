package com.game.data;

public class Boss extends Enemy
{
	public Boss(int indiceTextura, int idEnemigo)
	{
		super(indiceTextura, idEnemigo);
		
		tipoEntidad = TTypeEntity.Boss;
	}
}
