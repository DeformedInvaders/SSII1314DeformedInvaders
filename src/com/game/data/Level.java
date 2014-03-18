package com.game.data;

import java.util.List;
import java.util.Queue;

public class Level
{
	private int indiceNivel;
	private String nombreNivel;
	
	private List<Entidad> listaEnemigos;
	private Queue<InstanciaEntidad> colaEnemigos;
	private Background fondoNivel;
	
	public Level(int indice, String nombre, List<Entidad> lista, Queue<InstanciaEntidad> cola, Background fondo)
	{
		indiceNivel = indice;
		nombreNivel = nombre;
		listaEnemigos = lista;
		colaEnemigos = cola;
		fondoNivel = fondo;
	}

	public int getIndiceNivel()
	{
		return indiceNivel;
	}

	public String getNombreNivel()
	{
		return nombreNivel;
	}

	public List<Entidad> getListaEnemigos()
	{
		return listaEnemigos;
	}

	public Queue<InstanciaEntidad> getColaEnemigos()
	{
		return colaEnemigos;
	}

	public Background getFondoNivel()
	{
		return fondoNivel;
	}
}
