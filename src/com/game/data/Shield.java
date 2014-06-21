package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Shield extends Entity
{
	private InstanceEntity instanceEntity;
	
	private boolean activate;
	private int numLives;
	
	public Shield(InstanceEntity instance, int lives)
	{
		instanceEntity = instance;
		activate = false;
		numLives = lives;
		
		if (instanceEntity.getTypeEntity() == TTypeEntity.Character)
		{
			typeEntity = TTypeEntity.CharacterShield;
		}
		else if (instanceEntity.getTypeEntity() == TTypeEntity.Boss)
		{
			typeEntity = TTypeEntity.BossShield;
		}
		else
		{
			typeEntity = TTypeEntity.Nothing;
		}
	}
	
	private int indiceBurbuja(int vidas)
	{
		if (typeEntity == TTypeEntity.CharacterShield)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_character_1;
				case 1:
					return R.drawable.bubble_character_2;
				default:
					return R.drawable.bubble_character_3;
			}
		}
		else if (typeEntity == TTypeEntity.BossShield)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_boss_1;
				case 1:
					return R.drawable.bubble_boss_2;
				default:
					return R.drawable.bubble_boss_3;
			}
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
			{
				renderer.loadTextureRectangle(gl, instanceEntity.getHeight(), instanceEntity.getWidth(), indiceBurbuja(i), typeEntity, i, TTypeSticker.Nothing);
			}
			
			width = instanceEntity.getWidth();
			height = instanceEntity.getHeight();
		}
	}
	
	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
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
			if (activate && numLives > 0)
			{
				gl.glPushMatrix();
				
					gl.glTranslatef(instanceEntity.getCoordX(), instanceEntity.getCoordY(), 0.0f);
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
					renderer.drawTextureRectangle(gl, typeEntity, numLives - 1, TTypeSticker.Nothing);
				
				gl.glPopMatrix();
			}
		}
	}
	
	/* Métodos de Modificación de Información */
	
	public void activate()
	{
		activate = true;
	}
	
	public void deactivate()
	{
		activate = false;
	}
	
	public void restoreLifes(int lives)
	{
		numLives = lives;
	}
	
	public void loseLife()
	{
		numLives--;
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean isAlive()
	{
		return numLives >= 0;
	}
	
	public int getLives()
	{
		return numLives;
	}
}
