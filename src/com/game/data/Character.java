package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Movements;
import com.creation.data.TTypeMovement;
import com.main.model.GamePreferences;

public class Character extends Mesh
{
	// Nombre
	private String nombre;
	
	// Movimientos
	private Movements movimientos;
	private TTypeMovement tipoMovimiento;

	/* Constructora */
	
	public Character()
	{
		this(0);
	}

	public Character(int id)
	{
		tipoEntidad = TTypeEntity.Character;
		idEntidad = id;
		texturaEntidad = -1;
	}

	/* M�todos abstractos de Entidad */
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			gl.glPushMatrix();
				
				gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
				super.dibujar(gl, renderer);			
	
			gl.glPopMatrix();
		}
	}

	/* M�todos de Animaci�n */
	
	public void seleccionarAnimacion(TTypeMovement movimiento)
	{
		if (movimientosReady)
		{
			tipoMovimiento = movimiento;
			listaVerticesAnimacion = movimientos.get(tipoMovimiento);
			
			iniciar();
		}
	}

	/* M�todos de Modificaci�n de Informaci�n */

	public void setMovimientos(Movements m)
	{
		movimientos = m;
		movimientosReady = true;
		
		reposo();
	}
	
	public void setNombre(String n)
	{
		nombre = n;
	}

	/* M�todos de Obtenci�n de Informaci�n */

	public Movements getMovimientos()
	{
		return movimientos;
	}
	
	public String getNombre()
	{
		return nombre;
	}
	
	public TTypeMovement getMovimientoActual()
	{
		return tipoMovimiento;
	}
}
