package com.video.video;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.video.data.Video;

public class VideoOpenGLRenderer extends OpenGLRenderer
{
	public VideoOpenGLRenderer(Context context, Video video)
	{
		super(context, TTipoFondoRenderer.Intercambiable, TTipoTexturasRenderer.Video);
		
		seleccionarTexturaFondo(video.getIdTexturaFondos());
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		animarFondo();
		return true;
	}
}
