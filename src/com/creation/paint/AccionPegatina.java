package com.creation.paint;

import com.creation.data.TTipoAccion;
import com.creation.data.TTipoSticker;

public class AccionPegatina extends Accion
{
	private TTipoSticker tipoPegatina;
	private int idPegatina;
	private float posXPegatina, posYPegatina;
	private short indiceTriangulo;
	private float factorEscala, anguloRotacion;
	
	public AccionPegatina(TTipoSticker tipo)
	{
		tipoAccion = TTipoAccion.Pegatina;
		
		tipoPegatina = tipo;
		idPegatina = -1;
	}
	
	public AccionPegatina(TTipoSticker tipo, int id, float x, float y, short indice, float factor, float angulo)
	{
		tipoAccion = TTipoAccion.Pegatina;
		
		tipoPegatina = tipo;
		idPegatina = id;
		posXPegatina = x;
		posYPegatina = y;
		indiceTriangulo = indice;
		factorEscala = factor;
		anguloRotacion = angulo;
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

	public float getFactorEscala()
	{
		return factorEscala;
	}

	public float getAnguloRotacion()
	{
		return anguloRotacion;
	}	
}