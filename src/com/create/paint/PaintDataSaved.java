package com.create.paint;

import java.util.Stack;

import com.project.data.Accion;

public class PaintDataSaved
{
	// Estructura de Datos
	private TPaintEstado estado;
	
	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;
	
	/* SECTION Constructora */
	
	public PaintDataSaved(Stack<Accion> anteriores, Stack<Accion> siguientes, TPaintEstado estado)
	{
		this.anteriores = anteriores;
		this.siguientes = siguientes;
		this.estado = estado;
	}
	
	/* SECTION Métodos de Obtención de Información */

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
