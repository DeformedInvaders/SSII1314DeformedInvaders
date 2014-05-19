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
	private TTipoAnimacion tipoAnimacion;
	
	private int[] texturasAnimadas;
	private int indiceAnimacionPulsada, indiceAnimacionPasos, indiceAnimacionCiclica;
	
	private boolean activado;
	
	public ObjetoAnimado(int id, int[] texturas, float posX, float posY, TEstadoVideo estado, int sonido, TTipoAnimacion animacion)
	{
		super(id, -1, posX, posY, estado, sonido);
		
		tipoEntidad = TTipoEntidad.ObjetoAnimado;
		tipoAnimacion = animacion;
		
		texturasAnimadas = texturas;
		indiceAnimacionPulsada = 0;
		indiceAnimacionPasos = 0;
		indiceAnimacionCiclica = 0;
		
		activado = false;
	}
	
	private int indiceObjeto()
	{
		if (tipoAnimacion == TTipoAnimacion.Pulsado)
		{
			if (activado)
			{
				return indiceObjeto(indiceAnimacionPulsada % GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS);
			}
			else
			{
				return indiceObjeto(0);
			}
		}
		if (tipoAnimacion == TTipoAnimacion.Ciclico)
		{
			return indiceObjeto(indiceAnimacionCiclica);
		}
		else
		{
			return indiceObjeto(indiceAnimacionPasos);
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
			if (indiceAnimacionPulsada > 0)
			{
				indiceAnimacionPulsada--;
			}
			else
			{
				activado = false;
			}
		}
		
		indiceAnimacionCiclica = (indiceAnimacionCiclica + 1) % GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS;
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
			indiceAnimacionPulsada = GamePreferences.NUM_FRAMES_ANIMATION;
		
			if (indiceAnimacionPasos < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS - 1)
			{
				indiceAnimacionPasos++;
			}
			
			return true;
		}
		
		return false;
	}
}
