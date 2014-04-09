package com.android.audio;

import java.io.IOException;

import android.media.MediaRecorder;

import com.android.storage.ExternalStorageManager;

public class AudioRecorderManager
{
	private ExternalStorageManager manager;

	private MediaRecorder recorder;
	private TEstadoRecord estado;

	/* Constructora */

	public AudioRecorderManager(ExternalStorageManager manager)
	{
		this.manager = manager;

		this.recorder = new MediaRecorder();

		this.estado = TEstadoRecord.Parado;
	}

	/* Métodos de Selección de Estado */

	public boolean startRecording(String nombre)
	{
		try
		{
			if (estado == TEstadoRecord.Parado)
			{
				// Initial
				recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// Initialized
				recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				// DataSourceConfigured
				recorder.setOutputFile(manager.guardarAudioTemp(nombre));
				// DataSourceConfigured
				recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				// DataSourceConfigured
				recorder.prepare();
				// Prepared
				recorder.start();
				// Recording

				estado = TEstadoRecord.Grabando;
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
		if (estado == TEstadoRecord.Grabando)
		{
			// Recording
			recorder.stop();
			// Initial

			estado = TEstadoRecord.Parado;
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
		return estado == TEstadoRecord.Grabando;
	}

	public boolean isStopped()
	{
		return estado == TEstadoRecord.Parado;
	}
}
