package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Platform extends Entity
{	
	private InstanceEntity instanceEntity;
	private TTypeEntity typeWeapon;
	
	private boolean activate;
	
	private int animationPositionFire;
	private int animationPositionShot;
	private int animationPositionWeapon;
	
	public Platform(InstanceEntity instance)
	{
		instanceEntity = instance;
		activate = false;
		
		animationPositionFire = 0;
		animationPositionShot = 0;
		animationPositionWeapon = 0;
		
		if (instanceEntity.getTypeEntity() == TTypeEntity.Character)
		{
			typeEntity = TTypeEntity.CharacterPlatform;
			typeWeapon = TTypeEntity.CharacterWeapon;
		}
		else if (instanceEntity.getTypeEntity() == TTypeEntity.Boss)
		{
			typeEntity = TTypeEntity.BossPlatform;
			typeWeapon = TTypeEntity.BossWeapon;
		}
		else
		{
			typeEntity = TTypeEntity.Nothing;
			typeWeapon = TTypeEntity.Nothing;
		}
	}
	
	private int platformIndex(int index)
	{
		if (typeEntity == TTypeEntity.CharacterPlatform)
		{
			switch (index)
			{
				case 0:
					return R.drawable.platform_character_1;
				case 1:
					return R.drawable.platform_character_2;
				default:
					return R.drawable.platform_character_3;
			}
		}
		else if (typeEntity == TTypeEntity.BossPlatform)
		{
			switch (index)
			{
				case 0:
					return R.drawable.platform_boss_1;
				case 1:
					return R.drawable.platform_boss_2;
				default:
					return R.drawable.platform_boss_3;
			}
		}
		
		return -1;
	}
	
	private int weaponIndex(int index)
	{
		if (typeWeapon == TTypeEntity.CharacterWeapon)
		{
			switch (index)
			{
				case 0:
					return R.drawable.weapon_character_1;
				case 1:
					return R.drawable.weapon_character_2;
				case 2:
					return R.drawable.weapon_character_3;
				default:
					return R.drawable.weapon_character_4;
			}
		}
		else if (typeWeapon == TTypeEntity.BossWeapon)
		{
			switch (index)
			{
				case 0:
					return R.drawable.weapon_boss_1;
				case 1:
					return R.drawable.weapon_boss_2;
				case 2:
					return R.drawable.weapon_boss_3;
				default:
					return R.drawable.weapon_boss_4;
			}
		}
		
		return -1;
	}
	
	private int weaponIndex()
	{
		if (animationPositionShot == 0)
		{
			return animationPositionWeapon;
		}
		else
		{
			return animationPositionWeapon + GamePreferences.NUM_TYPE_TEXTURE_WEAPONS;
		}
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			if (height == 0 && width == 0)
			{
				width = instanceEntity.getWidth();
				height = instanceEntity.getHeight();
			}
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
			{
				renderer.loadTextureRectangle(gl, height, width, weaponIndex(i), typeWeapon, i, TTypeSticker.Nothing);
			}
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
			{
				renderer.loadTextureRectangle(gl, height, width, platformIndex(i), typeEntity, i, TTypeSticker.Nothing);
			}
		}
	}
	
	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		if (typeEntity != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_WEAPONS; i++)
			{
				renderer.deleteTextureRectangle(typeWeapon, i, TTypeSticker.Nothing);
			}
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_PLATFORMS; i++)
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
				if (animationPositionWeapon == 0)
				{		
					// Plataforma	
					gl.glPushMatrix();
						
						gl.glTranslatef(instanceEntity.getCoordX(), instanceEntity.getCoordY() - 5.0f * instanceEntity.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
						renderer.drawTextureRectangle(gl, typeEntity, animationPositionFire, TTypeSticker.Nothing);
					
					gl.glPopMatrix();	
					
					// Arma Trasera
					gl.glPushMatrix();
						
						gl.glTranslatef(instanceEntity.getCoordX(), instanceEntity.getCoordY() - instanceEntity.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
						renderer.drawTextureRectangle(gl, typeWeapon, weaponIndex(), TTypeSticker.Nothing);
					
					gl.glPopMatrix();
				}
				else
				{
					// Arma Frontal
					gl.glPushMatrix();
						
						gl.glTranslatef(instanceEntity.getCoordX(), instanceEntity.getCoordY() - instanceEntity.getHeight() / 8.0f, 0.0f);
						gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
						renderer.drawTextureRectangle(gl, typeWeapon, weaponIndex(), TTypeSticker.Nothing);
						
					gl.glPopMatrix();
				}
				
				animationPositionWeapon = (animationPositionWeapon + 1) % GamePreferences.NUM_TYPE_TEXTURE_WEAPONS;
			}
		}
	}
	
	/* Métodos de modificación de Información */
	
	public boolean animateTexture()
	{
		animationPositionFire = (animationPositionFire + 1) % GamePreferences.NUM_TYPE_PLATFORMS;
		
		if (animationPositionShot > 0)
		{
			animationPositionShot--;
		}
		
		return true;
	}
	
	public void shoot()
	{
		animationPositionShot = GamePreferences.NUM_FRAMES_SHOT;
	}
	
	public void activate()
	{
		activate = true;
	}
	
	public void deactivate()
	{
		activate = false;
	}
}
