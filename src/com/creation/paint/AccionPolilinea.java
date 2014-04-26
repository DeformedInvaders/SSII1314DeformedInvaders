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
	
	/* Métodos de Obtención de Información */
	
	public Polilinea getPolilinea()
	{
		return polilinea;
	}
}
