package com.creation.paint;


public class ActionColor extends Action
{
	private int colorFondo;
	
	public ActionColor(int color)
	{
		tipoAccion = TTypeAction.Color;
		
		colorFondo = color;
	}

	/* M�todos de Obtenci�n de Informaci�n */
	
	public int getColorFondo()
	{
		return colorFondo;
	}
}
