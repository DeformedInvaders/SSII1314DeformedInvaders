package com.creation.paint;


public class ActionColor extends Action
{
	private int colorFondo;
	
	public ActionColor(int color)
	{
		tipoAccion = TTypeAction.Color;
		
		colorFondo = color;
	}

	/* Métodos de Obtención de Información */
	
	public int getColorFondo()
	{
		return colorFondo;
	}
}
