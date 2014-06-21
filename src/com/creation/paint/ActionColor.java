package com.creation.paint;


public class ActionColor extends Action
{
	private int backgroundColor;
	
	public ActionColor(int color)
	{
		typeAction = TTypeAction.Color;
		
		backgroundColor = color;
	}

	/* M�todos de Obtenci�n de Informaci�n */
	
	public int getBackgroundColor()
	{
		return backgroundColor;
	}
}
