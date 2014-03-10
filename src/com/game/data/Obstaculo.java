package com.game.data;

public class Obstaculo extends Rectangulo
{
	/* SECTION Constructora */
	
	public Obstaculo(int indiceTextura, int idObstaculo, float posObstaculo)
	{
		tipo = TTipoEntidad.Obstaculo;
		id = idObstaculo;
		textura = indiceTextura;
		posicionX = posObstaculo;
	}
}
