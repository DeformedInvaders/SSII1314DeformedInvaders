package com.game.data;

public class Missil extends Rectangle
{
	/* Constructora */

	public Missil(int indiceTextura, int idMisil)
	{
		tipoEntidad = TTypeEntity.Missil;
		idEntidad = idMisil;
		texturaEntidad = indiceTextura;
	}
}
