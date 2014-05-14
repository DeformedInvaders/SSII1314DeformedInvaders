package com.video.video;

import com.project.main.R;

public enum TEstadoSonido
{
	Nada, Agua, Electricidad, Monitores, Microfono, Altavoz, Puerta, Trastos;
	
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
			case Microfono:
				return R.raw.effect_video_microphone;
			case Altavoz:
				return R.raw.effect_video_speaker;
			case Puerta:
				return R.raw.effect_video_knockdoor;
			case Trastos:
				return R.raw.effect_video_noise;
			default:
				return -1;
		}
	}
}
