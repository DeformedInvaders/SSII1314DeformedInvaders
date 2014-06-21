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
	private int idEntity;
	private TTypeEntity typeEntity;
	
	private float coordX, coordY;
	private float height, width;
	
	private Circle area;
	private Handle handle;
	private boolean areaLoaded;
	
	private int animationPosition;
	private int animationLength;

	public InstanceEntity(int id, TTypeEntity tipo)
	{
		this(id, tipo, 0.0f, 0.0f);
	}
	
	public InstanceEntity(int id, TTypeEntity type, float x, float y)
	{
		idEntity = id;
		typeEntity = type;
		
		areaLoaded = false;
		
		coordX = x;
		coordY = y;
		
		animationPosition = 0;
	}
	
	public void setDimensions(float h, float w)
	{
		height = h;
		width = w;
		
		if (typeEntity == TTypeEntity.Missil || typeEntity == TTypeEntity.Obstacle)
		{
			area = new Circle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.5f);
			handle = new Handle(50, area.radius, Color.BLACK);
			areaLoaded = true;
		}
		else if (typeEntity == TTypeEntity.Character || typeEntity == TTypeEntity.Enemy || typeEntity == TTypeEntity.Boss)
		{
			area = new Circle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.5f);
			handle = new Handle(50, area.radius, Color.RED);
			areaLoaded = true;
		}
	}

	public int getIdEntity()
	{
		return idEntity;
	}
	
	public TTypeEntity getTypeEntity()
	{
		return typeEntity;
	}

	public float getCoordX()
	{
		return coordX;
	}

	public float getCoordY()
	{
		return coordY;
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
		coordX = x;
		coordY = y;
	}
	
	public void move()
	{
		if (typeEntity == TTypeEntity.Character)
		{
			if (animationPosition > 0)
			{
				if (animationPosition < 2 * animationLength / 8)
				{
					coordY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER();
					moveArea(coordX, coordY);
				}
				else if (animationPosition >= 6 * animationLength / 8)
				{
					coordY += GamePreferences.DIST_MOVIMIENTO_CHARACTER();
					moveArea(coordX, coordY);
				}
				
				animationPosition --;
			}
			else
			{
				coordX = 0;
				coordY = 0;
				moveArea(coordX, coordY);
			}
		}
		else if (typeEntity == TTypeEntity.Enemy || typeEntity == TTypeEntity.Missil || typeEntity == TTypeEntity.Obstacle)
		{
			coordX -= GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			moveArea(coordX, coordY);
		}
	}
	
	public void up() 
	{
		coordY += GamePreferences.DIST_MOVIMIENTO_PLATAFORMA();
		moveArea(coordX, coordY);
	}
	
	public void down() 
	{
		coordY -= GamePreferences.DIST_MOVIMIENTO_PLATAFORMA();
		moveArea(coordX, coordY);
	}
	
	public void jump(int numFrames)
	{
		animationPosition = numFrames;
		animationLength = numFrames;
	}
	
	public Circle getArea()
	{
		return area;
	}
	
	private void moveArea(float x, float y)
	{
		area.setPosition(getWidth() / 2.0f + x, getHeight() / 2.0f + y);
	}
	
	public void drawTexture(GL10 gl, OpenGLRenderer renderer, Entity entity)
	{
		gl.glPushMatrix();

			gl.glTranslatef(coordX, coordY, 0.0f);
			
			entity.drawTexture(gl, renderer);
	
		gl.glPopMatrix();
		
		if (areaLoaded && GamePreferences.IS_DEBUG_ENABLED())
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(area.x, area.y, 0.0f);
				
				handle.dibujar(gl);
			
			gl.glPopMatrix();
		}
	}
	
	public TStateCollision collision(InstanceEntity entity, TTypeMovement movement)
	{
		if (Intersector.overlaps(area, entity.getArea()))
		{			
			// Enemigo derrotado
			if (entity.getTypeEntity() == TTypeEntity.Enemy && movement == TTypeMovement.Attack)
			{
				return TStateCollision.EnemyDefeated;
			}
			
			if (entity.getTypeEntity() == TTypeEntity.Missil && movement == TTypeMovement.Crouch)
			{
				return TStateCollision.Nothing;
			}

			return TStateCollision.EnemyCollision;
		}

		return TStateCollision.Nothing;
	}
	
	public TStateCollision collision(Shot shot)
	{
		if (shot.isActive() && Intersector.overlaps(area, shot.getArea()))
		{
			return TStateCollision.EnemyCollision;
		}

		return TStateCollision.Nothing;
	}
}
