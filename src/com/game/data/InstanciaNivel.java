package com.game.data;

import java.util.List;

import com.game.select.TTipoLevel;

public class InstanciaNivel
{
	private TTipoLevel tipoNivel;
	private String nombreNivel;

	private List<Entidad> tiposEnemigos;
	private List<InstanciaEntidad> listaEnemigos;
	private Jefe enemigoBoss;
	private Background fondoNivel;

	public InstanciaNivel(TTipoLevel tipo, String nombre, List<Entidad> tipos, Jefe boss, List<InstanciaEntidad> lista, Background fondo)
	{
		tipoNivel = tipo;
		nombreNivel = nombre;
		tiposEnemigos = tipos;
		listaEnemigos = lista;
		enemigoBoss = boss;
		fondoNivel = fondo;
	}

	public TTipoLevel getTipoNivel()
	{
		return tipoNivel;
	}

	public String getNombreNivel()
	{
		return nombreNivel;
	}

	public List<Entidad> getTipoEnemigos()
	{
		return tiposEnemigos;
	}
	
	public Jefe getBoss()
	{
		return enemigoBoss;
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
