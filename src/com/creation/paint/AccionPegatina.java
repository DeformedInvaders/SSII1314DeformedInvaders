package com.creation.paint;

import com.creation.data.TTipoAccion;
import com.creation.data.TTipoSticker;

public class AccionPegatina extends Accion
{
	private TTipoSticker tipoPegatina;
	private int idPegatina;
	private float posXPegatina, posYPegatina;
	private short indiceTriangulo;
	
	public AccionPegatina(TTipoSticker tipo, int id, float x, float y, short indice)
	{
		tipoAccion = TTipoAccion.Pegatina;
		
		tipoPegatina = tipo;
		idPegatina = id;
		posXPegatina = x;
		posYPegatina = y;
		indiceTriangulo = indice;
	}
	
	/* Métodos de Obtención de Información */

	public TTipoSticker getTipoPegatina()
	{
		return tipoPegatina;
	}

	public int getIdPegatina()
	{
		return idPegatina;
	}

	public short getIndiceTriangulo()
	{
		return indiceTriangulo;
	}

	public float getPosXPegatina()
	{
		return posXPegatina;
	}

	public float getPosYPegatina()
	{
		return posYPegatina;
	}	
}