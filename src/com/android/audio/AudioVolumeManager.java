package com.android.audio;

import android.content.Context;
import android.media.AudioManager;

public class AudioVolumeManager
{
	private AudioManager audioManager;

	/* Constructora */

	public AudioVolumeManager(Context context)
	{
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}

	/* Métodos de Selección de Estado */

	public void increaseVolume()
	{
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	public void decreaseVolume()
	{
		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	public void muteVolume()
	{
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}

	public void unmuteVolume()
	{
		audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	/* Métodos de Obtención de Información */

	public boolean isMaxVolume()
	{
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public boolean isMinVolume()
	{
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
	}
}
