package com.game.data;

public class Misil extends Rectangulo
{
	/* Constructora */

	public Misil(int indiceTextura, int idMisil)
	{
		tipo = TTipoEntidad.Misil;
		id = idMisil;
		textura = indiceTextura;
	}
}
