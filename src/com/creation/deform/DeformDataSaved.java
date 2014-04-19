package com.creation.deform;

import java.util.List;

import com.lib.opengl.VertexArray;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.controller.DataSaved;

public class DeformDataSaved extends DataSaved
{
	private FloatArray handles;
	private ShortArray indiceHandles;
	private VertexArray verticesModificados;
	private List<VertexArray> listaVertices;
	private TEstadoDeform estado;

	/* Constructora */

	public DeformDataSaved(FloatArray handles, ShortArray indiceHandles, VertexArray verticesModificados, TEstadoDeform estado, List<VertexArray> listaVertices)
	{
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		this.verticesModificados = verticesModificados;
		this.estado = estado;
		this.listaVertices = listaVertices;

	}

	/* Métodos Obtención de Información */

	public FloatArray getHandles()
	{
		return handles;
	}

	public ShortArray getIndiceHandles()
	{
		return indiceHandles;
	}

	public VertexArray getVerticesModificados()
	{
		return verticesModificados;
	}

	public List<VertexArray> getListaVertices()
	{
		return listaVertices;
	}

	public TEstadoDeform getEstado()
	{
		return estado;
	}
}
