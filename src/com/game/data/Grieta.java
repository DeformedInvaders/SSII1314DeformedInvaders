package com.game.data;

public class Grieta extends Rectangulo
{
	/* SECTION Constructora */
	
	public Grieta(int indiceTextura, float posObstaculo)
	{
		tipo = TTipoEntidad.Grieta;
		id = 0;
		textura = indiceTextura;
		posicionX = posObstaculo;
	}
}
