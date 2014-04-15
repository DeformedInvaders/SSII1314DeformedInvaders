package com.game.game;

import com.creation.data.TTipoMovimiento;

public interface OnGameListener
{
	public void onGameFinished(int score, int lives);
	public void onGameFailed();
	public void onGameScoreChanged(int score);
	public void onGameLivesChanged(int lives);
	
	public void onGamePlaySound(TTipoMovimiento tipo);
}
