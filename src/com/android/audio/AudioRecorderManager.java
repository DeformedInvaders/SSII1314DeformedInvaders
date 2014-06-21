package com.android.audio;

import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorderManager
{
	private MediaRecorder audioRecorder;
	private TStateRecorder mState;

	/* Constructora */

	public AudioRecorderManager()
	{
		audioRecorder = new MediaRecorder();

		mState = TStateRecorder.Stopped;
	}

	/* Métodos de Selección de Estado */

	public boolean startRecording(String path)
	{
		try
		{
			if (mState == TStateRecorder.Stopped)
			{
				// Initial
				audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// Initialized
				audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				// DataSourceConfigured
				audioRecorder.setOutputFile(path);
				// DataSourceConfigured
				audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				// DataSourceConfigured
				audioRecorder.prepare();
				// Prepared
				audioRecorder.start();
				// Recording

				mState = TStateRecorder.Recording;
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
		if (mState == TStateRecorder.Recording)
		{
			// Recording
			audioRecorder.stop();
			// Initial

			mState = TStateRecorder.Stopped;
			return true;
		}

		return false;
	}

	public void releaseRecorder()
	{
		audioRecorder.release();
	}

	/* Métodos de Obtención de Información */

	public boolean isRecording()
	{
		return mState == TStateRecorder.Recording;
	}

	public boolean isStopped()
	{
		return mState == TStateRecorder.Stopped;
	}
}
