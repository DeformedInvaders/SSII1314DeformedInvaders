package com.game.data;

import com.game.game.TTipoEndgame;

public class Background
{
	private int[] idPolaroid;
	private int[] idFondos;
	private int idFondoSol;
	
	public Background(int fondoSol, int[] fondos, int[] polaroid)
	{
		idFondos = fondos;
		idFondoSol = fondoSol;
		idPolaroid = polaroid;
	}

	public int[] getIdTexturaFondos()
	{
		return idFondos;
	}
	
	public int getIdTexturaSol()
	{
		return idFondoSol;
	}
	
	public int getIdPolaroid(TTipoEndgame tipo)
	{
		return idPolaroid[tipo.ordinal()];
	}
}
