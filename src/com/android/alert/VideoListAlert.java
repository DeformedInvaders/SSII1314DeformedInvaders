package com.android.alert;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.VideoView;

import com.android.view.IconImageButton;
import com.project.main.R;

public class VideoListAlert extends WindowAlert
{
	private VideoView videoView;
	private IconImageButton buttonNext, buttonPrev;
	private int posActual, posMax;
	
	private List<Integer> listMessage;
	private List<Uri> listVideoPath;
	
	public VideoListAlert(Context context, int title, List<Integer> message, int textYes, List<Uri> videoPath)
	{
		super(context, title, false);
		
		posActual = 0;
		posMax = Math.min(message.size(), videoPath.size());
		
		listMessage = message;
		listVideoPath = videoPath;
		
		setView(R.layout.alert_videolist_layout);
		
		videoView = (VideoView) findViewById(R.id.videoViewVideoAlert1);
		videoView.setZOrderOnTop(true);
		videoView.setOnPreparedListener(new OnPreparedListener() {                    
		    @Override
		    public void onPrepared(MediaPlayer mediaplayer)
		    {
		    	mediaplayer.setLooping(true);
		    }
		});
		
		
		buttonPrev = (IconImageButton) findViewById(R.id.imageButtonVideoAlert1);
		buttonNext = (IconImageButton) findViewById(R.id.imageButtonVideoAlert2);
		
		buttonNext.setOnClickListener(new OnNextVideoClickListener());
		buttonPrev.setOnClickListener(new OnPrevVideoClickListener());
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				videoView.stopPlayback();
			}
		});
		
		actualizarInterfaz();
	}
	
	/* Métodos Públicos */

	@Override
	public void show()
	{
		super.show();
		videoView.start();
	}
	
	/* Métodos Privados */

	private void actualizarInterfaz()
	{
		changeMessage(listMessage.get(posActual));
		
		if(videoView.isPlaying())
		{
			videoView.stopPlayback();
		}
		
		videoView.setVideoURI(listVideoPath.get(posActual));
		videoView.start();
		
		buttonPrev.setVisibility(View.INVISIBLE);
		buttonNext.setVisibility(View.INVISIBLE);
		
		if (posActual > 0)
		{
			buttonPrev.setVisibility(View.VISIBLE);
		}
		
		if (posActual < posMax - 1)
		{
			buttonNext.setVisibility(View.VISIBLE);
		}
	}

	/* Métodos Listener onClick */

	private class OnPrevVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (posActual > 0)
			{
				posActual --;
				actualizarInterfaz();
			}
		}
	}

	private class OnNextVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (posActual < posMax - 1)
			{
				posActual ++;
				actualizarInterfaz();
			}
		}
	}
}
