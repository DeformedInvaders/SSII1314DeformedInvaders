package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Movimientos;
import com.creation.data.TTipoMovimiento;
import com.game.game.TEstadoColision;
import com.lib.math.Intersector;
import com.main.model.GamePreferences;

public class Personaje extends Malla
{
	// Nombre
	private String nombre;
	
	// Movimientos
	private Movimientos movimientos;
	private TTipoMovimiento tipoMovimiento;
	
	// Escalado
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
		
		posicionX = 0.0f;
		posicionY = 0.0f;	
		
		escalado = false;
	}

	/* Métodos abstractos de Entidad */
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			gl.glPushMatrix();
	
				gl.glTranslatef(posicionX, posicionY, 0.0f);
				
				if (escalado)
				{
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(), GamePreferences.GAME_SCALE_FACTOR(), 1.0f);
				}
				
				super.dibujar(gl, renderer);			
	
			gl.glPopMatrix();
		}
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(area.x, area.y, 0.0f);
				handle.dibujar(gl);
	
			gl.glPopMatrix();
		}
	}

	@Override
	public boolean animar()
	{
		boolean finAnimacion = super.animar();
		
		if (movimientosReady)
		{
			if (tipoMovimiento == TTipoMovimiento.Jump)
			{
				if (posicionAnimacion < 2 * listaVerticesAnimacion.size() / 6)
				{
					posicionY += GamePreferences.DIST_MOVIMIENTO_CHARACTER();
				}
				else if (posicionAnimacion >= 4 * listaVerticesAnimacion.size() / 6)
				{
					posicionY -= GamePreferences.DIST_MOVIMIENTO_CHARACTER();
				}
				
				moverArea(posicionX, posicionY);
			}
		}

		return finAnimacion;
	}
	
	public boolean animar(boolean desplazamiento)
	{
		if(desplazamiento)
		{
			return animar();
		}
		
		return super.animar();
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

	public TEstadoColision colision(Entidad entidad)
	{
		// Hay colisión entre el personaje y el enemigo
		if (Intersector.overlaps(area, entidad.getArea()))
		{
			entidad.restaurarArea();
			
			// Enemigo derrotado
			if (entidad.getTipo() == TTipoEntidad.Enemigo && tipoMovimiento == TTipoMovimiento.Attack)
			{
				return TEstadoColision.EnemigoDerrotado;
			}
			
			if (entidad.getTipo() == TTipoEntidad.Misil && tipoMovimiento == TTipoMovimiento.Crouch)
			{
				return TEstadoColision.Nada;
			}

			return TEstadoColision.Colision;
		}

		return TEstadoColision.Nada;
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
	
	public float getWidth()
	{
		return width * GamePreferences.GAME_SCALE_FACTOR();
	}
	
	public float getHeight()
	{
		return height * GamePreferences.GAME_SCALE_FACTOR();
	}
	
	public String getNombre()
	{
		return nombre;
	}
}
