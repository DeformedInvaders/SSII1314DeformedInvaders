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
	
	public PaintDataSaved(Stack<Accion> anteriores, Stack<Accion> siguientes, TPaintEstado estado)
	{
		this.anteriores = anteriores;
		this.siguientes = siguientes;
		this.estado = estado;
	}

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
