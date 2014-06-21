package com.video.data;

import java.util.List;

import com.game.data.Character;
import com.video.video.TStateVideo;

public class Video
{
	private int[] idFondos;
	private List<int[]> listaMensajes;
	private List<Character> listaPersonajes;
	private List<InanimatedObject> listaObjetos;
	
	public Video(int[] fondos, List<int[]> mensajes, List<Character> personajes, List<InanimatedObject> objetos)
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
	
	public Character getPersonaje(TTypeActors actor)
	{
		return listaPersonajes.get(actor.ordinal());
	}
	
	public List<InanimatedObject> getListaObjetos()
	{
		return listaObjetos;
	}
	
	public int[] getMensaje(TStateVideo estado)
	{
		if (estado.ordinal() < listaMensajes.size())
		{
			return listaMensajes.get(estado.ordinal());
		}
		
		return null;
	}
}
