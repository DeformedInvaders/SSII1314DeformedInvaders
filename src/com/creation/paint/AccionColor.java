package com.creation.paint;

import com.creation.data.TTipoAccion;

public class AccionColor extends Accion
{
	private int colorFondo;
	
	public AccionColor(int color)
	{
		tipoAccion = TTipoAccion.Color;
		
		colorFondo = color;
	}

	/* M�todos de Obtenci�n de Informaci�n */
	
	public int getColorFondo()
	{
		return colorFondo;
	}
}
