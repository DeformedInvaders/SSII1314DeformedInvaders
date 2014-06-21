package com.creation.paint;

import com.creation.data.Polyline;

public class ActionPolyline extends Action
{
	private Polyline polilinea;
	
	public ActionPolyline(Polyline linea)
	{
		tipoAccion = TTypeAction.Polyline;
		
		polilinea = linea;
	}
	
	/* Métodos de Obtención de Información */
	
	public Polyline getPolilinea()
	{
		return polilinea;
	}
}
