package com.character.select;

import com.project.controller.DataSaved;

public class CharacterSelectionDataSaved extends DataSaved
{
	private int indice;
	
	public CharacterSelectionDataSaved(int page)
	{
		indice = page;
	}
	
	public int getIndice()
	{
		return indice;
	}
}
