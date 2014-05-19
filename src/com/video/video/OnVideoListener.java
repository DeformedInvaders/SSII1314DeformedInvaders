package com.video.video;

public interface OnVideoListener
{
	public void onVideoFinished();
	public void onPlayMusic(int music);
	public void onPlaySoundEffect(int sound, boolean blockable);
	public void onChangeDialog(int text, TEstadoVideo estado);
	public void onDismissDialog();
}
