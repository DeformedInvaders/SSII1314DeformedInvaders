package com.game.game;

import com.creation.data.TTipoMovimiento;

public interface OnGameListener
{
	public void onGameEnemiesFinished(int score, int characterLives, int bossLives);
	public void onGameBossFinished(int score, int characterLives, int bossLives);
	public void onGameFailed(int score, int characterLives);
	public void onGameScoreChanged(int score);
	public void onGameLivesChanged(int characterLives);
	public void onGameLivesChanged(int characterLives, int bossLives);
	
	public void onGamePlaySound(TTipoMovimiento tipo);
}
