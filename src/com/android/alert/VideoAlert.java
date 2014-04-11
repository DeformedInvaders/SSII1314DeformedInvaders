package com.android.alert;

import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.project.main.R;

public class VideoAlert extends WindowAlert
{
	private VideoView video;
	private ImageButton botonNext, botonPrev;
	private int pos, posMax;
	
	private List<String> listMessage;
	private List<Uri> listVideoPath;
	
	public VideoAlert(Context context, String title, String message, String textYes, Uri videoPath)
	{
		super(context, title);
		
		setMessage(message);
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		
			video = new VideoView(context);
			video.setVideoURI(videoPath);
			video.setZOrderOnTop(true);
			video.setOnPreparedListener(new OnPreparedListener() {                    
			    @Override
			    public void onPrepared(MediaPlayer mediaplayer)
			    {
			    	mediaplayer.setLooping(true);
			    }
			});
		
			layout.addView(video, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
		setView(layout);
		
		setCancelable(false);
	
		setPositiveButton(textYes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton)
			{
				video.stopPlayback();
			}
		});
	}
	
	public VideoAlert(Context context, String title, List<String> message, String textYes, List<Uri> videoPath)
	{
		super(context, title);
		
		pos = 0;
		posMax = Math.min(message.size(), videoPath.size());
		
		listMessage = message;
		listVideoPath = videoPath;
		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
			
			video = new VideoView(context);
			video.setZOrderOnTop(true);
			video.setOnPreparedListener(new OnPreparedListener() {                    
			    @Override
			    public void onPrepared(MediaPlayer mediaplayer)
			    {
			    	mediaplayer.setLooping(true);
			    }
			});
			
			LinearLayout layoutBotones = new LinearLayout(context);
			
				int widthButton = (int) context.getResources().getDimension(R.dimen.FragmentButton_LayoutWidth_Dimen);
				int heightButton = (int) context.getResources().getDimension(R.dimen.FragmentButton_LayoutHeight_Dimen);
				
				botonNext = new ImageButton(context);
				botonPrev = new ImageButton(context);
				
				botonNext.setLayoutParams(new LinearLayout.LayoutParams(widthButton, heightButton));
				botonPrev.setLayoutParams(new LinearLayout.LayoutParams(widthButton, heightButton));
				
				botonNext.setOnClickListener(new OnNextVideoClickListener());
				botonPrev.setOnClickListener(new OnPrevVideoClickListener());
	
				botonNext.setBackgroundResource(R.drawable.icon_tool_next);
				botonPrev.setBackgroundResource(R.drawable.icon_tool_prev);

			layoutBotones.addView(botonPrev);
			layoutBotones.addView(botonNext);
			layoutBotones.setOrientation(LinearLayout.HORIZONTAL);
			layoutBotones.setGravity(Gravity.CENTER);
			
		layout.addView(video, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addView(layoutBotones, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
		setView(layout);
		
		setCancelable(false);
	
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
