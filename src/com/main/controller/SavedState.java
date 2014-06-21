package com.main.controller;

public class SavedState
{
	private DataSaved dataSaved;
	private TStateController stateSaved;
	
	public SavedState(TStateController state)
	{
		stateSaved = state;
	}
	
	public SavedState(TStateController state, DataSaved data)
	{
		dataSaved = data;
		stateSaved = state;
	}

	public DataSaved getDataSaved()
	{
		return dataSaved;
	}

	public TStateController getStateSaved()
	{
		return stateSaved;
	}
	
}
