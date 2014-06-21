package com.creation.deform;

import java.util.List;

import com.lib.buffer.HandleArray;
import com.lib.buffer.VertexArray;
import com.main.controller.DataSaved;

public class DeformDataSaved extends DataSaved
{
	private HandleArray handles;
	private VertexArray verticesModificados;
	private List<VertexArray> listaVertices;
	private TStateDeform estado;

	/* Constructora */

	public DeformDataSaved(HandleArray handles, VertexArray verticesModificados, TStateDeform estado, List<VertexArray> listaVertices)
	{
		this.handles = handles;
		this.verticesModificados = verticesModificados;
		this.estado = estado;
		this.listaVertices = listaVertices;
	}

	/* M�todos Obtenci�n de Informaci�n */

	public HandleArray getHandles()
	{
		return handles;
	}

	public VertexArray getVerticesModificados()
	{
		return verticesModificados;
	}

	public List<VertexArray> getListaVertices()
	{
		return listaVertices;
	}

	public TStateDeform getEstado()
	{
		return estado;
	}
}
