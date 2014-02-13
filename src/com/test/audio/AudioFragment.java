package com.test.audio;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.main.R;

public class AudioFragment extends Fragment
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    
    private boolean mStartRecording, mStartPlaying;
    
	private ImageButton botonRecord, botonPlay, botonVolumenMas, botonVolumenMenos;
	
	public static final AudioFragment newInstance()
	{
		AudioFragment fragment = new AudioFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_audio_layout, container, false);
		
		mStartRecording = true;
		mStartPlaying = true;
		
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";

		botonRecord = (ImageButton) rootView.findViewById(R.id.imageButtonAudio1);
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonAudio2);
		botonVolumenMas = (ImageButton) rootView.findViewById(R.id.imageButtonAudio3);
		botonVolumenMenos = (ImageButton) rootView.findViewById(R.id.imageButtonAudio4);
		
		botonRecord.setOnClickListener(new OnRecordClickListener());
		botonPlay.setOnClickListener(new OnPlayClickListener());
		botonVolumenMas.setOnClickListener(new OnVolumenMasClickListener());
		botonVolumenMenos.setOnClickListener(new OnVolumenMenosClickListener());
		
        return rootView;
    }
	
    @Override
    public void onPause()
    {
        super.onPause();
        if (mRecorder != null)
        {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null)
        {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	private void onRecord(boolean start)
	{
        if (start)
        {
            startRecording();
        }
        else
        {
            stopRecording();
        }
    }

    private void onPlay(boolean start)
    {
        if (start)
        {
            startPlaying();
        }
        else
        {
            stopPlaying();
        }
    }

    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        
        try
        {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying()
    {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording()
    {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try
        {
            mRecorder.prepare();
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
	
	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			onRecord(mStartRecording);
			
            if (mStartRecording)
            {
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_stop);
            }
            else
            {
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_record);
            }
            
            mStartRecording = !mStartRecording;
		}
	}
	
	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            onPlay(mStartPlaying);
            
            if (mStartPlaying)
            {
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_pause);
            }
            else
            {
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_play);
            }
            
            mStartPlaying = !mStartPlaying;
			
		}
	}
	
	private class OnVolumenMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	private class OnVolumenMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			// TODO Auto-generated method stub
			
		}
	}
}
