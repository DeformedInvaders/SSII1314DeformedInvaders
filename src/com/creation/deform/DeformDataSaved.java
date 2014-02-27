package com.creation.deform;

import java.util.List;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DeformDataSaved
{
	private FloatArray handles;
	private ShortArray indiceHandles;
	private FloatArray verticesModificados;
	private List<FloatArray> listaVertices;
	private TDeformEstado estado;
	
	/* SECTION Constructora */
	
	public DeformDataSaved(FloatArray handles, ShortArray indiceHandles, FloatArray verticesModificados, TDeformEstado estado, List<FloatArray> listaVertices)
	{
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		this.verticesModificados = verticesModificados;
		this.estado = estado;
		this.listaVertices = listaVertices;
		
	}
	
	/* SECTION Métodos Obtención de Información */

	public FloatArray getHandles()
	{
		return handles;
	}

	public ShortArray getIndiceHandles()
	{
		return indiceHandles;
	}

	public FloatArray getVerticesModificados()
	{
		return verticesModificados;
	}
	public List<FloatArray> getListaVertices()
	{
		return listaVertices;
	}
	
	public TDeformEstado getEstado()
	{
		return estado;
	}
}
