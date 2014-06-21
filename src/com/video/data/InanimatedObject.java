package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeSticker;
import com.game.data.Entity;
import com.game.data.TTypeEntity;
import com.lib.buffer.Dimensions;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TStateVideo;

public class InanimatedObject extends Entity
{
	protected TStateVideo stateActive;
	protected int soundActive;
	
	protected float coordX, coordY, factor;
	
	protected Rectangle area;
	protected Handle handle;
	
	public InanimatedObject(int id, int textura, float x, float y, TStateVideo state, int sound)
	{
		idEntity = id;
		typeEntity = TTypeEntity.InanimatedObject;
		textureEntity = textura;
		
		coordX = x * GamePreferences.SCREEN_WIDTH_SCALE_FACTOR();
		coordY = y * GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
		
		factor = 1.0f;

		stateActive = state;
		soundActive = sound;
	}
	
	protected int objectIndex()
	{
		return idEntity;
	}
	
	/* Métodos Abstráctos de Entidad */
	
	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensions dim = renderer.loadTextureRectangle(gl, textureEntity, typeEntity, idEntity, TTypeSticker.Nothing);
		if (dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
		
		area = new Rectangle(coordX, coordY, getWidth(), getHeight());
		handle = new Handle(area.getX(), area.getY(), area.getWidth(), area.getHeight(), Color.BLUE);
	}

	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		renderer.deleteTextureRectangle(typeEntity, idEntity, TTypeSticker.Nothing);
	}

	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(coordX, coordY, 0.0f);
			gl.glScalef(GamePreferences.SCREEN_WIDTH_SCALE_FACTOR(), GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR(), 1.0f);
			gl.glScalef(factor, factor, 1.0f);
			renderer.drawTextureRectangle(gl, typeEntity, objectIndex(), TTypeSticker.Nothing);
		
		gl.glPopMatrix();
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			handle.dibujar(gl);
		}
	}
	
	@Override
	public float getHeight()
	{
		return height * GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	@Override
	public float getWidth()
	{
		return width * GamePreferences.SCREEN_WIDTH_SCALE_FACTOR();
	}
	
	/* Métodos Públicos */
	
	public boolean contains(float x, float y)
	{
		if (area == null)
		{
			return false;
		}
		
		return area.contains(x, y);
	}
	
	public TStateVideo getStateActive()
	{
		return stateActive;
	}
	
	public int getSoundActive()
	{
		return soundActive;
	}
	
	public void stopAnimation()
	{
		
	}
}
