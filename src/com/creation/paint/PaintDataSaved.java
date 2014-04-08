package com.creation.paint;

import java.util.Stack;

import com.creation.data.Accion;

public class PaintDataSaved
{
	// Estructura de Datos
	private TPaintEstado estado;

	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;

	/* Constructora */

	public PaintDataSaved(Stack<Accion> anteriores, Stack<Accion> siguientes, TPaintEstado estado)
	{
		this.anteriores = anteriores;
		this.siguientes = siguientes;
		this.estado = estado;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public TPaintEstado getEstado()
	{
		return estado;
	}

	public Stack<Accion> getAnteriores()
	{
		return anteriores;
	}

	public Stack<Accion> getSiguientes()
	{
		return siguientes;
	}
}
