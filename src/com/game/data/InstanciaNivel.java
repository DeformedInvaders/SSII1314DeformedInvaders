package com.game.data;

import java.util.List;

public class InstanciaNivel
{
	private int indiceNivel;
	private String nombreNivel;

	private List<Entidad> tiposEnemigos;
	private List<InstanciaEntidad> listaEnemigos;
	private Background fondoNivel;

	public InstanciaNivel(int indice, String nombre, List<Entidad> tipos, List<InstanciaEntidad> lista, Background fondo)
	{
		indiceNivel = indice;
		nombreNivel = nombre;
		tiposEnemigos = tipos;
		listaEnemigos = lista;
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

	public List<Entidad> getTipoEnemigos()
	{
		return tiposEnemigos;
	}

	public List<InstanciaEntidad> getListaEnemigos()
	{
		return listaEnemigos;
	}

	public Background getFondoNivel()
	{
		return fondoNivel;
	}
}
