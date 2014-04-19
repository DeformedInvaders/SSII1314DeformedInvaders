package com.creation.design;

import com.lib.opengl.HullArray;
import com.lib.opengl.TriangleArray;
import com.lib.opengl.VertexArray;
import com.project.controller.DataSaved;

public class DesignDataSaved extends DataSaved
{
	private VertexArray puntos;
	private VertexArray vertices;
	private TriangleArray triangulos;
	private HullArray contorno;

	private TEstadoDesign estado;
	private boolean poligonoSimple;

	/* Constructora */

	public DesignDataSaved(VertexArray puntos, VertexArray vertices, TriangleArray triangulos, HullArray contorno, TEstadoDesign estado, boolean poligonoSimple)
	{
		this.puntos = puntos;
		this.vertices = vertices;
		this.triangulos = triangulos;
		this.contorno = contorno;
		this.estado = estado;
		this.poligonoSimple = poligonoSimple;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public VertexArray getPuntos()
	{
		return puntos;
	}

	public VertexArray getVertices()
	{
		return vertices;
	}

	public TriangleArray getTriangulos()
	{
		return triangulos;
	}

	public HullArray getContorno()
	{
		return contorno;
	}

	public TEstadoDesign getEstado()
	{
		return estado;
	}

	public boolean getPoligonoSimple()
	{
		return poligonoSimple;
	}
}
