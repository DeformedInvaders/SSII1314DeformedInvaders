package com.android.audio;

import java.io.IOException;

import android.media.MediaRecorder;

import com.android.storage.ExternalStorageManager;

public class AudioRecorderManager
{
	private ExternalStorageManager manager;
	
	private MediaRecorder recorder;
    private TRecordEstado estado;

	public AudioRecorderManager(ExternalStorageManager manager)
	{
		this.manager = manager;
		this.recorder = new MediaRecorder();
		this.estado = TRecordEstado.Parado;
	}
	
	/* Métodos de Selección de Estado */
	
    public void startRecording(String nombre)
    {
        try
        {
        	//Initial
			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			//Initialized
	    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    	//DataSourceConfigured
	    	recorder.setOutputFile(manager.getDirectorioMusica(nombre));
	    	//DataSourceConfigured
	    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    	//DataSourceConfigured
        	recorder.prepare();
        	//Prepared
        	recorder.start();
        	//Recording
        	
        	estado = TRecordEstado.Grabando;
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        }
    }

    public void stopRecording()
    {
    	//Recording
    	recorder.stop();
    	//Initial
  
    	estado = TRecordEstado.Parado;
    }
    
    public void releaseRecorder()
    {
    	recorder.release();
    }
    
    /* Métodos de Obtención de Información */
    
    public boolean isRecording()
    {
    	return estado == TRecordEstado.Grabando;
    }
    
    public boolean isStopped()
    {
    	return estado == TRecordEstado.Parado;
    }
}
