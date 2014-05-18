package com.video.video;

import com.project.main.R;

public enum TEstadoVideo
{
	Nada, Outside, Door, Rock, Noise, Brief, Logo;
	
	public TEstadoVideo getNext()
	{
		switch(this)
		{
			case Nada:
				return TEstadoVideo.Outside;
			case Outside:
				return TEstadoVideo.Door;
			case Door:
				return TEstadoVideo.Rock;
			case Rock:
				return TEstadoVideo.Noise;
			case Noise:
				return TEstadoVideo.Brief;
			case Brief:
				return TEstadoVideo.Logo;
			default:
				return TEstadoVideo.Nada;
		}
	}
	
	public long getDuration()
	{
		switch(this)
		{
			case Rock:
				return 18 * 1000;
			case Noise:
				return 5 * 1000;
			default:
				return 10 * 1000;
		}
	}
	
	public int getMusic()
	{
		switch(this)
		{
			case Outside:
				return R.raw.music_video_outside;
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
				return R.raw.effect_video_opendoor;
			case Noise:
				return R.raw.effect_video_stopplay;
			default:
				return -1;
		}
	}
}