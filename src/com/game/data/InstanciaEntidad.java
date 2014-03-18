package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.view.OpenGLRenderer;

public class InstanciaEntidad implements Comparable<InstanciaEntidad>
{
	public static final float DIST_AVANCE = 20.0f;
	
	private int idEntidad;
	private boolean pintar, derrotado;
	private float posicionX, posicionY;
	
	public InstanciaEntidad(int id, float posX)
	{
		pintar = true;
		derrotado = false;
		idEntidad = id;
		
		posicionX = posX;
		posicionY = 0.0f;
	}

	public boolean isPintar() 
	{
		return pintar;
	}

	public void setNoPintar() 
	{
		pintar = false;
	}

	public boolean isDerrotado() 
	{
		return derrotado;
	}

	public void setDerrotado() 
	{
		derrotado = true;
	}

	public int getIdEntidad() 
	{
		return idEntidad;
	}

	public float getPosicionX() 
	{
		return posicionX;
	}

	public float getPosicionY() 
	{
		return posicionY;
	}

	public void avanzar(OpenGLRenderer renderer, Entidad entidad)
	{
		posicionX -= DIST_AVANCE;

		pintar = (posicionX < renderer.getScreenWidth() && posicionX > -entidad.getWidth());
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, Entidad entidad)
	{
		if(pintar && !derrotado)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(posicionX, posicionY, 0.0f);
				
				entidad.dibujar(gl, renderer);
				
			gl.glPopMatrix();
		}
	}
	
	@Override
	public int compareTo(InstanciaEntidad entidad)
	{
		if(posicionX > entidad.getPosicionX())
		{
			return 1;
		}
		else if(posicionX < entidad.getPosicionX())
		{
			return -1;
		}
		
		return 0;
	}
}
