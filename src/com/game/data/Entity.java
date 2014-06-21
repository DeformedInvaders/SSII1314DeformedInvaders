package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.main.model.GamePreferences;

public abstract class Entity
{
	protected TTypeEntity typeEntity;
	protected int idEntity;
	protected int textureEntity;

	protected float width = 0.0f;
	protected float height = 0.0f;

	/* Métodos abstractos a implementar */

	public abstract void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context);

	public abstract void deleteTexture(OpenGLRenderer renderer);

	public abstract void drawTexture(GL10 gl, OpenGLRenderer renderer);

	public boolean animateTexture()
	{ 
		return false;
	}

	/* Métodos públicos */
	
	public int getId()
	{
		return idEntity;
	}

	public TTypeEntity getType()
	{
		return typeEntity;
	}
	
	public int getIndexTexture()
	{
		return textureEntity;
	}
	
	public float getWidth()
	{
		return width * GamePreferences.GAME_SCALE_FACTOR(typeEntity);
	}
	
	public float getHeight()
	{
		return height * GamePreferences.GAME_SCALE_FACTOR(typeEntity);
	}
}
