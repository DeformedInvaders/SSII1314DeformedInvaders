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

import com.android.audio.AudioPlayerManager;
import com.android.audio.AudioRecorderManager;
import com.android.storage.ExternalStorageManager;
import com.project.main.GamePreferences;
import com.project.main.R;

public abstract class AudioAlert extends WindowAlert
{
	private ProgressBar progressBar;
	private ImageButton botonRecAudio, botonPlayAudio;

	private CountDownTimer timer;
	private AudioRecorderManager audioRecorder;
	private AudioPlayerManager audioPlayer;
	private ExternalStorageManager externalManager;

	private String movimiento;

	/* Constructora */

	public AudioAlert(Context context, String title, String messege, String textYes, String textNo, ExternalStorageManager manager, String nombre)
	{
		super(context, title);

		movimiento = nombre;
		externalManager = manager;

		audioRecorder = new AudioRecorderManager(externalManager);
		audioPlayer = new AudioPlayerManager(externalManager) {
			@Override
			public void onPlayerCompletion()
			{
				botonPlayAudio.setBackgroundResource(R.drawable.icon_media_play);
			}
		};

		setMessage(messege);

		LinearLayout layout = new LinearLayout(context);

		progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);

		LinearLayout layoutBotones = new LinearLayout(context);

		int widthButton = (int) context.getResources().getDimension(R.dimen.FragmentButton_LayoutWidth_Dimen);
		int heightButton = (int) context.getResources().getDimension(R.dimen.FragmentButton_LayoutHeight_Dimen);
		
		botonRecAudio = new ImageButton(context);		
		botonPlayAudio = new ImageButton(context);
		
		botonRecAudio.setLayoutParams(new LinearLayout.LayoutParams(widthButton, heightButton));
		botonPlayAudio.setLayoutParams(new LinearLayout.LayoutParams(widthButton, heightButton));
		
		botonRecAudio.setOnClickListener(new OnRecAudioClickListener());
		botonPlayAudio.setOnClickListener(new OnPlayAudioClickListener());

		botonRecAudio.setBackgroundResource(R.drawable.icon_media_record);
		botonPlayAudio.setBackgroundResource(R.drawable.icon_media_play);

		layoutBotones.addView(botonRecAudio);
		layoutBotones.addView(botonPlayAudio);
		layoutBotones.setOrientation(LinearLayout.HORIZONTAL);
		layoutBotones.setGravity(Gravity.CENTER);

		layout.addView(progressBar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(layoutBotones, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);

		setView(layout);

		setCancelable(false);
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				audioPlayer.stopPlaying();
				audioRecorder.stopRecording();
				
				onPossitiveButtonClick();
			}
		});

		setNegativeButton(textNo, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				if(audioPlayer.stopPlaying() &&	audioRecorder.stopRecording())
				{
					externalManager.eliminarAudioTemp(movimiento);
				}
				
				onNegativeButtonClick();
			}
		});

		timer = new CountDownTimer(GamePreferences.TIME_DURATION_ANIMATION, 100) {

			@Override
			public void onFinish()
			{
				if (audioRecorder.stopRecording())
				{
					botonRecAudio.setBackgroundResource(R.drawable.icon_media_record);
	
					actualizarInterfaz();
					reiniciarContadores();
				}
			}

			@Override
			public void onTick(long millisUntilFinished)
			{
				actualizarContadores(millisUntilFinished);
			}
		};

		reiniciarContadores();
		actualizarInterfaz();
	}

	/* Métodos Abstractos */

	public abstract void onPossitiveButtonClick();

	public abstract void onNegativeButtonClick();

	private void reiniciarContadores()
	{
		changeMessage("0" + GamePreferences.TIME_DURATION_ANIMATION / 1000 + ":" + GamePreferences.TIME_DURATION_ANIMATION % 100);
		progressBar.setProgress(0);
	}

	/* Métodos Privados */

	private void actualizarContadores(long millisUntilFinished)
	{
		changeMessage("0" + millisUntilFinished / 1000 + ":" + (millisUntilFinished % 100));
		progressBar.setProgress((int) (100 * (GamePreferences.TIME_DURATION_ANIMATION - millisUntilFinished) / GamePreferences.TIME_DURATION_ANIMATION));
	}

	private void actualizarInterfaz()
	{
		if (externalManager.existeFicheroTemp(movimiento))
		{
			botonPlayAudio.setVisibility(View.VISIBLE);
		}
		else
		{
			botonPlayAudio.setVisibility(View.INVISIBLE);
		}

		botonRecAudio.setVisibility(View.VISIBLE);
	}

	/* Métodos Listener onClick */

	private class OnRecAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (audioRecorder.startRecording(movimiento))
			{
				timer.start();
				
				botonRecAudio.setBackgroundResource(R.drawable.icon_media_record_selected);
				actualizarInterfaz();
			}
		}
	}

	private class OnPlayAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (audioPlayer.startPlaying(movimiento))
			{
				botonPlayAudio.setBackgroundResource(R.drawable.icon_media_play_selected);
			}
		}
	}
}
