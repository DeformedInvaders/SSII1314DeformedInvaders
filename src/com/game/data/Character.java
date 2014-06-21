package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Movements;
import com.creation.data.TTypeMovement;
import com.main.model.GamePreferences;

public class Character extends Mesh
{
	// Nombre
	private String name;
	
	// Movimientos
	private Movements movements;
	private TTypeMovement movementActual;

	/* Constructora */
	
	public Character()
	{
		this(0);
	}

	public Character(int id)
	{
		typeEntity = TTypeEntity.Character;
		idEntity = id;
		textureEntity = -1;
	}

	/* Métodos abstractos de Entidad */
	
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		if (skeletonReady && textureReady && movementsReady)
		{
			gl.glPushMatrix();
				
				gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
				super.drawTexture(gl, renderer);			
	
			gl.glPopMatrix();
		}
	}

	/* Métodos de Animación */
	
	public void selectMovement(TTypeMovement movimiento)
	{
		if (movementsReady)
		{
			movementActual = movimiento;
			listAnimationVertex = movements.get(movementActual);
			
			startAnimation();
		}
	}

	/* Métodos de Modificación de Información */

	public void setMovements(Movements m)
	{
		movements = m;
		movementsReady = true;
		
		stopAnimation();
	}
	
	public void setName(String n)
	{
		name = n;
	}

	/* Métodos de Obtención de Información */

	public Movements getMovements()
	{
		return movements;
	}
	
	public String getName()
	{
		return name;
	}
	
	public TTypeMovement getMovementActual()
	{
		return movementActual;
	}
}
