package com.video.video;

import com.project.main.R;

public enum TEstadoSonido
{
	Nada, Agua, Electricidad, Monitores;
	
	public int getSound()
	{
		switch(this)
		{
			case Agua:
				return R.raw.effect_video_water;
			case Electricidad:
				return R.raw.effect_video_electricity;
			case Monitores:
				return R.raw.effect_video_computer;
			default:
				return -1;
		}
	}
}
