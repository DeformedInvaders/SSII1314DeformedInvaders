package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public class Obstaculo extends Entidad
{
	public Obstaculo()
	{
		tipo = TTipoEntidad.Obstaculo;
	}
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer) { }
	public void descargarTextura(OpenGLRenderer renderer) { }
	public void dibujar(GL10 gl, OpenGLRenderer renderer) { }
}
