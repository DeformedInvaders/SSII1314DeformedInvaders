package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.main.model.GamePreferences;
import com.video.video.TEstadoVideo;

public class ObjetoMovil extends ObjetoAnimado
{
	private float posicionOriginalX, posicionOriginalY, factorOriginal;
	
	public ObjetoMovil(int id, int[] texturas, float posX, float posY, TEstadoVideo estado, int sonido, TTipoAnimacion animacion)
	{
		super(id, texturas, posX, posY, estado, sonido, animacion);
		
		posicionOriginalX = posX;
		posicionOriginalY = posY;
		factorOriginal = 1.0f;
		
		numeroCiclosEspera = 1;
	}

	@Override
	public void reposo()
	{
		super.reposo();
		
		posicionX = posicionOriginalX;
		posicionY = posicionOriginalY;
		factor = factorOriginal;
	}
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		posicionY = posicionY + GamePreferences.DIST_MOVIMIENTO_SPACESHIP();
		factor = factor * 0.998f;
		
		super.dibujar(gl, renderer);
	}
}
