package com.video.video;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.main.model.GamePreferences;
import com.video.data.Video;

public class VideoOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnVideoListener mListener;
	
	private TStateVideo estado;
	private Video video;
	
	private VideoOpenGLRenderer renderer;
	
	private boolean threadActivo;
	private Handler handler;
	private Runnable task;
	
	private int[] mensajes;
	private int posMensaje;
	
	private long tiempoInicio, tiempoDuracion;

	/* Constructora */

	public VideoOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);
	}

	public void setParameters(OnVideoListener listener, Video v)
	{		
		mListener = listener;
		
		video = v;
		estado = TStateVideo.Nothing;
		threadActivo = false;
		
		posMensaje = 0;
		
		tiempoInicio = System.currentTimeMillis();
		tiempoDuracion = 0;
		
		// Asignar Renderer al GLSurfaceView
		renderer = new VideoOpenGLRenderer(getContext(), video);
		setRenderer(renderer);

		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (threadActivo)
				{
					long tiempoActual = System.currentTimeMillis();
					
					// Fin de Ciclo de Escena
					if (tiempoActual - tiempoInicio >= tiempoDuracion)
					{
						// Fin de Video
						if (estado == TStateVideo.Space)
						{
							threadActivo = false;
							
							mListener.onDismissDialog();
							mListener.onVideoFinished();
						}
						
						// Fin de Escena
						if (mensajes == null || posMensaje >= mensajes.length)
						{
							estado = estado.getNext();
							
							mListener.onDismissDialog();
							mensajes = video.getQuote(estado);
							posMensaje = 0;
							
							renderer.seleccionarEstado(estado);
							
							if (estado != TStateVideo.Outside)
							{
								renderer.avanzarEscena();
								requestRender();
							}
							
							long duration = estado.getDuration();
							int music = estado.getMusic();
							int sound = estado.getSound();
							
							if (sound != -1)
							{
								mListener.onPlayVoice(sound);
							}
							
							if (music != -1)
							{
								mListener.onPlayMusic(music);
							}
							
							if (duration != -1)
							{
								tiempoInicio = tiempoActual;
								tiempoDuracion = duration;
							}
						}
						
						if (mensajes != null)
						{
							mListener.onChangeDialog(mensajes[posMensaje], estado);
							tiempoInicio = tiempoActual;
							posMensaje++;
							
							int sound = estado.getSound();
							
							if (sound != -1)
							{
								mListener.onPlayVoice(sound);
							}
						}		
					}
					
					// Cambio de Ciclo de Escena
					if (estado == TStateVideo.Door)
					{
						renderer.acercarEscena(0.999f);
					}
					
					renderer.animarEscena();
					requestRender();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION_VIDEO());
				}
			}
		};
	}
	
	public void seleccionarResume()
	{
		if (!threadActivo)
		{
			threadActivo = true;
			task.run();
		}
	}

	public void seleccionarPause()
	{
		if (threadActivo)
		{
			threadActivo = false;
			handler.removeCallbacks(task);
		}
	}
	
	/* Métodos Abstráctos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		if (renderer.onTouchUp(x, y, width, height, pos))
		{
			int sonido = renderer.getSonidoActivado();
			if (sonido != -1)
			{
				mListener.onPlaySoundEffect(sonido, true);
				renderer.desactivarEstadoSonido();
				return true;
			}
		}
		
		return false;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		renderer.saveData();
	}
}
