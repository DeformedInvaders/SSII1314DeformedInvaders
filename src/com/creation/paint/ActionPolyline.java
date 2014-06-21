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
	
	/* Métodos de Obtención de Información */
	
	public Polyline getPolyline()
	{
		return polyline;
	}
}
