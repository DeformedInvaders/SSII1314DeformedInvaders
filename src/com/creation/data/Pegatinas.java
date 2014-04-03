package com.creation.data;

import java.io.Serializable;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final int NUM_PEGATINAS = 3;

	// ID Drawable
	private int[] indicePegatinas;

	// Vertice Asociado
	private int[] verticePegatinas;

	/* SECTION Constructora */

	public Pegatinas()
	{
		indicePegatinas = new int[NUM_PEGATINAS];
		verticePegatinas = new int[NUM_PEGATINAS];

		for (int i = 0; i < NUM_PEGATINAS; i++)
		{
			indicePegatinas[i] = -1;
			verticePegatinas[i] = -1;
		}
	}

	/* SECTION M�todos de Modificaci�n de Informaci�n */

	public void setPegatina(int indice, int vertice, int tipo)
	{
		if (tipo >= 0 && tipo < NUM_PEGATINAS)
		{
			indicePegatinas[tipo] = indice;
			verticePegatinas[tipo] = vertice;
		}
	}

	/* SECTION M�todos de Obtenci�n de Informaci�n */

	public boolean isCargada(int tipo)
	{
		return indicePegatinas[tipo] != -1;
	}

	public int getIndice(int tipo)
	{
		return indicePegatinas[tipo];
	}

	public int getVertice(int tipo)
	{
		return verticePegatinas[tipo];
	}

	public int getNumPegatinas()
	{
		return NUM_PEGATINAS;
	}
}
