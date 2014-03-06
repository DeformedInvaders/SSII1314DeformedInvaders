package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public class Grieta extends Entidad
{
	public Grieta()
	{
		tipo = TTipoEntidad.Grieta;
	}
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer) { }
	public void descargarTextura(OpenGLRenderer renderer) { }
	public void dibujar(GL10 gl, OpenGLRenderer renderer) { }
}
