package com.creation.paint;

import java.util.Stack;

import com.main.controller.DataSaved;

public class PaintDataSaved extends DataSaved
{
	// Estructura de Datos
	private TStatePaint state;

	// Anterior Siguiente Buffers
	private Stack<Action> prevBuffer;
	private Stack<Action> nextBuffer;

	/* Constructora */

	public PaintDataSaved(Stack<Action> prevBuffer, Stack<Action> nextBuffer, TStatePaint state)
	{
		this.prevBuffer = prevBuffer;
		this.nextBuffer = nextBuffer;
		this.state = state;
	}

	/* Métodos de Obtención de Información */

	public TStatePaint getState()
	{
		return state;
	}

	public Stack<Action> getPrevBuffer()
	{
		return prevBuffer;
	}

	public Stack<Action> getNextBuffer()
	{
		return nextBuffer;
	}
}
