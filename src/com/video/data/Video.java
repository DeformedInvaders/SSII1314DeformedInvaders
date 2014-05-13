package com.video.data;

import com.game.data.Personaje;
import com.video.video.TEstadoVideo;

public class Video
{
	private int[] idFondos;
	private int[] mensaje1, mensaje2;
	private Personaje personaje1, personaje2;
	
	public Video(int[] fondos, int[] m1, int[] m2, Personaje p1, Personaje p2)
	{
		mensaje1 = m1;
		mensaje2 = m2;
		idFondos = fondos;
		personaje1 = p1;
		personaje2 = p2;
	}
	
	public int[] getIdTexturaFondos()
	{
		return idFondos;
	}
	
	public Personaje getPersonaje(TTipoActores actor)
	{
		if (actor == TTipoActores.Guitarrista)
		{
			return personaje1;
		}
		else
		{
			return personaje2;
		}
	}
	
	public int[] getMensaje(TEstadoVideo estado)
	{
		if (estado == TEstadoVideo.Noise)
		{
			return mensaje1;
		}
		else if (estado == TEstadoVideo.Brief)
		{
			return mensaje2;
		}
		
		return null;
	}
}
