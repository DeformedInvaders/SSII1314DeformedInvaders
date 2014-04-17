package com.creation.data;

import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.TTipoEntidad;
import com.lib.utils.FloatArray;
import com.project.model.GamePreferences;
import com.project.model.GameResources;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;

	// ID Drawable
	private int[] indicePegatinas;

	// Vertice Asociado
	private int[] verticePegatinas;

	/* Constructora */

	public Pegatinas()
	{
		indicePegatinas = new int[GamePreferences.NUM_TYPE_STICKERS];
		verticePegatinas = new int[GamePreferences.NUM_TYPE_STICKERS];

		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			indicePegatinas[i] = -1;
			verticePegatinas[i] = -1;
		}
	}
	
	/* Métodos de representación en renderer */
	
	public void cargarTexturas(GL10 gl, OpenGLRenderer renderer, Context context, TTipoEntidad tipo, int id)
	{
		TTipoSticker[] tipoPegatinas = TTipoSticker.values();
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{			
			if (isCargada(tipoPegatinas[i]))
			{
				renderer.cargarTexturaRectangulo(gl, getIndice(tipoPegatinas[i], context), tipo, id, tipoPegatinas[i]);
			}
		}
	}
	
	public void descargarTextura(OpenGLRenderer renderer, TTipoEntidad tipo, int id)
	{
		TTipoSticker[] tipoPegatinas = TTipoSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			renderer.descargarTexturaRectangulo(tipo, id, tipoPegatinas[i]);
		}
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, FloatArray vertices, TTipoEntidad tipo, int id)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(0, 0, GamePreferences.DEEP_STICKERS);
			
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
			{				
				if (isCargada(tipoPegatinas[i]))
				{
					int indice = getVertice(tipoPegatinas[i]);
					renderer.dibujarTexturaRectangulo(gl, vertices.get(2 * indice), vertices.get(2 * indice + 1), tipo, id, tipoPegatinas[i]);
				}
			}
			
		gl.glPopMatrix();
	}

	/* Métodos de Modificación de Información */

	public void setPegatina(int indice, int vertice, TTipoSticker tipo)
	{
		indicePegatinas[tipo.ordinal()] = indice;
		verticePegatinas[tipo.ordinal()] = vertice;
	}
	
	public void deletePegatina(TTipoSticker tipo)
	{
		indicePegatinas[tipo.ordinal()] = -1;
		verticePegatinas[tipo.ordinal()] = -1;	
	}

	/* Métodos de Obtención de Información */

	private boolean isCargada(TTipoSticker tipo)
	{
		return indicePegatinas[tipo.ordinal()] != -1;
	}

	private int getIndice(TTipoSticker tipo, Context context)
	{
		String nombrePegatina = GameResources.GET_STICKER(tipo, indicePegatinas[tipo.ordinal()]);
		return context.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, context.getPackageName());
	}

	private int getVertice(TTipoSticker tipo)
	{
		return verticePegatinas[tipo.ordinal()];
	}

}
