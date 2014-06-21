package com.creation.deform;

import java.util.List;

import com.lib.buffer.HandleArray;
import com.lib.buffer.VertexArray;
import com.main.controller.DataSaved;

public class DeformDataSaved extends DataSaved
{
	private HandleArray handles;
	private VertexArray vertices;
	private List<VertexArray> animationVertices;
	private TStateDeform state;

	/* Constructora */

	public DeformDataSaved(HandleArray handles, VertexArray vertices, TStateDeform state, List<VertexArray> animationVertices)
	{
		this.handles = handles;
		this.vertices = vertices;
		this.state = state;
		this.animationVertices = animationVertices;
	}

	/* Métodos Obtención de Información */

	public HandleArray getHandles()
	{
		return handles;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public List<VertexArray> getAnimationVertices()
	{
		return animationVertices;
	}

	public TStateDeform getState()
	{
		return state;
	}
}
