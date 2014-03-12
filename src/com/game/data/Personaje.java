package com.game.data;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.deform.TDeformTipo;
import com.game.game.InstanciaEntidad;

public class Personaje extends Malla
{	
	private Movimientos movimientos;
	
	private TDeformTipo estado;
	private float posicionX, posicionY;
	/* SECTION Constructora */
	
	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
		posicionX = 0.0f;
		posicionY = 0.0f;
	}
	
	/* SECTION M�todos abstractos de Entidad */
	
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
	
	public int colision(Entidad entidad, InstanciaEntidad instanciaE)
	{	
		float posXEntidad = instanciaE.getPosX();
		float posYEntidad = instanciaE.getPosY();
		
		float widthEntidad = entidad.getWidth();	
		float heightEntidad = entidad.getHeight();
		
		if(posXEntidad + widthEntidad/2 < posicionX)
		{
			return 2;
		}
		else if(posXEntidad < posicionX + width/2 && posXEntidad > posicionX)
		{
			if(posicionY < posYEntidad + heightEntidad)
			{
				return 1;
			}
		}
		
		return 0;
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
