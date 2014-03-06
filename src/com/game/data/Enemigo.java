package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public class Enemigo extends Entidad
{
	public Enemigo()
	{
		tipo = TTipoEntidad.Enemigo;
	}
	
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer) { }
	public void descargarTextura(OpenGLRenderer renderer) { }
	public void dibujar(GL10 gl, OpenGLRenderer renderer) { }
}
