package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.main.model.GamePreferences;
import com.video.video.TStateVideo;

public class MovingObject extends AnimatedObject
{
	private float coordXOriginal, coordYOriginal, factorOriginal;
	
	public MovingObject(int id, int[] textures, float x, float y, TStateVideo state, int sound, TTypeAnimation animation)
	{
		super(id, textures, x, y, state, sound, animation);
		
		coordXOriginal = x;
		coordYOriginal = y;
		factorOriginal = 1.0f;
		
		animationFramesWait = 1;
	}

	@Override
	public void stopAnimation()
	{
		super.stopAnimation();
		
		coordX = coordXOriginal;
		coordY = coordYOriginal;
		factor = factorOriginal;
	}
	
	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		coordY = coordY + GamePreferences.DIST_MOVIMIENTO_SPACESHIP();
		factor = factor * 0.998f;
		
		super.drawTexture(gl, renderer);
	}
}
