package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTipoMovimiento;
import com.game.game.TEstadoColision;
import com.lib.math.Circle;
import com.lib.math.Intersector;
import com.main.model.GamePreferences;

public class InstanciaEntidad
{
	private int idEntidad;
	private TTipoEntidad tipoEntidad;
	
	private float posicionX, posicionY;
	private float height, width;
	
	private Circle area;
	private Handle handle;
	private boolean areaCargada;
	
	private int indiceSalto;
	private int numSalto;

	public InstanciaEntidad(int id, TTipoEntidad tipo)
	{
		this(id, tipo, 0.0f, 0.0f);
	}
	
	public InstanciaEntidad(int id, TTipoEntidad tipo, float posX, float posY)
	{
		idEntidad = id;
		tipoEntidad = tipo;
		
		areaCargada = false;
		
		posicionX = posX;
		posicionY = posY;
		
		indiceSalto = 0;
	}
	
	public void setDimensions(float h, float w)
	{
		height = h;
		width = w;
		
		if (tipoEntidad == TTipoEntidad.Misil || tipoEntidad == TTipoEntidad.Obstaculo)
		{
			area = new Circle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.5f);
			handle = new Handle(50, area.radius, Color.BLACK);
			areaCargada = true;
		}
		else if (tipoEntidad == TTipoEntidad.Personaje || tipoEntidad == TTipoEntidad.Enemigo)
		{
			area = new Circle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.5f);
			handle = new Handle(50, area.radius, Color.RED);
			areaCargada = true;
		}
	}

	public int getIdEntidad()
	{
		return idEntidad;
	}
	
	public TTipoEntidad getTipoEntidad()
	{
		return tipoEntidad;
	}

	public float getPosicionX()
	{
		return posicionX;
	}

	public float getPosicionY()
	{
		return posicionY;
	}
	
	public float getHeight()
	{
		return height;
	}
	
	public float getWidth()
	{
		return width;
	}
	
	public void setPosicion(float x, float y)
	{
		posicionX = x;
		posicionY = y;
	}
	
	public void avanzar()
	{
		if (tipoEntidad == TTipoEntidad.Personaje)
		{
			if (indiceSalto > 0)
			{
				if (indiceSalto < 2 * numSalto / 8)
				{
					posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER();
					moverArea(posicionX, posicionY);
				}
				else if (indiceSalto >= 6 * numSalto / 8)
				{
					posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER();
					moverArea(posicionX, posicionY);
				}
				
				indiceSalto --;
			}
			else
			{
				restaurar();
			}
		}
		else if (tipoEntidad == TTipoEntidad.Enemigo || tipoEntidad == TTipoEntidad.Misil || tipoEntidad == TTipoEntidad.Obstaculo)
		{
			posicionX -= GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			moverArea(posicionX, posicionY);
		}
	}
	
	public void subir() 
	{
		posicionY += GamePreferences.DIST_MOVIMIENTO_PLATAFORMA();
		moverArea(posicionX, posicionY);
	}
	
	public void bajar() 
	{
		posicionY -= GamePreferences.DIST_MOVIMIENTO_PLATAFORMA();
		moverArea(posicionX, posicionY);
	}
	
	public void saltar(int numFrames)
	{
		indiceSalto = numFrames;
		numSalto = numFrames;
	}
	
	public void restaurar()
	{
		posicionX = 0;
		posicionY = 0;
		moverArea(posicionX, posicionY);
	}
	
	public Circle getArea()
	{
		return area;
	}
	
	private void moverArea(float x, float y)
	{
		area.setPosition(getWidth() / 2.0f + x, getHeight() / 2.0f + y);
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, Entidad entidad)
	{
		gl.glPushMatrix();

			gl.glTranslatef(posicionX, posicionY, 0.0f);
			
			entidad.dibujar(gl, renderer);
	
		gl.glPopMatrix();
		
		if (areaCargada && GamePreferences.IS_DEBUG_ENABLED())
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(area.x, area.y, 0.0f);
				
				handle.dibujar(gl);
			
			gl.glPopMatrix();
		}
	}
	
	public TEstadoColision colision(InstanciaEntidad entidad, TTipoMovimiento movimiento)
	{
		if (Intersector.overlaps(area, entidad.getArea()))
		{			
			// Enemigo derrotado
			if (entidad.getTipoEntidad() == TTipoEntidad.Enemigo && movimiento == TTipoMovimiento.Attack)
			{
				return TEstadoColision.EnemigoDerrotado;
			}
			
			if (entidad.getTipoEntidad() == TTipoEntidad.Misil && movimiento == TTipoMovimiento.Crouch)
			{
				return TEstadoColision.Nada;
			}

			return TEstadoColision.Colision;
		}

		return TEstadoColision.Nada;
	}
	
	public TEstadoColision colision(Disparo disparo)
	{
		if (disparo.isActivado() && Intersector.overlaps(area, disparo.getArea()))
		{
			return TEstadoColision.Colision;
		}

		return TEstadoColision.Nada;
	}
}
