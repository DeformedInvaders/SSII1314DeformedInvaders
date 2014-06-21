package com.creation.paint;

import java.util.Stack;

import com.main.controller.DataSaved;

public class PaintDataSaved extends DataSaved
{
	// Estructura de Datos
	private TStatePaint estado;

	// Anterior Siguiente Buffers
	private Stack<Action> anteriores;
	private Stack<Action> siguientes;

	/* Constructora */

	public PaintDataSaved(Stack<Action> anteriores, Stack<Action> siguientes, TStatePaint estado)
	{
		this.anteriores = anteriores;
		this.siguientes = siguientes;
		this.estado = estado;
	}

	/* Métodos de Obtención de Información */

	public TStatePaint getEstado()
	{
		return estado;
	}

	public Stack<Action> getAnteriores()
	{
		return anteriores;
	}

	public Stack<Action> getSiguientes()
	{
		return siguientes;
	}
}
