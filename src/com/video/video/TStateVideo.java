package com.video.video;

import com.project.main.R;

public enum TStateVideo
{
	Nothing, Outside, Door, Rock, Noise, Brief, Spaceship, Space;
	
	public TStateVideo getNext()
	{
		switch(this)
		{
			case Nothing:
				return TStateVideo.Outside;
			case Outside:
				return TStateVideo.Door;
			case Door:
				return TStateVideo.Rock;
			case Rock:
				return TStateVideo.Noise;
			case Noise:
				return TStateVideo.Brief;
			case Brief:
				return TStateVideo.Spaceship;
			case Spaceship:
				return TStateVideo.Space;
			default:
				return TStateVideo.Nothing;
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
			case Space:
			case Spaceship:
				return 7 * 1000;
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
			case Spaceship:
				return R.raw.music_video_logo;
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
			case Brief:
				return R.raw.effect_video_brief;
			default:
				return -1;
		}
	}
}