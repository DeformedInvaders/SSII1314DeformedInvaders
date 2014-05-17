package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTipoSticker;
import com.game.data.TTipoEntidad;
import com.lib.buffer.Dimensiones;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TEstadoVideo;

public class ObjetoAnimado extends ObjetoInanimado
{
	private int[] texturasAnimadas;
	private int indiceAnimacionTextura;
	
	private boolean activado;
	
	public ObjetoAnimado(int id, int[] texturas, float posX, float posY, TEstadoVideo estado, int sonido)
	{
		super(id, -1, posX, posY, estado, sonido);
		
		tipoEntidad = TTipoEntidad.ObjetoAnimado;
		
		texturasAnimadas = texturas;
		indiceAnimacionTextura = 0;
		
		activado = false;
	}
	
	private int indiceObjeto()
	{
		if (activado)
		{
			return indiceObjeto(indiceAnimacionTextura % GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS);
		}
		else
		{
			return indiceObjeto(0);
		}
	}
	
	private int indiceObjeto(int indice)
	{
		return idEntidad * GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS + indice;
	}
	
	/* Métodos Abstráctos de Entidad */
	
	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS; i++)
		{
			Dimensiones dim = renderer.cargarTexturaRectangulo(gl, texturasAnimadas[i], tipoEntidad, indiceObjeto(i), TTipoSticker.Nada);
			if (dim != null)
			{
				width = dim.getWidth();
				height = dim.getHeight();
			}
		}
		
		area = new Rectangle(posicionX, posicionY, getWidth(), getHeight());
		handle = new Handle(area.getX(), area.getY(), area.getWidth(), area.getHeight(), Color.RED);
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS; i++)
		{
			renderer.descargarTexturaRectangulo(tipoEntidad, indiceObjeto(i), TTipoSticker.Nada);
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(posicionX, posicionY, 0.0f);
			gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 0.0f);
			renderer.dibujarTexturaRectangulo(gl, tipoEntidad, indiceObjeto(), TTipoSticker.Nada);
		
		gl.glPopMatrix();
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			handle.dibujar(gl);
		}
		
		if (activado)
		{
			if (indiceAnimacionTextura > 0)
			{
				indiceAnimacionTextura--;
			}
			else
			{
				activado = false;
			}
		}
	}
	
	/* Métodos Públicos */
	
	public boolean contains(float x, float y)
	{
		if (area == null)
		{
			return false;
		}
		
		if (area.contains(x, y))
		{
			activado = true;
			indiceAnimacionTextura = 2 * GamePreferences.NUM_FRAMES_ANIMATION;
			return true;
		}
		
		return false;
	}
}
