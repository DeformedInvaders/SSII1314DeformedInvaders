package com.video.video;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.main.model.GamePreferences;
import com.video.data.Video;

public class VideoOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnVideoListener mListener;
	
	private TEstadoVideo estado;
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
		super(context, attrs, TEstadoDetector.SimpleTouch, false);
	}

	public void setParameters(OnVideoListener listener, Video v)
	{		
		mListener = listener;
		
		video = v;
		estado = TEstadoVideo.Nada;
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
					
					if (tiempoActual - tiempoInicio >= tiempoDuracion)
					{
						estado = estado.getNext();							
						mensajes = video.getMensaje(estado);
						
						animarCambioEscena();
						
						long duration = estado.getDuration();
						int music = estado.getMusic();
						int sound = estado.getSound();
						
						if (sound != -1)
						{
							mListener.onPlaySoundEffect(sound);
						}
						
						if (music != -1)
						{
							mListener.onPlayMusic(music);
						}
						
						if (duration != -1)
						{
							tiempoInicio = System.currentTimeMillis();
							tiempoDuracion = duration;
						}
					}
					
					animarCicloEscena();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION());
				}
				else
				{
					mListener.onDismissDialog();
					mListener.onVideoFinished();
				}
			}
		};
	}
	
	private void animarCicloEscena()
	{
		if (estado == TEstadoVideo.Door)
		{
			renderer.acercarEscena();
		}
		
		renderer.animarEscena();
		requestRender();
	}
	
	private void animarCambioEscena()
	{
		mListener.onDismissDialog();
		renderer.seleccionarEstado(estado);
		
		if (estado == TEstadoVideo.Rock)
		{
			renderer.recuperarEscena();
		}
		else if (estado == TEstadoVideo.Noise)
		{	
			mListener.onChangeDialog(mensajes[posMensaje]);
		}
		else if (estado == TEstadoVideo.Brief)
		{
			mListener.onChangeDialog(mensajes[posMensaje]);
			posMensaje++;
			threadActivo = posMensaje != mensajes.length;
		}
		
		renderer.seleccionarEstado(estado);
		renderer.avanzarEscena();
		requestRender();
	}
	
	public void iniciarVideo()
	{
		if (!threadActivo)
		{
			threadActivo = true;
			task.run();
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
				mListener.onPlaySoundEffect(sonido);
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
