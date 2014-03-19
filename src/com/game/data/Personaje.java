package com.game.data;

import com.android.view.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.deform.TDeformTipo;
import com.game.game.TEstadoColision;

public class Personaje extends Malla
{	
	private Movimientos movimientos;
	
	private TDeformTipo estado;
	
	
	/* SECTION Constructora */
	
	public Personaje()
	{
		tipo = TTipoEntidad.Personaje;
		id = 0;
		posicionX = 0.0f;
		posicionY = 0.0f;
	}
	
	/* SECTION Métodos abstractos de Entidad */
	
	public boolean avanzar(OpenGLRenderer renderer)
	{
		if(estado == TDeformTipo.Jump)
		{
			float dY = 5 * width / 24;
			
			int mitadAnimacion = posicionAnimacion / 2;
			
			if(posicionAnimacion < mitadAnimacion)
			{
				posicionY += dY;
			}
			else 
			{
				posicionY -= dY;
			}
		}
		return posicionAnimacion == listaVerticesAnimacion.size();
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
	
	public TEstadoColision colision(Entidad entidad, InstanciaEntidad instanciaE)
	{	
		float posXEntidad = instanciaE.getPosicionX();
		float posYEntidad = instanciaE.getPosicionY();
		
		float widthEntidad = entidad.getWidth();	
		float heightEntidad = entidad.getHeight();
		
		if(posXEntidad + widthEntidad/2 < posicionX)
		{
			return TEstadoColision.EnemigoFueraRango;
		}
		else if(posXEntidad < posicionX + width/2 && posXEntidad > posicionX)
		{
			if(posicionY < posYEntidad + heightEntidad)
			{
				return TEstadoColision.Colision;
			}
		}
		
		return TEstadoColision.Nada;
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
