package com.test.audio;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.main.R;

public class AudioFragment extends Fragment implements OnCompletionListener
{
    private static String nombreFichero;

    private MediaRecorder recorder;
    private MediaPlayer player;
    private AudioManager audio;
    
    private boolean grabar, reproducir, ficheroCreado;
    
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
		
		recorder = new MediaRecorder();
		player = new MediaPlayer();
		audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		
		nombreFichero = Environment.getExternalStorageDirectory().getAbsolutePath();
		nombreFichero += "/audiorecordtest.3gp";
		
		grabar = true;
		reproducir = true;
		ficheroCreado = new File(nombreFichero).exists();

		botonRecord = (ImageButton) rootView.findViewById(R.id.imageButtonAudio1);
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonAudio2);
		botonVolumenMas = (ImageButton) rootView.findViewById(R.id.imageButtonAudio3);
		botonVolumenMenos = (ImageButton) rootView.findViewById(R.id.imageButtonAudio4);
		
		botonRecord.setOnClickListener(new OnRecordClickListener());
		botonPlay.setOnClickListener(new OnPlayClickListener());
		botonVolumenMas.setOnClickListener(new OnVolumenMasClickListener());
		botonVolumenMenos.setOnClickListener(new OnVolumenMenosClickListener());
		
		player.setOnCompletionListener(this);
		
		getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
		actualizarBotones();
		
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		recorder = null;
		player = null;
		audio = null;
		
		botonRecord = null;
		botonPlay = null;
		botonVolumenMas = null;
		botonVolumenMenos = null;
	}
	
    @Override
    public void onPause()
    {
        super.onPause();
        
    	recorder.release();
    	player.release();
    }
    
    /* Reproducción */

    private void startPlaying()
    {
        try
        {
        	player.setDataSource(nombreFichero);
            player.prepare();
            player.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void stopPlaying()
    {
    	player.release();
    	player = null;
    }

    /* Grabación */
    
    private void startRecording()
    {
    	recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    	recorder.setOutputFile(nombreFichero);
    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try
        {
        	recorder.prepare();
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        }

        recorder.start();
    }

    private void stopRecording()
    {
    	recorder.stop();
    	recorder.release();
    	
    	ficheroCreado = true;
    }
    
    /* Listeners de Botones */
	
	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            if (grabar)
            {
            	startRecording();
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_stop);
            }
            else
            {
            	stopRecording();
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_record);
            }
            
            grabar = !grabar;
		}
	}
	
	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            if (reproducir)
            {
            	startPlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_pause);
            }
            else
            {
            	stopPlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_play);
            }
            
            reproducir = !reproducir;
			
		}
	}
	
	private void actualizarBotones()
	{
		// Reproducir
		if(ficheroCreado)
		{
			botonPlay.setVisibility(View.VISIBLE);
		}
		else
		{
			botonPlay.setVisibility(View.INVISIBLE);
		}
		
		// Volumen
		
		if(audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
		{
			botonVolumenMas.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonVolumenMas.setVisibility(View.VISIBLE);
		}
		
		if(audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
		{
			botonVolumenMenos.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonVolumenMenos.setVisibility(View.VISIBLE);
		}
	}
	
	private class OnVolumenMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
			actualizarBotones();
		}
	}
	
	private class OnVolumenMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
			actualizarBotones();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		stopPlaying();
	    botonPlay.setBackgroundResource(R.drawable.icon_audio_play);
	    reproducir = true;
	}
}
