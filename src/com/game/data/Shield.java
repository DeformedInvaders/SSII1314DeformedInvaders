package com.game.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.TTypeSticker;
import com.main.model.GamePreferences;
import com.project.main.R;

public class Shield extends Entity
{
	private InstanceEntity entidad;
	
	private boolean activado;
	private int numVidas;
	
	public Shield(InstanceEntity instancia, int vidas)
	{
		entidad = instancia;
		activado = false;
		numVidas = vidas;
		
		if (entidad.getTipoEntidad() == TTypeEntity.Character)
		{
			tipoEntidad = TTypeEntity.CharacterShield;
		}
		else if (entidad.getTipoEntidad() == TTypeEntity.Boss)
		{
			tipoEntidad = TTypeEntity.BossShield;
		}
		else
		{
			tipoEntidad = TTypeEntity.Nothing;
		}
	}
	
	private int indiceBurbuja(int vidas)
	{
		if (tipoEntidad == TTypeEntity.CharacterShield)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_character_1;
				case 1:
					return R.drawable.bubble_character_2;
				default:
					return R.drawable.bubble_character_3;
			}
		}
		else if (tipoEntidad == TTypeEntity.BossShield)
		{
			switch (vidas)
			{
				case 0:
					return R.drawable.bubble_boss_1;
				case 1:
					return R.drawable.bubble_boss_2;
				default:
					return R.drawable.bubble_boss_3;
			}
		}
		
		return -1;
	}
	
	/* Métodos abstractos de Entidad */

	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
			{
				renderer.cargarTexturaRectangulo(gl, entidad.getHeight(), entidad.getWidth(), indiceBurbuja(i), tipoEntidad, i, TTypeSticker.Nothing);
			}
			
			width = entidad.getWidth();
			height = entidad.getHeight();
		}
	}
	
	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_BUBBLES; i++)
			{
				renderer.descargarTexturaRectangulo(tipoEntidad, i, TTypeSticker.Nothing);
			}
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (tipoEntidad != TTypeEntity.Nothing)
		{
			if (activado && numVidas > 0)
			{
				gl.glPushMatrix();
				
					gl.glTranslatef(entidad.getPosicionX(), entidad.getPosicionY(), 0.0f);
					gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 1.0f);
					renderer.dibujarTexturaRectangulo(gl, tipoEntidad, numVidas - 1, TTypeSticker.Nothing);
				
				gl.glPopMatrix();
			}
		}
	}
	
	/* Métodos de Modificación de Información */
	
	public void activarBurbuja()
	{
		activado = true;
	}
	
	public void desactivarBurbuja()
	{
		activado = false;
	}
	
	public void reiniciarVidas(int vidas)
	{
		numVidas = vidas;
	}
	
	public void quitarVida()
	{
		numVidas--;
	}
	
	/* Métodos de Obtención de Información */
	
	public boolean isAlive()
	{
		return numVidas >= 0;
	}
	
	public int getVidas()
	{
		return numVidas;
	}
}
