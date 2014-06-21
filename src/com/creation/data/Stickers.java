package com.creation.data;

import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.game.data.TTypeEntity;
import com.lib.buffer.StickerArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;
import com.main.model.GameResources;

public class Stickers implements Serializable
{
	private static final long serialVersionUID = 1L;

	private StickerArray pegatinas;

	/* Constructora */

	public Stickers()
	{
		pegatinas = new StickerArray();
	}
	
	/* Métodos de representación en renderer */
	
	public void cargarTexturas(GL10 gl, OpenGLRenderer renderer, Context context, TTypeEntity tipo, int id)
	{
		TTypeSticker[] tipoPegatinas = TTypeSticker.values();
		
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{			
			if (isCargada(tipoPegatinas[i]))
			{
				renderer.cargarTexturaRectangulo(gl, getIndice(tipoPegatinas[i], context), tipo, id, tipoPegatinas[i]);
			}
		}
	}
	
	public void descargarTextura(OpenGLRenderer renderer, TTypeEntity tipo, int id)
	{
		TTypeSticker[] tipoPegatinas = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			renderer.descargarTexturaRectangulo(tipo, id, tipoPegatinas[i]);
		}
	}
	
	public void dibujar(GL10 gl, OpenGLRenderer renderer, VertexArray vertices, TriangleArray triangulos, TTypeEntity tipo, int id)
	{
		float factorEscala = GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
		
		gl.glPushMatrix();
		
			gl.glTranslatef(0, 0, GamePreferences.DEEP_STICKERS);
			
			TTypeSticker[] tipoPegatinas = TTypeSticker.values();
			for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
			{				
				if (isCargada(tipoPegatinas[i]))
				{
					gl.glPushMatrix();
					
						gl.glTranslatef(pegatinas.getXCoords(tipoPegatinas[i], vertices, triangulos), pegatinas.getYCoords(tipoPegatinas[i], vertices, triangulos), 0.0f);
						gl.glScalef(factorEscala, factorEscala, 1.0f);
						gl.glRotatef(pegatinas.getKappaSticker(tipoPegatinas[i], vertices, triangulos), 0.0f, 0.0f, 1.0f);
						gl.glRotatef(pegatinas.getIotaSticker(tipoPegatinas[i]), 0.0f, 0.0f, 1.0f);
						gl.glRotatef(pegatinas.getThetaSticker(tipoPegatinas[i]), 0.0f, 0.0f, 1.0f);
						gl.glScalef(pegatinas.getFactorSticker(tipoPegatinas[i]), pegatinas.getFactorSticker(tipoPegatinas[i]), 1.0f);
						
						renderer.dibujarTexturaRectangulo(gl, tipo, id, tipoPegatinas[i]);
					
					gl.glPopMatrix();
				}
			}
			
		gl.glPopMatrix();
	}

	/* Métodos de Modificación de Información */

	public void anyadirPegatina(TTypeSticker tipo, int id, float x, float y, short index, float factor, float angulo, VertexArray vertices, TriangleArray triangulos)
	{
		if (id == -1)
		{
			eliminarPegatina(tipo);
		}
		else
		{
			pegatinas.setSticker(tipo, id, x, y, index, vertices, triangulos);
			pegatinas.setFactorSticker(tipo, factor);
			pegatinas.setThetaSticker(tipo, angulo);
		}
	}
	
	public void anyadirPegatina(TTypeSticker tipo, int id, float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		if (id == -1)
		{
			eliminarPegatina(tipo);
		}
		else
		{
			pegatinas.setSticker(tipo, id, x, y, index, vertices, triangulos);
		}
	}
	
	public void eliminarPegatina(TTypeSticker tipo)
	{
		pegatinas.removeSticker(tipo);	
	}
	
	public void eliminarPegatinas()
	{
		TTypeSticker[] tipoPegatinas = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			eliminarPegatina(tipoPegatinas[i]);
		}
	}
	
	public void ampliarPegatina(TTypeSticker tipo, float factor)
	{
		pegatinas.setFactorSticker(tipo, factor);
	}
	
	public void rotarPegatina(TTypeSticker tipo, float ang)
	{
		pegatinas.setThetaSticker(tipo, ang);
	}
	
	public void moverPegatina(TTypeSticker tipo, float x, float y, short index, VertexArray vertices, TriangleArray triangulos)
	{
		pegatinas.setCoords(tipo, x, y, index, vertices, triangulos);
	}
	
	public void recuperarPegatina(TTypeSticker tipo)
	{
		pegatinas.restoreSticker(tipo);
	}

	/* Métodos de Obtención de Información */
	
	public float getXCoords(TTypeSticker tipo, VertexArray vertices, TriangleArray triangulos)
	{
		return pegatinas.getXCoords(tipo, vertices, triangulos);
	}
	
	public float getYCoords(TTypeSticker tipo, VertexArray vertices, TriangleArray triangulos)
	{
		return pegatinas.getYCoords(tipo, vertices, triangulos);
	}
	
	public int getId(TTypeSticker tipo)
	{
		return pegatinas.getIdSticker(tipo);
	}
	
	public float getTheta(TTypeSticker tipo)
	{
		return pegatinas.getThetaSticker(tipo);
	}
	
	public float getFactor(TTypeSticker tipo)
	{
		return pegatinas.getFactorSticker(tipo);
	}
	
	public short getIndice(TTypeSticker tipo)
	{
		return pegatinas.getIndexSticker(tipo);
	}

	public boolean isCargada(TTypeSticker tipo)
	{
		return pegatinas.isLoadedSticker(tipo);
	}

	private int getIndice(TTypeSticker tipo, Context context)
	{
		String nombrePegatina = GameResources.GET_STICKER(tipo, pegatinas.getIdSticker(tipo));
		return context.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, context.getPackageName());
	}
}
