package com.game.data;

public class Misil extends Rectangulo
{
	/* Constructora */

	public Misil(int indiceTextura, int idMisil)
	{
		tipoEntidad = TTipoEntidad.Misil;
		idEntidad = idMisil;
		texturaEntidad = indiceTextura;
	}
}
