package com.video.video;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.main.model.GamePreferences;
import com.video.data.TTipoActores;
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
	
	private int contadorCiclos;

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
		contadorCiclos = 0;
		
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
					if (contadorCiclos == 0)
					{
						estado = estado.getNext();	
						animarCambioEscena();
						
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
							contadorCiclos = duration / GamePreferences.TIME_INTERVAL_ANIMATION();
							android.util.Log.d("TEST", "Ciclos "+contadorCiclos);
						}
						else
						{
							contadorCiclos = -1;
						}
					}
					
					animarCicloEscena();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION());
					
					if (contadorCiclos != -1)
					{
						contadorCiclos--;
					}
				}
			}
		};
	}
	
	private void animarCicloEscena()
	{
		if (estado == TEstadoVideo.Door)
		{
			if (contadorCiclos % 250 == 0)
			{
				renderer.acercarEscena();
			}
		}
		
		renderer.animarEscena();
		requestRender();
	}
	
	private void animarCambioEscena()
	{
		if (estado == TEstadoVideo.Rock)
		{
			renderer.recuperarEscena();
			renderer.activarActor(TTipoActores.Guitarrista, true);
		}
		else if (estado == TEstadoVideo.Noise)
		{
			renderer.activarActor(TTipoActores.Guitarrista, false);			
		}
		else if (estado == TEstadoVideo.Brief)
		{
			renderer.activarActor(TTipoActores.Cientifico, true);
		}
		
		renderer.avanzarEscena();
		requestRender();
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
	
	/* M�todos de Guardado de Informaci�n */

	public void saveData()
	{
		renderer.saveData();
	}
}
