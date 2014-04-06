package com.game.data;

public class Background
{
	private int idTexturaFondo1, idTexturaFondo2, idTexturaFondo3, idTexturaCielo, idTexturaGameOver, idTexturaLevelCompleted;

	public void setBackground(int idFondo1, int idFondo2, int idFondo3, int idFondo4, int idFondo5, int idFondo6)
	{
		idTexturaFondo1 = idFondo1;
		idTexturaFondo2 = idFondo2;
		idTexturaFondo3 = idFondo3;
		idTexturaCielo = idFondo4;
		idTexturaGameOver = idFondo5;
		idTexturaLevelCompleted = idFondo6;
	}

	public int getIdTexturaFondo1()
	{
		return idTexturaFondo1;
	}

	public int getIdTexturaFondo2()
	{
		return idTexturaFondo2;
	}

	public int getIdTexturaFondo3()
	{
		return idTexturaFondo3;
	}
	
	public int getIdTexturaCielo()
	{
		return idTexturaCielo;
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
