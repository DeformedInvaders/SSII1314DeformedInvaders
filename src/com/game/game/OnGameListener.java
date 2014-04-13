package com.game.game;

public interface OnGameListener
{
	public void onGameFinished(int score, int lives);

	public void onGameFailed();
	
	public void onScoreChanged(int score);
	
	public void onLivesChanged(int lives);
}
