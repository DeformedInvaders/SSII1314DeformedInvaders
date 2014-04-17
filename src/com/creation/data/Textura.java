package com.creation.data;

import java.io.Serializable;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.TTipoEntidad;
import com.lib.utils.FloatArray;

public class Textura implements Serializable
{
	private static final long serialVersionUID = 1L;

	private MapaBits mapaBits;
	private FloatArray coordTextura;
	private Pegatinas pegatinas;

	/* Constructora */

	public Textura(MapaBits mapaBits, FloatArray coordTextura, Pegatinas pegatinas)
	{
		this.mapaBits = mapaBits;
		this.coordTextura = coordTextura;
		this.pegatinas = pegatinas;
	}

	/* Métodos de Obtención de Información */

	public MapaBits getMapaBits()
	{
		return mapaBits;
	}

	public FloatArray getCoordTextura()
	{
		return coordTextura;
	}

	public Pegatinas getPegatinas()
	{
		return pegatinas;
	}
	
	public int getHeight()
	{
		return mapaBits.getHeight();
	}
	
	public int getWidth()
	{
		return mapaBits.getWidth();
	}
	
	/* Métodos de representación en renderer */
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context, TTipoEntidad tipo)
	{
		renderer.cargarTexturaMalla(gl, mapaBits.getBitmap(), tipo);
	}

	public void descargarTextura(OpenGLRenderer renderer, TTipoEntidad tipo)
	{
		renderer.descargarTexturaMalla(tipo);
	}

	public void dibujar(GL10 gl, OpenGLRenderer renderer, FloatBuffer triangulos, FloatBuffer coordenadas, TTipoEntidad tipo)
	{
		renderer.dibujarTexturaMalla(gl, triangulos, coordenadas, tipo);
	}
}
