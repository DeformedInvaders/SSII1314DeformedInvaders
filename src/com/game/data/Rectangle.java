package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.lib.buffer.Dimensions;
import com.main.model.GamePreferences;

public abstract class Rectangle extends Entity
{
	/* Métodos abstractos de Entidad */

	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensions dim = renderer.loadTextureRectangle(gl, textureEntity, typeEntity, idEntity, TTypeSticker.Nothing);
		if(dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
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
		
			gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 0.0f);
			
			renderer.drawTextureRectangle(gl, typeEntity, idEntity, TTypeSticker.Nothing);
			
		gl.glPopMatrix();
	}
}
