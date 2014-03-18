package com.game.data;

public class Background
{
	int idTextura1, idTextura2, idTextura3;
	int idTexturaGameOver, idTexturaLevelCompleted;
	
	public void setBackground(int id1, int id2, int id3, int id4, int id5)
	{
		idTextura1 = id1;
		idTextura2 = id2;
		idTextura3 = id3;
		idTexturaGameOver = id4;
		idTexturaLevelCompleted = id5;
	}

	public int getIdTextura1()
	{
		return idTextura1;
	}

	public int getIdTextura2()
	{
		return idTextura2;
	}

	public int getIdTextura3()
	{
		return idTextura3;
	}
	
	public int getIdTextureGameOver()
	{
		return idTexturaGameOver;
	}
	
	public int getIdTextureLevelCompleted()
	{
		return idTexturaLevelCompleted;
	}
	
}
