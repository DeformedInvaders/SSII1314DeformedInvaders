package com.android.alert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioRecorderManager;
import com.android.storage.ExternalStorageManager;
import com.project.main.R;

public abstract class AudioAlert 
{
	private AlertDialog.Builder alert;
	
	private TextView cuenta;
	private ProgressBar progressBar;
	private ImageButton botonRecAudio, botonPlayAudio;
	private CountDownTimer timer;
	private long tiempo;
	private int progreso;
	private AudioRecorderManager audioRecord;
	private AudioPlayerManager audioPlayer;
	private String personaje;
	
	public AudioAlert(Context context, String title, String messege, String textYes, String textNo, ExternalStorageManager manager, String personaje)
	{
		alert = new AlertDialog.Builder(context);
		
		alert.setTitle(title);
		alert.setMessage(messege);
		
		tiempo = 2500;
		progreso = 0;
		
		this.personaje = personaje;
		audioRecord = new AudioRecorderManager(manager);
		audioPlayer = new AudioPlayerManager(manager){
			public void onPlayerCompletion()
			{
				botonPlayAudio.setBackgroundResource(R.drawable.icon_play);
			}
		};
		
		LinearLayout layout = new LinearLayout(context);
		cuenta = new TextView(context);
		cuenta.setText(tiempo/1000 + ":" + tiempo%100);
		cuenta.setTextSize(20);
		cuenta.setGravity(Gravity.CENTER);
		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		progressBar.setProgress(progreso);
		
		LinearLayout layoutBotones = new LinearLayout(context);
		botonRecAudio = new ImageButton(context);
		botonPlayAudio = new ImageButton(context);
		botonRecAudio.setOnClickListener(new OnRecAudioClickListener());
		botonPlayAudio.setOnClickListener(new OnPlayAudioClickListener());
		botonRecAudio.setBackgroundResource(R.drawable.icon_record_start);
		botonPlayAudio.setBackgroundResource(R.drawable.icon_play);
		
		botonPlayAudio.setVisibility(View.INVISIBLE);
		
		layoutBotones.addView(botonRecAudio);
		layoutBotones.addView(botonPlayAudio);
		layoutBotones.setOrientation(LinearLayout.HORIZONTAL);
		
		layout.addView(cuenta, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(progressBar, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.addView(layoutBotones, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		alert.setView(layout);
		
		timer = new CountDownTimer(2500, 10) 
		{

			@Override
			public void onFinish() 
			{ 
				actualizarBotones();
				audioRecord.stopRecording();
				tiempo = 2500;
				progreso = 0;
			}

			@Override
			public void onTick(long arg0) 
			{
				tiempo = tiempo-arg0;
				progreso = (int) (100 - 100*tiempo/2500);
				
				cuenta.setText(tiempo/1000 + ":" + tiempo%100);
				progressBar.setProgress(progreso);
			}
        };
        
		alert.setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		alert.setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
	}
	
	private void actualizarBotones()
	{
		botonRecAudio.setBackgroundResource(R.drawable.icon_record_start);
		botonPlayAudio.setVisibility(View.VISIBLE);
	}
	
	private class OnRecAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			//TODO Grabar audio
			botonRecAudio.setBackgroundResource(R.drawable.icon_record_started);
			timer.start();
			audioRecord.startRecording(personaje);
		}	
	}
	
	private class OnPlayAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			//TODO Reproducir audio
			botonPlayAudio.setBackgroundResource(R.drawable.icon_play_selected);
			audioPlayer.startPlaying(personaje);
		}	
	}
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	public void show()
	{
		alert.show();
	}
}
