package com.create.design;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DesignDataSaved
{
	private FloatArray puntos;	
	
	private FloatArray puntosTest;
	private ShortArray triangulosTest;
	
	private TDesignEstado estado;
	
	public DesignDataSaved(FloatArray puntos, FloatArray puntosTest, ShortArray triangulosTest, TDesignEstado estado)
	{
		this.puntos = puntos;
		this.puntosTest = puntosTest;
		this.triangulosTest = triangulosTest;
		this.estado = estado;
	}

	public FloatArray getPuntos()
	{
		return puntos;
	}

	public FloatArray getPuntosTest()
	{
		return puntosTest;
	}

	public ShortArray getTriangulosTest()
	{
		return triangulosTest;
	}

	public TDesignEstado getEstado()
	{
		return estado;
	}
}
