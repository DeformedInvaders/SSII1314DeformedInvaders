package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeSticker;
import com.game.data.TTypeEntity;
import com.lib.buffer.Dimensions;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TStateVideo;

public class AnimatedObject extends InanimatedObject
{
	private TTypeAnimation animationType;
	
	private int[] animationTexture;
	private int animationPositionPressed, animationPositionStep, animationPositionCyclic;
	private int animationFrames;
	
	protected int animationFramesWait, animationFramesLength;
	
	private boolean activate;
	
	public AnimatedObject(int id, int[] textures, float x, float y, TStateVideo state, int sound, TTypeAnimation animation)
	{
		super(id, -1, x, y, state, sound);
		
		typeEntity = TTypeEntity.AnimatedObject;
		animationType = animation;
		
		animationTexture = textures;
		animationPositionPressed = 0;
		animationPositionStep = 0;
		animationPositionCyclic = 0;
				
		animationFramesWait = GamePreferences.NUM_FRAMES_CYCLE;
		animationFramesLength = GamePreferences.NUM_FRAMES_ANIMATION;
		
		animationFrames = animationFramesWait;
		
		activate = false;
	}
	
	@Override
	protected int objectIndex()
	{
		if (animationType == TTypeAnimation.Pressed)
		{
			if (activate)
			{
				return objectIndex(animationPositionPressed % GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS);
			}
			else
			{
				return objectIndex(0);
			}
		}
		else if (animationType == TTypeAnimation.Cyclic)
		{
			return objectIndex(animationPositionCyclic);
		}
		else
		{
			return objectIndex(animationPositionStep);
		}
	}
	
	private int objectIndex(int indice)
	{
		return idEntity * GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS + indice;
	}
	
	/* Métodos Abstráctos de Entidad */
	
	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS; i++)
		{
			Dimensions dim = renderer.loadTextureRectangle(gl, animationTexture[i], typeEntity, objectIndex(i), TTypeSticker.Nothing);
			if (dim != null)
			{
				width = dim.getWidth();
				height = dim.getHeight();
			}
		}
		
		area = new Rectangle(coordX, coordY, getWidth(), getHeight());
		handle = new Handle(area.getX(), area.getY(), area.getWidth(), area.getHeight(), Color.RED);
	}

	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS; i++)
		{
			renderer.deleteTextureRectangle(typeEntity, objectIndex(i), TTypeSticker.Nothing);
		}
	}

	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		super.drawTexture(gl, renderer);
		
		animationFrames--;
		
		if (animationFrames == 0)
		{
			animationFrames = animationFramesWait;
			
			if (activate)
			{
				if (animationPositionPressed > 0)
				{
					animationPositionPressed--;
				}
				else
				{
					activate = false;
				}
			}
			
			animationPositionCyclic = (animationPositionCyclic + 1) % GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS;
		}
	}
	
	/* Métodos Públicos */
	
	@Override
	public boolean contains(float x, float y)
	{
		if (area == null)
		{
			return false;
		}
		
		if (area.contains(x, y))
		{
			if (!activate)
			{
				activate = true;
				animationFrames = animationFramesWait;
				animationPositionPressed = animationFramesLength;
			}
		
			if (animationPositionStep < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS - 1)
			{
				animationPositionStep++;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void stopAnimation()
	{
		super.stopAnimation();
		
		animationPositionPressed = 0;
		animationPositionStep = 0;
		animationPositionCyclic = 0;
		animationFrames = animationFramesWait;;
		activate = false;
	}
}
