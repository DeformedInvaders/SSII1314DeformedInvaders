package com.creation.design;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.controller.DataSaved;

public class DesignDataSaved extends DataSaved
{
	private FloatArray puntos;
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;

	private TEstadoDesign estado;
	private boolean poligonoSimple;

	/* Constructora */

	public DesignDataSaved(FloatArray puntos, FloatArray vertices, ShortArray triangulos, ShortArray contorno, TEstadoDesign estado, boolean poligonoSimple)
	{
		this.puntos = puntos;
		this.vertices = vertices;
		this.triangulos = triangulos;
		this.contorno = contorno;
		this.estado = estado;
		this.poligonoSimple = poligonoSimple;
	}

	/* Métodos de Obtención de Información */

	public FloatArray getPuntos()
	{
		return puntos;
	}

	public FloatArray getVertices()
	{
		return vertices;
	}

	public ShortArray getTriangulos()
	{
		return triangulos;
	}

	public ShortArray getContorno()
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
