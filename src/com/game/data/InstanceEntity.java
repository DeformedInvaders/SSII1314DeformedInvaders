package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeMovement;
import com.game.game.TStateCollision;
import com.lib.math.Circle;
import com.lib.math.Intersector;
import com.main.model.GamePreferences;

public class InstanceEntity
{
	private int idEntidad;
	private TTypeEntity tipoEntidad;
	
	private float posicionX, posicionY;
	private float height, width;
	
	private Circle area;
	private Handle handle;
	private boolean areaCargada;
	
	private int indiceSalto;
	private int numSalto;

	public InstanceEntity(int id, TTypeEntity tipo)
	{
		this(id, tipo, 0.0f, 0.0f);
	}
	
	public InstanceEntity(int id, TTypeEntity tipo, float posX, float posY)
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
		
		if (tipoEntidad == TTypeEntity.Missil || tipoEntidad == TTypeEntity.Obstacle)
		{
			area = new Circle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.5f);
			handle = new Handle(50, area.radius, Color.BLACK);
			areaCargada = true;
		}
		else if (tipoEntidad == TTypeEntity.Character || tipoEntidad == TTypeEntity.Enemy || tipoEntidad == TTypeEntity.Boss)
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
	
	public TTypeEntity getTipoEntidad()
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
		if (tipoEntidad == TTypeEntity.Character)
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
		else if (tipoEntidad == TTypeEntity.Enemy || tipoEntidad == TTypeEntity.Missil || tipoEntidad == TTypeEntity.Obstacle)
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
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, Entity entidad)
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
	
	public TStateCollision colision(InstanceEntity entidad, TTypeMovement movimiento)
	{
		if (Intersector.overlaps(area, entidad.getArea()))
		{			
			// Enemigo derrotado
			if (entidad.getTipoEntidad() == TTypeEntity.Enemy && movimiento == TTypeMovement.Attack)
			{
				return TStateCollision.EnemyDefeated;
			}
			
			if (entidad.getTipoEntidad() == TTypeEntity.Missil && movimiento == TTypeMovement.Crouch)
			{
				return TStateCollision.Nothing;
			}

			return TStateCollision.EnemyCollision;
		}

		return TStateCollision.Nothing;
	}
	
	public TStateCollision colision(Shot disparo)
	{
		if (disparo.isActivado() && Intersector.overlaps(area, disparo.getArea()))
		{
			return TStateCollision.EnemyCollision;
		}

		return TStateCollision.Nothing;
	}
}
