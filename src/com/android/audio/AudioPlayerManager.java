package com.android.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public abstract class AudioPlayerManager implements OnCompletionListener
{
	private Context mContext;

	private MediaPlayer player;
	private TStatePlayer estado;

	/* Constructora */
	
	public AudioPlayerManager(Context context)
	{
		mContext = context;
		
		player = new MediaPlayer();
		player.setOnCompletionListener(this);
		
		estado = TStatePlayer.Free;
	}

	/* Métodos Abstractos */

	public abstract void onPlayerCompletion();

	/* Métodos de Selección de Estado */
	
	public boolean startPlaying(String path, boolean loop, boolean blockable)
	{
		if (blockable && estado != TStatePlayer.Free)
		{
			return false;
		}
		
		if (estado != TStatePlayer.Free)
		{
			resetPlaying();
		}
		
		try
		{
			// Idle
			player.setDataSource(path);
			// Initialized
			player.prepare();
			// Prepared
			player.start();
			// Started

			estado = TStatePlayer.Playing;
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean startPlaying(int path, boolean loop, boolean blockable)
	{
		if (blockable && estado != TStatePlayer.Free)
		{
			return false;
		}
		
		if (estado != TStatePlayer.Free)
		{
			resetPlaying();
		}
		
		AssetFileDescriptor file = mContext.getResources().openRawResourceFd(path);
		try
		{
			// Idle
			player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getDeclaredLength());
			player.setLooping(loop);
			// Initialized
			player.prepare();
			// Prepared
			player.start();
			// Started

			file.close();
			estado = TStatePlayer.Playing;
			return true;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean pausePlaying()
	{
		if (estado == TStatePlayer.Playing)
		{
			// Started
			player.pause();
			// Pause
			estado = TStatePlayer.Paused;
			return true;
		}

		return false;
	}

	public boolean resumePlaying()
	{
		if (estado == TStatePlayer.Paused)
		{
			// Pause
			player.start();
			// Started
			estado = TStatePlayer.Playing;
			return true;
		}

		return false;
	}

	public boolean stopPlaying()
	{		
		if (estado == TStatePlayer.Playing || estado == TStatePlayer.Paused)
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
		estado = TStatePlayer.Free;
	}

	public void releasePlayer()
	{
		player.release();
	}

	/* Métodos de Obtención de Información */

	public boolean isPlaying()
	{
		return estado == TStatePlayer.Playing;
	}

	public boolean isPaused()
	{
		return estado == TStatePlayer.Paused;
	}

	public boolean isStoped()
	{
		return estado == TStatePlayer.Free;
	}

	/* Métodos Listener onCompletition */

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		resetPlaying();

		onPlayerCompletion();
	}
}
