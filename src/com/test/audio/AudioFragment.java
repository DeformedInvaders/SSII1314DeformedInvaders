package com.test.audio;

import android.app.Activity;
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
import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioRecorderManager;
import com.android.audio.AudioVolumeManager;
import com.android.storage.ExternalStorageManager;
import com.project.main.R;

public class AudioFragment extends Fragment
{
	private ExternalStorageManager manager;
	
	private AudioRecorderManager recorder;
    private AudioPlayerManager player;
    private AudioVolumeManager audio;
    
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
		
		audio = new AudioVolumeManager(activity);
		recorder = new AudioRecorderManager(manager);
		player = new AudioPlayerManager(manager) {
			@Override
			public void onPlayerCompletion()
			{
			    botonPlay.setBackgroundResource(R.drawable.icon_play);	    
			    Toast.makeText(getActivity(), R.string.text_audio_play_confirmation, Toast.LENGTH_SHORT).show();
			}
		};
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
		recorder.releaseRecorder();
		recorder = null;
		player.releasePlayer();
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
		
		if(audio.isMaxVolume())
		{
			botonVolumenMas.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonVolumenMas.setVisibility(View.VISIBLE);
		}
		
		if(audio.isMinVolume())
		{
			botonVolumenMenos.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonVolumenMenos.setVisibility(View.VISIBLE);
		}
	}
    
    /* Listeners de Botones */
	
	private class OnRecordClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
            if(recorder.isStopped())
            {
            	TextInputAlert alert = new TextInputAlert(getActivity(), getString(R.string.text_audio_record_title), getString(R.string.text_audio_record_description), getString(R.string.text_button_record), getString(R.string.text_button_cancel)) {
            		
        			@Override
        			public void onPossitiveButtonClick()
        			{
    		        	recorder.startRecording(manager.getDirectorioMusica(getText()));
    	            	
    	            	botonRecord.setBackgroundResource(R.drawable.icon_record_selected);
        			}

        			@Override
        			public void onNegativeButtonClick() { }
        			
        		};

        		alert.show();
            }
            else if(recorder.isRecording())
            {
            	recorder.stopRecording();
            	
            	botonRecord.setBackgroundResource(R.drawable.icon_record);
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
            if(player.isStoped())
            {
        		ChooseAlert alert = new ChooseAlert(getActivity(), getString(R.string.text_audio_play_title), getString(R.string.text_button_play), getString(R.string.text_button_cancel), manager.getFicherosDirectorioMusica()) {
        			
        			@Override
        			public void onSelectedPossitiveButtonClick(String selected)
        			{						
        				player.startPlaying(selected);
        				botonPlay.setBackgroundResource(R.drawable.icon_pause);
        			}
        			
        			@Override
        			public void onNoSelectedPossitiveButtonClick() { }

        			@Override
        			public void onNegativeButtonClick() { }
        			
        		};

        		alert.show();
            }
            else if(player.isPlaying())
            {
            	player.pausePlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_pause);
            }
            else if(player.isPaused())
            {
            	player.resumePlaying();
            	botonPlay.setBackgroundResource(R.drawable.icon_play);
            }
            
            actualizarBotones();
		}
	}
	
	private class OnVolumenMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			audio.increaseVolume();
			actualizarBotones();
		}
	}
	
	private class OnVolumenMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			audio.decreaseVolume();
			actualizarBotones();
		}
	}
}
