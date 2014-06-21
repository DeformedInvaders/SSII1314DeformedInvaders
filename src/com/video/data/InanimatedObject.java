package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeSticker;
import com.game.data.Entity;
import com.game.data.TTypeEntity;
import com.lib.buffer.Dimensions;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TStateVideo;

public class InanimatedObject extends Entity
{
	protected TStateVideo estadoActivo;
	protected int sonidoActivo;
	
	protected float posicionX, posicionY, factor;
	
	protected Rectangle area;
	protected Handle handle;
	
	public InanimatedObject(int id, int textura, float posX, float posY, TStateVideo estado, int sonido)
	{
		idEntidad = id;
		tipoEntidad = TTypeEntity.InanimatedObject;
		texturaEntidad = textura;
		
		posicionX = posX * GamePreferences.SCREEN_WIDTH_SCALE_FACTOR();
		posicionY = posY * GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
		
		factor = 1.0f;

		estadoActivo = estado;
		sonidoActivo = sonido;
	}
	
	protected int indiceObjeto()
	{
		return idEntidad;
	}
	
	/* Métodos Abstráctos de Entidad */
	
	@Override
	public void cargarTextura(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		Dimensions dim = renderer.cargarTexturaRectangulo(gl, texturaEntidad, tipoEntidad, idEntidad, TTypeSticker.Nothing);
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
		renderer.descargarTexturaRectangulo(tipoEntidad, idEntidad, TTypeSticker.Nothing);
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(posicionX, posicionY, 0.0f);
			gl.glScalef(GamePreferences.SCREEN_WIDTH_SCALE_FACTOR(), GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR(), 1.0f);
			gl.glScalef(factor, factor, 1.0f);
			renderer.dibujarTexturaRectangulo(gl, tipoEntidad, indiceObjeto(), TTypeSticker.Nothing);
		
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
	
	public TStateVideo getEstadoActivo()
	{
		return estadoActivo;
	}
	
	public int getSonidoActivo()
	{
		return sonidoActivo;
	}
	
	public void reposo()
	{
		
	}
}
