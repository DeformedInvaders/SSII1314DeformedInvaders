package com.game.data;

public class Obstaculo extends Rectangulo
{
	/* Constructora */

	public Obstaculo(int indiceTextura, int idObstaculo)
	{
		tipoEntidad = TTipoEntidad.Obstaculo;
		idEntidad = idObstaculo;
		texturaEntidad = indiceTextura;
	}
}
