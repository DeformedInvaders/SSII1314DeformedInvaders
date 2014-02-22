package com.android.audio;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.android.storage.ExternalStorageManager;

public abstract class AudioPlayerManager implements OnCompletionListener
{
	private ExternalStorageManager manager;

	private MediaPlayer player;
    private TPlayEstado estado;
    
    public AudioPlayerManager(ExternalStorageManager manager)
    {
    	this.manager = manager;
    	
    	this.player = new MediaPlayer();
		this.player.setOnCompletionListener(this);
		
		this.estado = TPlayEstado.Parado;
    }
    
    public abstract void onPlayerCompletion();
    
    /* Métodos de Selección de Estado */
    
    public void startPlaying(String nombre)
    {			
		try
		{
			//Idle
			player.setDataSource(manager.cargarAudioTemp(nombre));
			//Initialized
			player.prepare();
			//Prepared
		    player.start();
		    //Started
		    
		    estado = TPlayEstado.Reproduciendo;
		}
		catch (IOException e)
		{
		    e.printStackTrace();
		}
    }
    
    public void pausePlaying()
    {
    	//Started
    	player.pause();
    	//Pause
    	estado = TPlayEstado.Pausado;
    }

    public void resumePlaying()
    {
    	//Pause
    	player.start();
    	//Started
    	estado = TPlayEstado.Reproduciendo;
    }
    
    public void stopPlaying()
    {
    	//Started or Paused
    	player.stop();
    	//Stopped
    	estado = TPlayEstado.Parado;
    }
    
    public void resetPlaying()
    {
    	//Any
    	player.reset();
    	//Idle
    	estado = TPlayEstado.Parado;
    }
    
    public void releasePlayer()
    {
    	player.release();
    }
    
	@Override
	public void onCompletion(MediaPlayer mp)
	{
		resetPlaying();
		
		onPlayerCompletion();
	}
	
	/* Métodos de Obtención de Información */
	
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
		return estado == TPlayEstado.Parado;
	}
}
