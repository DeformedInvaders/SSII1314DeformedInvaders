package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.main.model.GamePreferences;

public class Personaje extends Malla
{
	// Nombre
	private String nombre;
	
	// Movimientos
	private Movimientos movimientos;
	private TTipoMovimiento tipoMovimiento;
	
	// Escalado de Juego
	private boolean escalado;

	/* Constructora */
	
	public Personaje()
	{
		this(0);
	}

	public Personaje(int id)
	{
		tipoEntidad = TTipoEntidad.Personaje;
		idEntidad = id;
		texturaEntidad = -1;
		
		escalado = false;
	}

	/* Métodos abstractos de Entidad */
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			gl.glPushMatrix();
				
				if (escalado)
				{
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
				}
				
				super.dibujar(gl, renderer);			
	
			gl.glPopMatrix();
		}
	}

	/* Métodos de Animación */
	
	public void seleccionarAnimacion(TTipoMovimiento movimiento)
	{
		if (movimientosReady)
		{
			tipoMovimiento = movimiento;
			listaVerticesAnimacion = movimientos.get(tipoMovimiento);
			
			iniciar();
		}
	}

	/* Métodos de Modificación de Información */

	public void setMovimientos(Movimientos m)
	{
		movimientos = m;
		movimientosReady = true;
		
		reposo();
	}
	
	public void setNombre(String n)
	{
		nombre = n;
	}
	
	public void activarEscalado()
	{
		escalado = true;
	}
	
	public void desactivarEscalado()
	{
		escalado = false;
	}

	/* Métodos de Obtención de Información */

	public Movimientos getMovimientos()
	{
		return movimientos;
	}
	
	public String getNombre()
	{
		return nombre;
	}
	
	public TTipoMovimiento getMovimientoActual()
	{
		return tipoMovimiento;
	}
}
