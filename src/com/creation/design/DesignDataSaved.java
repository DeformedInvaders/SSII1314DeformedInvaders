package com.creation.design;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DesignDataSaved
{
	private FloatArray puntos;	
	private FloatArray vertices;
	private ShortArray triangulos;
	private ShortArray contorno;
	
	private TDesignEstado estado;
	private boolean poligonoSimple;
	
	/* SECTION Constructora */
	
	public DesignDataSaved(FloatArray puntos, FloatArray vertices, ShortArray triangulos, ShortArray contorno, TDesignEstado estado, boolean poligonoSimple)
	{
		this.puntos = puntos;
		this.vertices = vertices;
		this.triangulos = triangulos;
		this.contorno = contorno;
		this.estado = estado;
		this.poligonoSimple = poligonoSimple;
	}
	
	/* SECTION Métodos de Obtención de Información */

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

	public TDesignEstado getEstado()
	{
		return estado;
	}
	
	public boolean getPoligonoSimple()
	{
		return poligonoSimple;
	}
}
