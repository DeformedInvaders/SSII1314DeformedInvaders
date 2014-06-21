package com.game.data;

import java.util.List;

import com.game.select.TTypeLevel;

public class InstanceLevel
{
	private TTypeLevel tipoNivel;
	private String nombreNivel;

	private List<Entity> tiposEnemigos;
	private List<InstanceEntity> listaEnemigos;
	private Boss enemigoBoss;
	private Background fondoNivel;

	public InstanceLevel(TTypeLevel tipo, String nombre, List<Entity> tipos, Boss boss, List<InstanceEntity> lista, Background fondo)
	{
		tipoNivel = tipo;
		nombreNivel = nombre;
		tiposEnemigos = tipos;
		listaEnemigos = lista;
		enemigoBoss = boss;
		fondoNivel = fondo;
	}

	public TTypeLevel getTipoNivel()
	{
		return tipoNivel;
	}

	public String getNombreNivel()
	{
		return nombreNivel;
	}

	public List<Entity> getTipoEnemigos()
	{
		return tiposEnemigos;
	}
	
	public Boss getBoss()
	{
		return enemigoBoss;
	}

	public List<InstanceEntity> getListaEnemigos()
	{
		return listaEnemigos;
	}

	public Background getFondoNivel()
	{
		return fondoNivel;
	}
}
