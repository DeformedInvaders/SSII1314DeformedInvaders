package com.video.data;

import com.game.data.Personaje;

public class Video
{
	private int[] idFondos;
	private Personaje personaje1, personaje2;
	
	public Video(int[] fondos, Personaje p1, Personaje p2)
	{
		idFondos = fondos;
		personaje1 = p1;
		personaje2 = p2;
	}
	
	public int[] getIdTexturaFondos()
	{
		return idFondos;
	}
	
	public Personaje getGuitarrista()
	{
		return personaje1;
	}
	
	public Personaje getCientifico()
	{
		return personaje2;
	}
}
