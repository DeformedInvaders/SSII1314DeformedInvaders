package com.project.controller;

public class Estado
{
	private DataSaved datosSalvados;
	private TEstadoController estadoSalvado;
	
	public Estado(TEstadoController estado)
	{
		estadoSalvado = estado;
	}
	
	public Estado(TEstadoController estado, DataSaved datos)
	{
		datosSalvados = datos;
		estadoSalvado = estado;
	}

	public DataSaved getDatosSalvados()
	{
		return datosSalvados;
	}

	public TEstadoController getEstadoSalvado()
	{
		return estadoSalvado;
	}
	
}
