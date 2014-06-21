package com.video.data;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.TTypeSticker;
import com.game.data.TTypeEntity;
import com.lib.buffer.Dimensions;
import com.lib.math.Rectangle;
import com.main.model.GamePreferences;
import com.video.video.TStateVideo;

public class AnimatedObject extends InanimatedObject
{
	private TTypeAnimation tipoAnimacion;
	
	private int[] texturasAnimadas;
	private int indiceAnimacionPulsada, indiceAnimacionPasos, indiceAnimacionCiclica;
	private int ciclosAnimacion;
	
	protected int numeroCiclosEspera, numeroCiclosAnimacion;
	
	private boolean activado;
	
	public AnimatedObject(int id, int[] texturas, float posX, float posY, TStateVideo estado, int sonido, TTypeAnimation animacion)
	{
		super(id, -1, posX, posY, estado, sonido);
		
		tipoEntidad = TTypeEntity.AnimatedObject;
		tipoAnimacion = animacion;
		
		texturasAnimadas = texturas;
		indiceAnimacionPulsada = 0;
		indiceAnimacionPasos = 0;
		indiceAnimacionCiclica = 0;
				
		numeroCiclosEspera = GamePreferences.NUM_FRAMES_CYCLE;
		numeroCiclosAnimacion = GamePreferences.NUM_FRAMES_ANIMATION;
		
		ciclosAnimacion = numeroCiclosEspera;
		
		activado = false;
	}
	
	@Override
	protected int indiceObjeto()
	{
		if (tipoAnimacion == TTypeAnimation.Pressed)
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
		else if (tipoAnimacion == TTypeAnimation.Cyclic)
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
			Dimensions dim = renderer.cargarTexturaRectangulo(gl, texturasAnimadas[i], tipoEntidad, indiceObjeto(i), TTypeSticker.Nothing);
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
			renderer.descargarTexturaRectangulo(tipoEntidad, indiceObjeto(i), TTypeSticker.Nothing);
		}
	}

	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		super.dibujar(gl, renderer);
		
		ciclosAnimacion--;
		
		if (ciclosAnimacion == 0)
		{
			ciclosAnimacion = numeroCiclosEspera;
			
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
	}
	
	/* Métodos Públicos */
	
	@Override
	public boolean contains(float x, float y)
	{
		if (area == null)
		{
			return false;
		}
		
		if (area.contains(x, y))
		{
			if (!activado)
			{
				activado = true;
				ciclosAnimacion = numeroCiclosEspera;
				indiceAnimacionPulsada = numeroCiclosAnimacion;
			}
		
			if (indiceAnimacionPasos < GamePreferences.NUM_TYPE_TEXTURE_ANIMATED_OBJECTS - 1)
			{
				indiceAnimacionPasos++;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public void reposo()
	{
		super.reposo();
		
		indiceAnimacionPulsada = 0;
		indiceAnimacionPasos = 0;
		indiceAnimacionCiclica = 0;
		ciclosAnimacion = numeroCiclosEspera;;
		activado = false;
	}
}
