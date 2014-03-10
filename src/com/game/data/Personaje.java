package com.game.data;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.deform.TDeformTipo;

public class Personaje extends Malla
{	
	private Movimientos movimientos;
	
	private TDeformTipo estado;
	
	/* SECTION Constructora */
	
	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
	}
	
	/* SECTION M�todos abstractos de Entidad */
	
	@Override
	public void avanzar(OpenGLRenderer renderer, boolean primerosCiclos)
	{
		if(estado == TDeformTipo.Jump)
		{
			float dY = 5 * width / 24;
			if(primerosCiclos)
			{
				posicionY += dY;
			}
			else
			{
				posicionY -= dY;
			}
		}
	}
	
	/* SECTION M�todos de Animaci�n */
	
	public void mover() 
	{
		listaVerticesAnimacion = movimientos.get(0);
		
		estado = TDeformTipo.Run;
		iniciar();
	}
	
	public void saltar() 
	{
		listaVerticesAnimacion = movimientos.get(1);
		
		estado = TDeformTipo.Jump;
		iniciar();
	}
	
	public void agachar() 
	{
		listaVerticesAnimacion = movimientos.get(2);
		
		estado = TDeformTipo.Crouch;
		iniciar();
	}
	
	public void atacar() 
	{
		listaVerticesAnimacion = movimientos.get(3);
		
		estado = TDeformTipo.Attack;
		iniciar();
	}
	
	public boolean colision(Entidad entidad)
	{	
		if(entidad.isActivo())
		{
			float posEntidad = entidad.getPosicion();
			float widthEntidad = entidad.getWidth();
			TTipoEntidad tipoEntidad = entidad.getTipo();
			
			if(posEntidad + widthEntidad/2 < posicionX)
			{
				entidad.setInactivo();
			}
			else if(posEntidad < posicionX + width/2 && posEntidad > posicionX)
			{
				if(tipoEntidad == TTipoEntidad.Enemigo)
				{
					return estado != TDeformTipo.Attack && estado != TDeformTipo.Jump;
				}
				else if(tipoEntidad == TTipoEntidad.Obstaculo || tipoEntidad == TTipoEntidad.Grieta)
				{
					return estado != TDeformTipo.Jump;
				}
			}
		}
		
		return false;
	}
	
	/* SECTION M�todos de Modificaci�n de Informaci�n */
	
	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		
		reposo();
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	public Movimientos getMovimientos()
	{
		return movimientos;
	}
}
