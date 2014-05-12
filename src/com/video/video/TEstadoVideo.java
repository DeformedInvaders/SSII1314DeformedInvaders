package com.video.video;

import com.project.main.R;

public enum TEstadoVideo
{
	Nada, Door, Rock, Noise, Brief;
	
	public TEstadoVideo getNext()
	{
		switch(this)
		{
			case Nada:
				return TEstadoVideo.Door;
			case Door:
				return TEstadoVideo.Rock;
			case Rock:
				return TEstadoVideo.Noise;
			case Noise:
				return TEstadoVideo.Brief;
			case Brief:
				return null;
			default:
				return TEstadoVideo.Nada;
		}
	}
	
	public int getDuration()
	{
		switch(this)
		{
			case Door:
				return 30 * 1000;
			case Rock:
				return 30 * 1000;
			case Noise:
				return 6 * 1000;
			default:
				return -1;
		}
	}
	
	public int getMusic()
	{
		switch(this)
		{
			case Door:
				return R.raw.music_video_intro;
			case Noise:
				return R.raw.music_video_noise;
			case Rock:
				return R.raw.music_video_rock;
			default:
				return -1;
		}
	}
	
	public int getSound()
	{
		switch(this)
		{
			case Rock:
				return R.raw.effect_video_door;
			case Noise:
				return R.raw.effect_video_stopplay;
			default:
				return -1;
		}
	}
}