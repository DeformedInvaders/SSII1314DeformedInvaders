package com.android.audio;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public abstract class AudioPlayerManager implements OnCompletionListener
{
	private Context mContext;

	private MediaPlayer audioPlayer;
	private TStatePlayer mState;

	/* Constructora */
	
	public AudioPlayerManager(Context context)
	{
		mContext = context;
		
		audioPlayer = new MediaPlayer();
		audioPlayer.setOnCompletionListener(this);
		
		mState = TStatePlayer.Free;
	}

	/* Métodos Abstractos */

	public abstract void onPlayerCompletion();

	/* Métodos de Selección de Estado */
	
	public boolean startPlaying(String path, boolean loop, boolean blockable)
	{
		if (blockable && mState != TStatePlayer.Free)
		{
			return false;
		}
		
		if (mState != TStatePlayer.Free)
		{
			resetPlaying();
		}
		
		try
		{
			// Idle
			audioPlayer.setDataSource(path);
			// Initialized
			audioPlayer.prepare();
			// Prepared
			audioPlayer.start();
			// Started

			mState = TStatePlayer.Playing;
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
		if (blockable && mState != TStatePlayer.Free)
		{
			return false;
		}
		
		if (mState != TStatePlayer.Free)
		{
			resetPlaying();
		}
		
		AssetFileDescriptor file = mContext.getResources().openRawResourceFd(path);
		try
		{
			// Idle
			audioPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getDeclaredLength());
			audioPlayer.setLooping(loop);
			// Initialized
			audioPlayer.prepare();
			// Prepared
			audioPlayer.start();
			// Started

			file.close();
			mState = TStatePlayer.Playing;
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
		if (mState == TStatePlayer.Playing)
		{
			// Started
			audioPlayer.pause();
			// Pause
			mState = TStatePlayer.Paused;
			return true;
		}

		return false;
	}

	public boolean resumePlaying()
	{
		if (mState == TStatePlayer.Paused)
		{
			// Pause
			audioPlayer.start();
			// Started
			mState = TStatePlayer.Playing;
			return true;
		}

		return false;
	}

	public boolean stopPlaying()
	{		
		if (mState == TStatePlayer.Playing || mState == TStatePlayer.Paused)
		{
			// Started or Paused
			audioPlayer.stop();
			// Stopped

			resetPlaying();
			return true;
		}

		return false;
	}

	private void resetPlaying()
	{
		audioPlayer.reset();
		// Idle
		mState = TStatePlayer.Free;
	}

	public void releasePlayer()
	{
		audioPlayer.release();
	}

	/* Métodos de Obtención de Información */

	public boolean isPlaying()
	{
		return mState == TStatePlayer.Playing;
	}

	public boolean isPaused()
	{
		return mState == TStatePlayer.Paused;
	}

	public boolean isStoped()
	{
		return mState == TStatePlayer.Free;
	}

	/* Métodos Listener onCompletition */

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		resetPlaying();

		onPlayerCompletion();
	}
}
