package com.create.deform;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DeformDataSaved
{
	private FloatArray handles;
	private ShortArray indiceHandles;
	private FloatArray verticesModificados;
	private TDeformEstado estado;
	
	public DeformDataSaved(FloatArray handles, ShortArray indiceHandles, FloatArray verticesModificados, TDeformEstado estado)
	{
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		this.verticesModificados = verticesModificados;
		this.estado = estado;
	}

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
	
	public TDeformEstado getEstado()
	{
		return estado;
	}
}
