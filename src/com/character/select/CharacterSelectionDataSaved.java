package com.character.select;

import com.main.controller.DataSaved;

public class CharacterSelectionDataSaved extends DataSaved
{
	private int index;
	
	public CharacterSelectionDataSaved(int page)
	{
		index = page;
	}
	
	public int getIndex()
	{
		return index;
	}
}
