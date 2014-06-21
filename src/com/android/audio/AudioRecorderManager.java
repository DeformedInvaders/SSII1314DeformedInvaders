package com.android.audio;

import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorderManager
{
	private MediaRecorder recorder;
	private TStateRecorder estado;

	/* Constructora */

	public AudioRecorderManager()
	{
		recorder = new MediaRecorder();

		estado = TStateRecorder.Stopped;
	}

	/* Métodos de Selección de Estado */

	public boolean startRecording(String path)
	{
		try
		{
			if (estado == TStateRecorder.Stopped)
			{
				// Initial
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// Initialized
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				// DataSourceConfigured
				recorder.setOutputFile(path);
				// DataSourceConfigured
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				// DataSourceConfigured
				recorder.prepare();
				// Prepared
				recorder.start();
				// Recording

				estado = TStateRecorder.Recording;
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	public boolean stopRecording()
	{
		if (estado == TStateRecorder.Recording)
		{
			// Recording
			recorder.stop();
			// Initial

			estado = TStateRecorder.Stopped;
			return true;
		}

		return false;
	}

	public void releaseRecorder()
	{
		recorder.release();
	}

	/* Métodos de Obtención de Información */

	public boolean isRecording()
	{
		return estado == TStateRecorder.Recording;
	}

	public boolean isStopped()
	{
		return estado == TStateRecorder.Stopped;
	}
}
