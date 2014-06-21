package com.creation.data;

import java.io.Serializable;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.game.data.TTypeEntity;
import com.lib.buffer.VertexArray;

public class Texture implements Serializable
{
	private static final long serialVersionUID = 1L;

	private BitmapImage mapaBits;
	private VertexArray coordTextura;
	private Stickers pegatinas;

	/* Constructora */

	public Texture(BitmapImage mapaBits, VertexArray coordTextura, Stickers pegatinas)
	{
		this.mapaBits = mapaBits;
		this.coordTextura = coordTextura;
		this.pegatinas = pegatinas;
	}

	/* Métodos de Obtención de Información */

	public BitmapImage getMapaBits()
	{
		return mapaBits;
	}

	public VertexArray getCoordTextura()
	{
		return coordTextura;
	}

	public Stickers getPegatinas()
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
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context, TTypeEntity tipoEntidad, int posEntidad)
	{
		renderer.cargarTexturaMalla(gl, mapaBits.getBitmap(), tipoEntidad, posEntidad);
	}

	public void descargarTextura(OpenGLRenderer renderer, TTypeEntity tipoEntidad, int posEntidad)
	{
		renderer.descargarTexturaMalla(tipoEntidad, posEntidad);
	}

	public void dibujar(GL10 gl, OpenGLRenderer renderer, FloatBuffer triangulos, FloatBuffer coordenadas, TTypeEntity tipoEntidad, int posEntidad)
	{
		renderer.dibujarTexturaMalla(gl, triangulos, coordenadas, tipoEntidad, posEntidad);
	}
}
