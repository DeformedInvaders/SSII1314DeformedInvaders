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
				return TEstadoVideo.Brief;
			default:
				return TEstadoVideo.Nada;
		}
	}
	
	public int getDuration()
	{
		switch(this)
		{
			case Door:
				return 18 * 1000;
			case Rock:
				return 28 * 1000;
			case Noise:
				return 5 * 1000;
			case Brief:
				return 10 * 1000;
			default:
				return -1;
		}
	}
	
	public int getMusic()
	{
		switch(this)
		{
			case Door:
				return R.raw.music_video_door;
			case Noise:
				return R.raw.music_video_noise;
			case Rock:
				return R.raw.music_video_rock;
			case Brief:
				return R.raw.music_video_brief;
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