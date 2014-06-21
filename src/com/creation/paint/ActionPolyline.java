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
	
	/* M�todos de Obtenci�n de Informaci�n */
	
	public Polyline getPolilinea()
	{
		return polilinea;
	}
}
