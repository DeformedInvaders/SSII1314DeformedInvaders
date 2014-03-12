package com.game.game;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;
import com.game.data.Entidad;

public class InstanciaEntidad 
{
	public static final float DIST_AVANCE = 20.0f;
	
	private int entidad;
	private boolean pintar, derrotado;
	private float posX, posY;
	
	public InstanciaEntidad(int entidad, float posX)
	{
		pintar = true;
		derrotado = false;
		this.entidad = entidad;
		this.posX = posX;
	}

	public boolean isPintar() 
	{
		return pintar;
	}

	public void setNoPintar() 
	{
		this.pintar = false;
	}

	public boolean isDerrotado() 
	{
		return derrotado;
	}

	public void setDerrotado() 
	{
		this.derrotado = true;
	}

	public int getEntidad() 
	{
		return entidad;
	}

	public float getPosX() 
	{
		return posX;
	}

	public float getPosY() 
	{
		return posY;
	}

	public void avanzar(OpenGLRenderer renderer, Entidad entidad)
	{
		posX -= DIST_AVANCE;

		pintar = (posX < renderer.getScreenWidth() && posX > -entidad.getWidth());
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, Entidad entidad)
	{
		if(pintar && !derrotado)
		{
			gl.glPushMatrix();
				gl.glTranslatef(posX, posY, 0.0f);
				
				entidad.dibujar(gl, renderer);
			gl.glPopMatrix();
		}
	}
	
	public int compareTo(InstanciaEntidad a)
	{
		if(posX > a.getPosX()) return 1;
		else if(posX < a.getPosX()) return -1;
		else return 0;
	}
}
