package com.test.audio;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.alert.ChooseAlert;
import com.android.alert.TextInputAlert;
import com.android.storage.ExternalStorageManager;
import com.project.main.R;

public class AudioFragment extends Fragment implements OnCompletionListener
{
	private ExternalStorageManager manager;
	
	private MediaRecorder recorder;
    private MediaPlayer player;
    private AudioManager audio;
    
    private TRecordEstado estadoGrabar;
    private TPlayEstado estadoReproducir;
    
	private ImageButton botonRecord, botonPlay, botonVolumenMas, botonVolumenMenos;
	
	public static final AudioFragment newInstance()
	{
		AudioFragment fragment = new AudioFragment();
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		manager = new ExternalStorageManager();
		
		recorder = new MediaRecorder();
		player = new MediaPlayer();
		audio = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
		
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		estadoGrabar = TRecordEstado.Parado;
		estadoReproducir = TPlayEstado.Parado;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_audio_layout, container, false);

		botonRecord = (ImageButton) rootView.findViewById(R.id.imageButtonAudio1);
		botonPlay = (ImageButton) rootView.findViewById(R.id.imageButtonAudio2);
		botonVolumenMas = (ImageButton) rootView.findViewById(R.id.imageButtonAudio3);
		botonVolumenMenos = (ImageButton) rootView.findViewById(R.id.imageButtonAudio4);
		
		botonRecord.setOnClickListener(new OnRecordClickListener());
		botonPlay.setOnClickListener(new OnPlayClickListener());
		botonVolumenMas.setOnClickListener(new OnVolumenMasClickListener());
		botonVolumenMenos.setOnClickListener(new OnVolumenMenosClickListener());
		
		player.setOnCompletionListener(this);
		
		actualizarBotones();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonRecord = null;
		botonPlay = null;
		botonVolumenMas = null;
		botonVolumenMenos = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		manager = null;
		recorder.release();
		recorder = null;
		player.release();
		player = null;
		audio = null;
	}
    
	private void actualizarBotones()
	{
		// Reproducir
		if(manager.getNumFicherosDirectorioMusica() > 0)
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
    
    /* Reproducción */

    private void startPlaying()
    {
		ChooseAlert alert = new ChooseAlert(getActivity(), getString(R.string.text_audio_play_title), getString(R.string.text_button_play), getString(R.string.text_button_cancel), manager.getFicherosDirectorioMusica()) {
			
			@Override
			public void onSelectedPossitiveButtonClick(String selected)
			{						
				try
				{
					//Idle
					player.setDataSource(manager.getDirectorioMusica(selected));
					//Initialized
					player.prepare();
					//Prepared
				    player.start();
				    //Started
				    
				    estadoReproducir = TPlayEstado.Reproduciendo;
				}
				catch (IOException e)
				{
				    e.printStackTrace();
				}
			}
			
			@Override
			public void onNoSelectedPossitiveButtonClick() { }

			@Override
			public void onNegativeButtonClick() { }
			
		};

		alert.show();
    }
    
    private void pausePlaying()
    {
    	//Started
    	player.pause();
    	//Pause
    	estadoReproducir = TPlayEstado.Pausado;
    }

    private void resumePlaying()
    {
    	//Pause
    	player.start();
    	//Started
    	estadoReproducir = TPlayEstado.Reproduciendo;
    }
    
   /* private void stopPlaying()
    {
    	//Started or Paused
    	player.stop();
    	//Stopped
    	estadoReproducir = TPlayEstado.Parado;
    }*/
    
    private void resetPlaying()
    {
    	//Any
    	player.reset();
    	//Idle
    	estadoReproducir = TPlayEstado.Parado;
    }

    /* Grabación */
    
    private void startRecording()
    {
    	TextInputAlert alert = new TextInputAlert(getActivity(), getString(R.string.text_audio_record_title), getString(R.string.text_audio_record_description), getString(R.string.text_button_record), getString(R.string.text_button_cancel)) {
    		
			@Override
			public void onPossitiveButtonClick()
			{
		        try
		        {
		        	//Initial
					recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					//Initialized
			    	recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			    	//DataSourceConfigured
			    	recorder.setOutputFile(manager.getDirectorioMusica(getText()));
			    	//DataSourceConfigured
			    	recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			    	//DataSourceConfigured
		        	recorder.prepare();
		        	//Prepared
		        	recorder.start();
		        	//Recording
		        	
		        	estadoGrabar = TRecordEstado.Grabando;
		        }
		        catch (IOException e)
		        {
		        	e.printStackTrace();
		        }
			}

			@Override
			public void onNegativeButtonClick() { }
			
		};

		alert.show();
    }

    private void stopRecording()
    {
    	//Recording
    	recorder.stop();
    	//Initial
  
    	estadoGrabar = TRecordEstado.Parado;
    }
    
    /* Listeners de Botones */
	
	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            if(estadoGrabar == TRecordEstado.Parado)
            {
            	startRecording();
            	
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_stop);
            }
            else if(estadoGrabar == TRecordEstado.Grabando)
            {
            	stopRecording();
            	
            	botonRecord.setBackgroundResource(R.drawable.icon_audio_record);
            	Toast.makeText(getActivity(), R.string.text_audio_record_confirmation, Toast.LENGTH_SHORT).show();
            }

            actualizarBotones();
		}
	}
	
	private class OnPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            if(estadoReproducir == TPlayEstado.Parado)
            {
            	startPlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_pause);
            }
            else if(estadoReproducir == TPlayEstado.Reproduciendo)
            {
            	pausePlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_play);
            }
            else if(estadoReproducir == TPlayEstado.Pausado)
            {
            	resumePlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_audio_pause);
            }
            
            actualizarBotones();
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
		resetPlaying();
		
	    botonPlay.setBackgroundResource(R.drawable.icon_audio_play);	    
	    Toast.makeText(getActivity(), R.string.text_audio_play_confirmation, Toast.LENGTH_SHORT).show();
	}
}
