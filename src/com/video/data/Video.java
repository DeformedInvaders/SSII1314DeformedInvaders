package com.video.data;

import java.util.List;

import com.game.data.Personaje;
import com.video.video.TEstadoVideo;

public class Video
{
	private int[] idFondos;
	private List<int[]> listaMensajes;
	private List<Personaje> listaPersonajes;
	private List<ObjetoInanimado> listaObjetos;
	
	public Video(int[] fondos, List<int[]> mensajes, List<Personaje> personajes, List<ObjetoInanimado> objetos)
	{
		idFondos = fondos;
		
		listaMensajes = mensajes;
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
		if (estado.ordinal() < listaMensajes.size())
		{
			return listaMensajes.get(estado.ordinal());
		}
		
		return null;
	}
}
