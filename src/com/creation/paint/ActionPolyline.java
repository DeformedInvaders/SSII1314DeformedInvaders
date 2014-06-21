package com.creation.paint;

import com.creation.data.Polyline;

public class ActionPolyline extends Action
{
	private Polyline polyline;
	
	public ActionPolyline(Polyline line)
	{
		typeAction = TTypeAction.Polyline;
		
		polyline = line;
	}
	
	/* M�todos de Obtenci�n de Informaci�n */
	
	public Polyline getPolyline()
	{
		return polyline;
	}
}
