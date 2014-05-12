package com.video.video;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.video.data.Video;

public class VideoOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnVideoListener mListener;
	
	private TEstadoVideo estado;
	
	// Renderer
	private VideoOpenGLRenderer renderer;
	
	private boolean threadActivo;
	private Handler handler;
	private Runnable task;

	/* Constructora */

	public VideoOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch, false);
	}

	public void setParameters(OnVideoListener listener, Video video)
	{		
		mListener = listener;
		
		estado = TEstadoVideo.Nada;
		threadActivo = false;
		
		// Asignar Renderer al GLSurfaceView
		renderer = new VideoOpenGLRenderer(getContext(), video);
		setRenderer(renderer);
		
		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (estado != null)
				{	
					estado = estado.getNext();				
					renderer.avanzarEscena(estado);
					requestRender();
					
					int duration = estado.getDuration();
					int music = estado.getMusic();
					int sound = estado.getSound();

					if (music != -1)
					{
						mListener.onPlayMusic(music);
					}
					
					if (sound != -1)
					{
						mListener.onPlaySoundEffect(sound);
					}
					
					if (duration != -1)
					{
						handler.postDelayed(this, duration);
					}
				}
			}
		};
	}
	
	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		if (!threadActivo)
		{
			task.run();
			threadActivo = true;
		}
		
		return true;
	}
}
