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
	private VideoView video;
	private IconImageButton botonNext, botonPrev;
	private int pos, posMax;
	
	private List<Integer> listMessage;
	private List<Uri> listVideoPath;
	
	public VideoListAlert(Context context, int title, List<Integer> message, int textYes, List<Uri> videoPath)
	{
		super(context, title, false);
		
		pos = 0;
		posMax = Math.min(message.size(), videoPath.size());
		
		listMessage = message;
		listVideoPath = videoPath;
		
		setView(R.layout.alert_videolist_layout);
		
		video = (VideoView) findViewById(R.id.videoViewVideoAlert1);
		video.setZOrderOnTop(true);
		video.setOnPreparedListener(new OnPreparedListener() {                    
		    @Override
		    public void onPrepared(MediaPlayer mediaplayer)
		    {
		    	mediaplayer.setLooping(true);
		    }
		});
		
		
		botonPrev = (IconImageButton) findViewById(R.id.imageButtonVideoAlert1);
		botonNext = (IconImageButton) findViewById(R.id.imageButtonVideoAlert2);
		
		botonNext.setOnClickListener(new OnNextVideoClickListener());
		botonPrev.setOnClickListener(new OnPrevVideoClickListener());
		
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				video.stopPlayback();
			}
		});
		
		actualizarInterfaz();
	}
	
	/* Métodos Públicos */

	@Override
	public void show()
	{
		super.show();
		video.start();
	}
	
	/* Métodos Privados */

	private void actualizarInterfaz()
	{
		changeMessage(listMessage.get(pos));
		
		if(video.isPlaying())
		{
			video.stopPlayback();
		}
		
		video.setVideoURI(listVideoPath.get(pos));
		video.start();
		
		botonPrev.setVisibility(View.INVISIBLE);
		botonNext.setVisibility(View.INVISIBLE);
		
		if (pos > 0)
		{
			botonPrev.setVisibility(View.VISIBLE);
		}
		
		if (pos < posMax - 1)
		{
			botonNext.setVisibility(View.VISIBLE);
		}
	}

	/* Métodos Listener onClick */

	private class OnPrevVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (pos > 0)
			{
				pos --;
				actualizarInterfaz();
			}
		}
	}

	private class OnNextVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (pos < posMax - 1)
			{
				pos ++;
				actualizarInterfaz();
			}
		}
	}
}
