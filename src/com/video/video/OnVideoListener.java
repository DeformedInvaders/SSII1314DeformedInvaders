package com.video.video;

public interface OnVideoListener
{
	public void onVideoFinished();
	public void onPlayMusic(int music);
	public void onPlaySoundEffect(int sound, boolean blockable);
	public void onPlayVoice(int voice);
	public void onChangeDialog(int text, TStateVideo estado);
	public void onDismissDialog();
}
