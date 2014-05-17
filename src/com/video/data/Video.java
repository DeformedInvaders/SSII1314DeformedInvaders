package com.video.data;

import java.util.List;

import com.game.data.Personaje;
import com.video.video.TEstadoVideo;

public class Video
{
	private int[] idFondos;
	private int[] mensaje1, mensaje2, mensaje3;
	private List<Personaje> listaPersonajes;
	private List<ObjetoInanimado> listaObjetos;
	
	public Video(int[] fondos, int[] m1, int[] m2, int[] m3, List<Personaje> personajes, List<ObjetoInanimado> objetos)
	{
		mensaje1 = m1;
		mensaje2 = m2;
		mensaje3 = m3;
		idFondos = fondos;
		
		listaPersonajes = personajes;
		listaObjetos = objetos;
	}
	
	public int[] getIdTexturaFondos()
	{
		return idFondos;
	}
	
	public Personaje getPersonaje(TTipoActores actor)
	{
		return listaPersonajes.get(actor.ordinal());
	}
	
	public List<ObjetoInanimado> getListaObjetos()
	{
		return listaObjetos;
	}
	
	public int[] getMensaje(TEstadoVideo estado)
	{
		if (estado == TEstadoVideo.Outside)
		{
			return mensaje1;
		}
		else if (estado == TEstadoVideo.Noise)
		{
			return mensaje2;
		}
		else if (estado == TEstadoVideo.Brief)
		{
			return mensaje3;
		}
		
		return null;
	}
}
