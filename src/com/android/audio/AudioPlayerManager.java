package com.android.audio;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.android.storage.ExternalStorageManager;

public abstract class AudioPlayerManager implements OnCompletionListener
{
	private Context mContext;
	private ExternalStorageManager manager;

	private MediaPlayer player;
	private TEstadoPlay estado;

	/* Constructora */

	public AudioPlayerManager(ExternalStorageManager externalManager)
	{
		manager = externalManager;

		player = new MediaPlayer();
		player.setOnCompletionListener(this);

		estado = TEstadoPlay.Libre;
	}
	
	public AudioPlayerManager(Context context)
	{
		mContext = context;
		
		estado = TEstadoPlay.Libre;
	}

	/* Métodos Abstractos */

	public abstract void onPlayerCompletion();

	/* Métodos de Selección de Estado */
	
	private boolean startPlayingAudio(String path)
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		try
		{
			if (estado == TEstadoPlay.Libre)
			{
				// Idle
				player.setDataSource(path);
				// Initialized
				player.prepare();
				// Prepared
				player.start();
				// Started

				estado = TEstadoPlay.Reproduciendo;
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean startPlaying(int path, boolean loop)
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if(manager == null)
		{
			if (estado != TEstadoPlay.Libre)
			{
				resetPlaying();
			}
			
			player = MediaPlayer.create(mContext, path);
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setLooping(loop);
			player.setOnCompletionListener(this);
			
			// Prepared
			player.start();
			// Started

			estado = TEstadoPlay.Reproduciendo;
			return true;
			
		}
		
		return false;
	}

	public boolean startPlaying(String nombre, String movimiento)
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if (manager != null && manager.existeFicheroAudio(nombre, movimiento))
		{
			return startPlayingAudio(manager.cargarAudio(nombre, movimiento));
		}

		return false;
	}

	public boolean startPlaying(String nombre)
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if (manager != null && manager.existeFicheroTemp(nombre))
		{
			return startPlayingAudio(manager.cargarAudioTemp(nombre));
		}

		return false;
	}

	public boolean pausePlaying()
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if (estado == TEstadoPlay.Reproduciendo)
		{
			// Started
			player.pause();
			// Pause
			estado = TEstadoPlay.Pausado;
			return true;
		}

		return false;
	}

	public boolean resumePlaying()
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if (estado == TEstadoPlay.Pausado)
		{
			// Pause
			player.start();
			// Started
			estado = TEstadoPlay.Reproduciendo;
			return true;
		}

		return false;
	}

	public boolean stopPlaying()
	{
		if (estado == TEstadoPlay.Deshabilitado)
		{
			return false;
		}
		
		if (estado == TEstadoPlay.Reproduciendo || estado == TEstadoPlay.Pausado)
		{
			// Started or Paused
			player.stop();
			// Stopped

			resetPlaying();
			return true;
		}

		return false;
	}

	private void resetPlaying()
	{
		player.reset();
		// Idle
		estado = TEstadoPlay.Libre;
	}

	public void releasePlayer()
	{
		player.release();
	}
	
	public void enablePlayer()
	{
		resetPlaying();
	}
	
	public void disablePlayer()
	{
		resetPlaying();
		
		estado = TEstadoPlay.Deshabilitado;
	}

	/* Métodos de Obtención de Información */

	public boolean isPlaying()
	{
		return estado == TEstadoPlay.Reproduciendo;
	}

	public boolean isPaused()
	{
		return estado == TEstadoPlay.Pausado;
	}

	public boolean isStoped()
	{
		return estado == TEstadoPlay.Libre;
	}
	
	public boolean isEnabled()
	{
		return estado != TEstadoPlay.Deshabilitado;
	}

	/* Métodos Listener onCompletition */

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		resetPlaying();

		onPlayerCompletion();
	}
}
