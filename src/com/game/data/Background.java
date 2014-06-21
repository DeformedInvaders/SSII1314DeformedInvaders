package com.game.data;

import com.game.game.TTypeEndgame;

public class Background
{
	private int[] idPolaroid;
	private int[] idBackground;
	private int idSun;
	
	public Background(int sol, int[] background, int[] polaroid)
	{
		idBackground = background;
		idSun = sol;
		idPolaroid = polaroid;
	}

	public int[] getIdBackground()
	{
		return idBackground;
	}
	
	public int getIdSun()
	{
		return idSun;
	}
	
	public int getIdPolaroid(TTypeEndgame tipo)
	{
		return idPolaroid[tipo.ordinal()];
	}
}
