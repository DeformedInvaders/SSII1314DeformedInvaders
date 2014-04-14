package com.creation.paint;

import java.util.Stack;

import com.creation.data.Accion;
import com.project.controller.DataSaved;

public class PaintDataSaved extends DataSaved
{
	// Estructura de Datos
	private TEstadoPaint estado;

	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;

	/* Constructora */

	public PaintDataSaved(Stack<Accion> anteriores, Stack<Accion> siguientes, TEstadoPaint estado)
	{
		this.anteriores = anteriores;
		this.siguientes = siguientes;
		this.estado = estado;
	}

	/* Métodos de Obtención de Información */

	public TEstadoPaint getEstado()
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
