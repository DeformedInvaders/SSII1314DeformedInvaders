package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeSticker;
import com.lib.buffer.Dimensions;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Shot extends Entity
{
	private InstanceEntity instanceEntity;
	
	private boolean activate;
	private int animationPosition;
	
	private float coordX, coordY;
	
	private Rectangle area;
	private Handle handle;
	private boolean areaLoaded;
	
	public Shot(InstanceEntity instancia)
	{
		instanceEntity = instancia;
		
		activate = false;
		animationPosition = 0;
		areaLoaded = false;
		
		coordX = 0.0f;
		coordY = 0.0f;
		
		if (instanceEntity.getTypeEntity() == TTypeEntity.Character)
		{
			typeEntity = TTypeEntity.CharacterShot;
		}
		else if (instanceEntity.getTypeEntity() == TTypeEntity.Boss)
		{
			typeEntity = TTypeEntity.BossShot;
		}
		else
		{
			typeEntity = TTypeEntity.Nothing;
		}
	}
	
	private int shotIndex()
	{
		if (typeEntity == TTypeEntity.CharacterShot)
		{
			return R.drawable.shot_character;
		}
		else if (typeEntity == TTypeEntity.BossShot)
		{
			return R.drawable.shot_boss;
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{		
		if (typeEntity != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_SHOTS; i++)
			{
				Dimensions dim = renderer.loadTextureRectangle(gl, shotIndex(), typeEntity, i, TTypeSticker.Nothing);
				if (dim != null)
				{
					width = dim.getWidth();
					height = dim.getHeight();
				}
			}
			
			area = new Rectangle(coordX, coordY, width, height);
			handle = new Handle(area.getX(), area.getY(), area.getWidth(), area.getHeight(), Color.YELLOW);
			areaLoaded = true;
		}
	}
	
	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_SHOTS; i++)
			{
				renderer.deleteTextureRectangle(typeEntity, i, TTypeSticker.Nothing);
			}
		}
	}

	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			if (activate)
			{
				gl.glPushMatrix();
				
					gl.glTranslatef(coordX, coordY, 0.0f);
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
					renderer.drawTextureRectangle(gl, typeEntity, animationPosition, TTypeSticker.Nothing);
				
				gl.glPopMatrix();
			}
			
			if (areaLoaded && GamePreferences.IS_DEBUG_ENABLED())
			{
				gl.glPushMatrix();
				
					gl.glTranslatef(area.x, area.y, 0.0f);
					
					handle.dibujar(gl);
				
				gl.glPopMatrix();
			}
		}
	}
	
	public float getCoordX() 
	{
		return coordX;
	}
	
	public float getCoordY() 
	{
		return coordY;
	}
	
	public boolean isActive() 
	{
		return activate;
	}
	
	public Rectangle getArea()
	{
		return area;
	}
	
	private void moveArea(float x, float y)
	{
		area.setPosition(x, y);
	}
	
	/* Métodos de modificación de Información */
	
	@Override
	public boolean animateTexture()
	{
		if (activate)
		{
			animationPosition = (animationPosition + 1) % GamePreferences.NUM_TYPE_SHOTS;
			return true;
		}
		
		return false;
	}
	
	public void activate()
	{
		if (!activate)
		{
			if (typeEntity == TTypeEntity.CharacterShot)
			{
				activate = true;
				coordX = instanceEntity.getCoordX() + 3.0f * instanceEntity.getWidth() / 4.0f;
				coordY = instanceEntity.getCoordY() + 2.0f * instanceEntity.getHeight() / 8.0f;
			}
			else if (typeEntity == TTypeEntity.BossShot)
			{
				activate = true;
				coordX = instanceEntity.getCoordX() + instanceEntity.getWidth() / 4.0f;
				coordY = instanceEntity.getCoordY() + 2.0f * instanceEntity.getHeight() / 8.0f;
			}
			
			moveArea(coordX, coordY);
		}
	}
	
	public void deactivate()
	{
		activate = false;
	}
	
	public void move() 
	{
		if (activate)
		{
			if (typeEntity == TTypeEntity.BossShot)
			{
				coordX -= GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
			else if (typeEntity == TTypeEntity.CharacterShot)
			{
				coordX += GamePreferences.DIST_MOVIMIENTO_ENEMIES();
			}
			
			moveArea(coordX, coordY);
		}
	}
	
}
