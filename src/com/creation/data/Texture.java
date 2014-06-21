package com.creation.data;

import java.io.Serializable;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.android.opengl.OpenGLRenderer;
import com.game.data.TTypeEntity;
import com.lib.buffer.VertexArray;

public class Texture implements Serializable
{
	private static final long serialVersionUID = 1L;

	private BitmapImage bitmap;
	private VertexArray textureCoords;
	private Stickers stickers;

	/* Constructora */

	public Texture(BitmapImage bitmap, VertexArray textureCoords, Stickers stickers)
	{
		this.bitmap = bitmap;
		this.textureCoords = textureCoords;
		this.stickers = stickers;
	}

	/* Métodos de Obtención de Información */

	public BitmapImage getBitmap()
	{
		return bitmap;
	}

	public VertexArray getTextureCoords()
	{
		return textureCoords;
	}

	public Stickers getStickers()
	{
		return stickers;
	}
	
	public int getHeight()
	{
		return bitmap.getHeight();
	}
	
	public int getWidth()
	{
		return bitmap.getWidth();
	}
	
	/* Métodos de representación en renderer */
	
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context, TTypeEntity entity, int position)
	{
		renderer.loadTextureMesh(gl, bitmap.getBitmap(), entity, position);
	}

	public void deleteTexture(OpenGLRenderer renderer, TTypeEntity entity, int position)
	{
		renderer.deleteTextureMesh(entity, position);
	}

	public void drawTexture(GL10 gl, OpenGLRenderer renderer, FloatBuffer triangulos, FloatBuffer textureCoords, TTypeEntity entity, int position)
	{
		renderer.drawTextureMesh(gl, triangulos, textureCoords, entity, position);
	}
}
