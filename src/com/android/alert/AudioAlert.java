package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioRecorderManager;
import com.android.storage.ExternalStorageManager;
import com.project.main.R;

public abstract class AudioAlert extends WindowAlert
{
	private static final long DURATION = 3000;
	
	private TextView cuenta;
	private ProgressBar progressBar;
	private ImageButton botonRecAudio, botonPlayAudio;
	
	private CountDownTimer timer;
	private AudioRecorderManager audioRecord;
	private AudioPlayerManager audioPlayer;
	
	private String movimiento;
	
	/* SECTION Constructora */
	
	public AudioAlert(Context context, String title, String messege, String textYes, String textNo, ExternalStorageManager manager, String nombre)
	{		
		super(context, title);
		
		movimiento = nombre;
		
		audioRecord = new AudioRecorderManager(manager);
		audioPlayer = new AudioPlayerManager(manager) {
			
			@Override
			public void onPlayerCompletion()
			{
				botonPlayAudio.setBackgroundResource(R.drawable.icon_play);
			}
		};
		
		setMessage(messege);
		
		LinearLayout layout = new LinearLayout(context);
		
		cuenta = new TextView(context);
		cuenta.setTextSize(20);
		cuenta.setGravity(Gravity.CENTER);
		
		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
		
		LinearLayout layoutBotones = new LinearLayout(context);
		
		botonRecAudio = new ImageButton(context);
		botonPlayAudio = new ImageButton(context);
		
		botonRecAudio.setOnClickListener(new OnRecAudioClickListener());
		botonPlayAudio.setOnClickListener(new OnPlayAudioClickListener());
		
		botonRecAudio.setBackgroundResource(R.drawable.icon_record_started);
		botonPlayAudio.setBackgroundResource(R.drawable.icon_play);
		
		botonPlayAudio.setVisibility(View.INVISIBLE);
		
		layoutBotones.addView(botonRecAudio);
		layoutBotones.addView(botonPlayAudio);
		layoutBotones.setOrientation(LinearLayout.HORIZONTAL);
		layoutBotones.setGravity(Gravity.CENTER);
		
		layout.addView(cuenta, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(progressBar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(layoutBotones, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		setView(layout);
		        
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onPossitiveButtonClick();
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onNegativeButtonClick();
			}
		});
		
		timer = new CountDownTimer(3000, 1) {
			
			@Override
			public void onFinish() 
			{ 
				audioRecord.stopRecording();
				
				botonPlayAudio.setVisibility(View.VISIBLE);
				botonRecAudio.setVisibility(View.VISIBLE);
				
				reiniciarContadores();
			}

			@Override
			public void onTick(long millisUntilFinished) 
			{				
				actualizarContadores(millisUntilFinished);
			}
        };
        
        reiniciarContadores();
	}
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onPossitiveButtonClick();
	public abstract void onNegativeButtonClick();
	
	private void reiniciarContadores()
	{
		cuenta.setText("0" + DURATION/1000 + ":" + DURATION%100 + "0");
		progressBar.setProgress(0);
	}
	
	/* SECTION Métodos Privados */
	
	private void actualizarContadores(long millisUntilFinished)
	{
		cuenta.setText("0" + millisUntilFinished/1000 + ":" + (millisUntilFinished%100));
		progressBar.setProgress((int) (100*(DURATION - millisUntilFinished)/DURATION));
	}
	
	/* SECTION Métodos Listener onClick */
	
	private class OnRecAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			botonRecAudio.setVisibility(View.INVISIBLE);
			
			timer.start();
			audioRecord.startRecording(movimiento);
		}	
	}
	
	private class OnPlayAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			botonPlayAudio.setBackgroundResource(R.drawable.icon_play_selected);
			audioPlayer.startPlaying(movimiento);
		}	
	}
}
