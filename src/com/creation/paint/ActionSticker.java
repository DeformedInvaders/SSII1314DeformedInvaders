package com.creation.paint;

import com.creation.data.TTypeSticker;

public class ActionSticker extends Action
{
	private TTypeSticker tipoPegatina;
	private int idPegatina;
	private float posXPegatina, posYPegatina;
	private short indiceTriangulo;
	private float factorEscala, anguloRotacion;
	
	public ActionSticker(TTypeSticker tipo)
	{
		tipoAccion = TTypeAction.Sticker;
		
		tipoPegatina = tipo;
		idPegatina = -1;
	}
	
	public ActionSticker(TTypeSticker tipo, int id, float x, float y, short indice, float factor, float angulo)
	{
		tipoAccion = TTypeAction.Sticker;
		
		tipoPegatina = tipo;
		idPegatina = id;
		posXPegatina = x;
		posYPegatina = y;
		indiceTriangulo = indice;
		factorEscala = factor;
		anguloRotacion = angulo;
	}
	
	/* Métodos de Obtención de Información */

	public TTypeSticker getTipoPegatina()
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