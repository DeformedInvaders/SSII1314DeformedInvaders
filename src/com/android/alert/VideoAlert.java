package com.android.alert;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.widget.VideoView;

import com.project.main.R;

public class VideoAlert extends WindowAlert
{
	private VideoView videoView;
	
	public VideoAlert(Context context, int title, int message, int textYes, Uri videoPath)
	{
		super(context, title, false);
		
		setMessage(message);
		
		setView(R.layout.alert_video_layout);
		
		videoView = (VideoView) findViewById(R.id.videoViewVideoAlert1);
		
		videoView.setVideoURI(videoPath);
		videoView.setZOrderOnTop(true);
		videoView.setOnPreparedListener(new OnPreparedListener() {                    
		    @Override
		    public void onPrepared(MediaPlayer mediaplayer)
		    {
		    	mediaplayer.setLooping(true);
		    }
		});
	
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				videoView.stopPlayback();
			}
		});
		
		videoView.start();
	}
}
