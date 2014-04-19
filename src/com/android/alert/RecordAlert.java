package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.project.main.R;
import com.project.model.GamePreferences;

public abstract class RecordAlert extends WindowAlert
{
	private static final String TIME_FORMAT = "%02d:%03d";
	
	private ProgressBar progressBar;
	private ImageButton botonRecAudio;

	private CountDownTimer timer;

	/* Constructora */

	public RecordAlert(Context context, int title, int messege, int textYes, int textNo)
	{
		super(context, title, false);
		
		setMessage(messege);
		
		setView(R.layout.alert_record_layout);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBarRecordAlert1);
		
		botonRecAudio = (ImageButton) findViewById(R.id.imageButtonRecordAlert1);
		botonRecAudio.setOnClickListener(new OnRecAudioClickListener());
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				onStopRecording();
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

		timer = new CountDownTimer(GamePreferences.TIME_DURATION_ANIMATION, 10) {

			@Override
			public void onFinish()
			{
				botonRecAudio.setBackgroundResource(R.drawable.icon_media_record);
				onStopRecording();
				
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

	/* M�todos Abstractos */

	public abstract void onPossitiveButtonClick();

	public abstract void onNegativeButtonClick();
	
	public abstract void onStartRecording();
	
	public abstract void onStopRecording();
	
	/* M�todos Privados */

	private void reiniciarContadores()
	{
		changeMessage(String.format(TIME_FORMAT, GamePreferences.TIME_DURATION_ANIMATION / 1000, GamePreferences.TIME_DURATION_ANIMATION % 100));
		progressBar.setProgress(0);
	}

	private void actualizarContadores(long millisUntilFinished)
	{
		changeMessage(String.format(TIME_FORMAT, millisUntilFinished / 1000, millisUntilFinished % 100));
		progressBar.setProgress((int) (100 * (GamePreferences.TIME_DURATION_ANIMATION - millisUntilFinished) / GamePreferences.TIME_DURATION_ANIMATION));
	}

	/* M�todos Listener onClick */

	private class OnRecAudioClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			onStartRecording();
			
			botonRecAudio.setBackgroundResource(R.drawable.icon_media_record_selected);
			timer.start();
		}
	}
}