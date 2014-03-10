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
	
	/* SECTION Métodos abstractos de Entidad */
	
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
	
	/* SECTION Métodos de Animación */
	
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
	
	/* SECTION Métodos de Modificación de Información */
	
	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		
		reposo();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public Movimientos getMovimientos()
	{
		return movimientos;
	}
}
