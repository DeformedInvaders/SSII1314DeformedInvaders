package com.create.deform;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DeformDataSaved
{
	private FloatArray handles;
	private ShortArray indiceHandles;
	private FloatArray verticesModificados;
	
	public DeformDataSaved(FloatArray handles, ShortArray indiceHandles, FloatArray verticesModificados)
	{
		this.handles = handles;
		this.indiceHandles = indiceHandles;
		this.verticesModificados = verticesModificados;
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
}
