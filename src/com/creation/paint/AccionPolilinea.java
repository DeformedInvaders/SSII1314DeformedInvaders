package com.creation.paint;

import com.creation.data.Polilinea;
import com.creation.data.TTipoAccion;

public class AccionPolilinea extends Accion
{
	private Polilinea polilinea;
	
	public AccionPolilinea(Polilinea linea)
	{
		tipoAccion = TTipoAccion.Polilinea;
		
		polilinea = linea;
	}
	
	/* M�todos de Obtenci�n de Informaci�n */
	
	public Polilinea getPolilinea()
	{
		return polilinea;
	}
}
