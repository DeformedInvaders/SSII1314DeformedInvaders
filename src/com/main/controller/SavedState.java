package com.main.controller;

public class SavedState
{
	private DataSaved datosSalvados;
	private TStateController estadoSalvado;
	
	public SavedState(TStateController estado)
	{
		estadoSalvado = estado;
	}
	
	public SavedState(TStateController estado, DataSaved datos)
	{
		datosSalvados = datos;
		estadoSalvado = estado;
	}

	public DataSaved getDatosSalvados()
	{
		return datosSalvados;
	}

	public TStateController getEstadoSalvado()
	{
		return estadoSalvado;
	}
	
}
