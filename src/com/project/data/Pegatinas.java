package com.project.data;

import java.io.Serializable;

import com.lib.utils.FloatArray;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	// ID Drawable
	private int indiceOjos, indiceBoca, indiceArma;

	// Vertice Asociado
	private int verticeOjos, verticeBoca, verticeArma;
	
	// Coordenadas Textura
	private FloatArray puntosOjos, puntosBoca, puntosArma;
	
	/* SECTION Constructora */
	
	public Pegatinas()
	{
		indiceOjos = -1;
		indiceBoca = -1;
		indiceArma = -1;
		
		verticeOjos = -1;
		verticeBoca = -1;
		verticeArma = -1;
	}
	
	/* SECTION Métodos de Modificación de Información */
	
	public void setPegatina(int indice, int vertice, int tipo)
	{
		switch(tipo)
		{
			case 0:
				indiceOjos = indice;
				verticeOjos = vertice;
			break;
			case 1:
				indiceBoca = indice;
				verticeBoca = vertice;
			break;
			case 2:
				indiceArma = indice;
				verticeArma = vertice;
			break;
		}
	}
	
	// TODO Comprimir en un solo método
	
	public void setPuntosOjos(FloatArray puntosOjos)
	{
		this.puntosOjos = puntosOjos;
	}

	public FloatArray getPuntosBoca()
	{
		return puntosBoca;
	}

	public void setPuntosBoca(FloatArray puntosBoca)
	{
		this.puntosBoca = puntosBoca;
	}

	public FloatArray getPuntosArma()
	{
		return puntosArma;
	}

	public void setPuntosArma(FloatArray puntosArma)
	{
		this.puntosArma = puntosArma;
	}
	
	/* SECTION Métodos de Obtención de Información */

	public int getIndiceOjos()
	{
		return indiceOjos;
	}

	public int getIndiceBoca()
	{
		return indiceBoca;
	}

	public int getIndiceArma()
	{
		return indiceArma;
	}

	public int getVerticeOjos()
	{
		return verticeOjos;
	}

	public int getVerticeBoca()
	{
		return verticeBoca;
	}

	public int getVerticeArma()
	{
		return verticeArma;
	}

	public FloatArray getPuntosOjos()
	{
		return puntosOjos;
	}
}
