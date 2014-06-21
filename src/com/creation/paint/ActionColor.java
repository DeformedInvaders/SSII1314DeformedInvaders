package com.creation.paint;


public class ActionColor extends Action
{
	private int backgroundColor;
	
	public ActionColor(int color)
	{
		typeAction = TTypeAction.Color;
		
		backgroundColor = color;
	}

	/* Métodos de Obtención de Información */
	
	public int getBackgroundColor()
	{
		return backgroundColor;
	}
}
