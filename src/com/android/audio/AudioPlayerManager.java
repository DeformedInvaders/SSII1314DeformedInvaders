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
	private TPlayEstado estado;

	/* SECTION Constructora */

	public AudioPlayerManager(ExternalStorageManager externalManager)
	{
		manager = externalManager;

		player = new MediaPlayer();
		player.setOnCompletionListener(this);

		estado = TPlayEstado.Libre;
	}
	
	public AudioPlayerManager(Context context)
	{
		mContext = context;
		
		estado = TPlayEstado.Libre;
	}

	/* SECTION Métodos Abstractos */

	public abstract void onPlayerCompletion();

	/* SECTION Métodos de Selección de Estado */
	
	private boolean startPlayingAudio(String path)
	{
		try
		{
			if (estado == TPlayEstado.Libre)
			{
				// Idle
				player.setDataSource(path);
				// Initialized
				player.prepare();
				// Prepared
				player.start();
				// Started

				estado = TPlayEstado.Reproduciendo;
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
		if(manager == null)
		{
			if (estado != TPlayEstado.Libre)
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

			estado = TPlayEstado.Reproduciendo;
			return true;
			
		}
		return false;
	}

	public boolean startPlaying(String nombre, String movimiento)
	{
		if (manager != null && manager.existeFicheroAudio(nombre, movimiento))
		{
			return startPlayingAudio(manager.cargarAudio(nombre, movimiento));
		}

		return false;
	}

	public boolean startPlaying(String nombre)
	{
		if (manager != null && manager.existeFicheroTemp(nombre))
		{
			return startPlayingAudio(manager.cargarAudioTemp(nombre));
		}

		return false;
	}

	public boolean pausePlaying()
	{
		if (estado == TPlayEstado.Reproduciendo)
		{
			// Started
			player.pause();
			// Pause
			estado = TPlayEstado.Pausado;
			return true;
		}

		return false;
	}

	public boolean resumePlaying()
	{
		if (estado == TPlayEstado.Pausado)
		{
			// Pause
			player.start();
			// Started
			estado = TPlayEstado.Reproduciendo;
			return true;
		}

		return false;
	}

	public boolean stopPlaying()
	{
		if (estado == TPlayEstado.Reproduciendo || estado == TPlayEstado.Pausado)
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
		// Any
		player.reset();
		// Idle
		estado = TPlayEstado.Libre;
	}

	public void releasePlayer()
	{
		player.release();
	}

	/* SECTION Métodos de Obtención de Información */

	public boolean isPlaying()
	{
		return estado == TPlayEstado.Reproduciendo;
	}

	public boolean isPaused()
	{
		return estado == TPlayEstado.Pausado;
	}

	public boolean isStoped()
	{
		return estado == TPlayEstado.Libre;
	}

	/* SECTION Métodos Listener onCompletition */

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		resetPlaying();

		onPlayerCompletion();
	}
}
