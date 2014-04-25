package com.creation.data;

import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.view.OpenGLRenderer;
import com.game.data.TTipoEntidad;
import com.lib.buffer.StickerArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;
import com.main.model.GameResources;

public class Pegatinas implements Serializable
{
	private static final long serialVersionUID = 1L;

	private StickerArray pegatinas;

	/* Constructora */

	public Pegatinas()
	{
		pegatinas = new StickerArray();
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
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, VertexArray vertices, TriangleArray triangulos, TTipoEntidad tipo, int id)
	{
		float factorEscala = GamePreferences.SCREEN_SCALE_FACTOR();
		
		gl.glPushMatrix();
		
			gl.glTranslatef(0, 0, GamePreferences.DEEP_STICKERS);
			
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
			{				
				if (isCargada(tipoPegatinas[i]))
				{
					gl.glPushMatrix();
					
						gl.glTranslatef(pegatinas.getXCoords(tipoPegatinas[i], vertices, triangulos), pegatinas.getYCoords(tipoPegatinas[i], vertices, triangulos), 0.0f);
						gl.glScalef(factorEscala, factorEscala, 1.0f);
						
						renderer.dibujarTexturaRectangulo(gl, tipo, id, tipoPegatinas[i]);
					
					gl.glPopMatrix();
				}
			}
			
		gl.glPopMatrix();
	}

	/* Métodos de Modificación de Información */

	public void setPegatina(TTipoSticker tipo, int id, float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		pegatinas.setSticker(tipo, id, x, y, index, vertices, triangulos);
	}
	
	public void eliminarPegatinas(TTipoSticker tipo)
	{
		pegatinas.removeSticker(tipo);	
	}
	
	public void eliminarPegatinas()
	{
		TTipoSticker[] tipoPegatinas = TTipoSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			pegatinas.removeSticker(tipoPegatinas[i]);
		}
	}
	
	public void ocultarPegatinas()
	{
		TTipoSticker[] tipoPegatinas = TTipoSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			pegatinas.hideStickers(tipoPegatinas[i]);
		}
	}
	
	public void mostrarPegatina(TTipoSticker tipo)
	{
		pegatinas.showSticker(tipo);
	}

	/* Métodos de Obtención de Información */

	private boolean isCargada(TTipoSticker tipo)
	{
		return pegatinas.isLoadedSticker(tipo);
	}

	private int getIndice(TTipoSticker tipo, Context context)
	{
		String nombrePegatina = GameResources.GET_STICKER(tipo, pegatinas.getIdSticker(tipo));
		return context.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, context.getPackageName());
	}
}
