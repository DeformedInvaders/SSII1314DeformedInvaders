package com.android.audio;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

public class AudioVolumeManager
{
	private AudioManager audio;

	/* Constructora */

	public AudioVolumeManager(Activity activity)
	{
		audio = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	/* Métodos de Selección de Estado */

	public void increaseVolume()
	{
		audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
	}

	public void decreaseVolume()
	{
		audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
	}

	public void muteVolume()
	{
		audio.setStreamMute(AudioManager.STREAM_MUSIC, true);
	}

	public void unmuteVolume()
	{
		audio.setStreamMute(AudioManager.STREAM_MUSIC, false);
	}

	/* Métodos de Obtención de Información */

	public boolean isMaxVolume()
	{
		return audio.getStreamVolume(AudioManager.STREAM_MUSIC) == audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public boolean isMinVolume() {
		return audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0;
	}
}
