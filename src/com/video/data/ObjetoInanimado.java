package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTipoSticker;
import com.game.data.Entidad;
import com.game.data.TTipoEntidad;
import com.lib.buffer.Dimensiones;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TEstadoVideo;

public class ObjetoInanimado extends Entidad
{
	protected TEstadoVideo estadoActivo;
	protected int sonidoActivo;
	
	protected float posicionX, posicionY;
	
	protected Rectangle area;
	protected Handle handle;
	
	public ObjetoInanimado(int id, int textura, float posX, float posY, TEstadoVideo estado, int sonido)
	{
		idEntidad = id;
		tipoEntidad = TTipoEntidad.ObjetoInanimado;
		texturaEntidad = textura;
		
		posicionX = posX * GamePreferences.SCREEN_WIDTH_SCALE_FACTOR();
		posicionY = posY * GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();

		estadoActivo = estado;
		sonidoActivo = sonido;
	}
	
	/* Métodos Abstráctos de Entidad */
	
	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensiones dim = renderer.cargarTexturaRectangulo(gl, texturaEntidad, tipoEntidad, idEntidad, TTipoSticker.Nada);
		if (dim != null)
		{
			width = dim.getWidth();
			height = dim.getHeight();
		}
		
		area = new Rectangle(posicionX, posicionY, getWidth(), getHeight());
		handle = new Handle(area.getX(), area.getY(), area.getWidth(), area.getHeight(), Color.BLUE);
	}

	@Override
	public void descargarTextura(OpenGLRenderer renderer)
	{
		renderer.descargarTexturaRectangulo(tipoEntidad, idEntidad, TTipoSticker.Nada);
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(posicionX, posicionY, 0.0f);
			gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), GamePreferences.GAME_SCALE_FACTOR(tipoEntidad), 0.0f);
			renderer.dibujarTexturaRectangulo(gl, tipoEntidad, idEntidad, TTipoSticker.Nada);
		
		gl.glPopMatrix();
		
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			handle.dibujar(gl);
		}
	}
	
	@Override
	public float getHeight()
	{
		return height * GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
	}
	
	@Override
	public float getWidth()
	{
		return width * GamePreferences.SCREEN_WIDTH_SCALE_FACTOR();
	}
	
	/* Métodos Públicos */
	
	public boolean contains(float x, float y)
	{
		if (area == null)
		{
			return false;
		}
		
		return area.contains(x, y);
	}
	
	public TEstadoVideo getEstadoActivo()
	{
		return estadoActivo;
	}
	
	public int getSonidoActivo()
	{
		return sonidoActivo;
	}
}
