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

	private StickerArray stickers;

	/* Constructora */

	public Stickers()
	{
		stickers = new StickerArray();
	}
	
	/* Métodos de representación en renderer */
	
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context, TTypeEntity entity, int position)
	{
		TTypeSticker[] typeSticker = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{			
			if (isStickerLoaded(typeSticker[i]))
			{
				renderer.loadTextureRectangle(gl, getStickerId(typeSticker[i], context), entity, position, typeSticker[i]);
			}
		}
	}
	
	public void deleteTexture(OpenGLRenderer renderer, TTypeEntity entity, int id)
	{
		TTypeSticker[] typeSticker = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			renderer.deleteTextureRectangle(entity, id, typeSticker[i]);
		}
	}
	
	public void drawTexture(GL10 gl, OpenGLRenderer renderer, VertexArray vertices, TriangleArray triangles, TTypeEntity entity, int position)
	{
		float scaleFactor = GamePreferences.SCREEN_HEIGHT_SCALE_FACTOR();
		
		gl.glPushMatrix();
		
			gl.glTranslatef(0, 0, GamePreferences.DEEP_STICKERS);
			
			TTypeSticker[] typeSticker = TTypeSticker.values();
			for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
			{				
				if (isStickerLoaded(typeSticker[i]))
				{
					gl.glPushMatrix();
					
						gl.glTranslatef(stickers.getXCoords(typeSticker[i], vertices, triangles), stickers.getYCoords(typeSticker[i], vertices, triangles), 0.0f);
						gl.glScalef(scaleFactor, scaleFactor, 1.0f);
						gl.glRotatef(stickers.getKappaSticker(typeSticker[i], vertices, triangles), 0.0f, 0.0f, 1.0f);
						gl.glRotatef(stickers.getIotaSticker(typeSticker[i]), 0.0f, 0.0f, 1.0f);
						gl.glRotatef(stickers.getThetaSticker(typeSticker[i]), 0.0f, 0.0f, 1.0f);
						gl.glScalef(stickers.getFactorSticker(typeSticker[i]), stickers.getFactorSticker(typeSticker[i]), 1.0f);
						
						renderer.drawTextureRectangle(gl, entity, position, typeSticker[i]);
					
					gl.glPopMatrix();
				}
			}
			
		gl.glPopMatrix();
	}

	/* Métodos de Modificación de Información */

	public void addSticker(TTypeSticker sticker, int id, float x, float y, short index, float factor, float angle, VertexArray vertices, TriangleArray triangles)
	{
		if (id == -1)
		{
			deleteSticker(sticker);
		}
		else
		{
			stickers.setSticker(sticker, id, x, y, index, vertices, triangles);
			stickers.setFactorSticker(sticker, factor);
			stickers.setThetaSticker(sticker, angle);
		}
	}
	
	public void addSticker(TTypeSticker sticker, int id, float x, float y, short index, VertexArray vertices, TriangleArray triangles)
	{
		if (id == -1)
		{
			deleteSticker(sticker);
		}
		else
		{
			stickers.setSticker(sticker, id, x, y, index, vertices, triangles);
		}
	}
	
	public void deleteSticker(TTypeSticker sticker)
	{
		stickers.removeSticker(sticker);	
	}
	
	public void resetSticker()
	{
		TTypeSticker[] typeSticker = TTypeSticker.values();
		for (int i = 0; i < GamePreferences.NUM_TYPE_STICKERS; i++)
		{
			deleteSticker(typeSticker[i]);
		}
	}
	
	public void zoomSticker(TTypeSticker sticker, float factor)
	{
		stickers.setFactorSticker(sticker, factor);
	}
	
	public void rotateSticker(TTypeSticker sticker, float angle)
	{
		stickers.setThetaSticker(sticker, angle);
	}
	
	public void moveSticker(TTypeSticker sticker, float x, float y, short index, VertexArray vertices, TriangleArray triangles)
	{
		stickers.setCoords(sticker, x, y, index, vertices, triangles);
	}
	
	public void restoreSticker(TTypeSticker sticker)
	{
		stickers.restoreSticker(sticker);
	}

	/* Métodos de Obtención de Información */
	
	public float getXCoords(TTypeSticker sticker, VertexArray vertices, TriangleArray triangles)
	{
		return stickers.getXCoords(sticker, vertices, triangles);
	}
	
	public float getYCoords(TTypeSticker sticker, VertexArray vertices, TriangleArray triangles)
	{
		return stickers.getYCoords(sticker, vertices, triangles);
	}
	
	public int getId(TTypeSticker sticker)
	{
		return stickers.getIdSticker(sticker);
	}
	
	public float getTheta(TTypeSticker sticker)
	{
		return stickers.getThetaSticker(sticker);
	}
	
	public float getFactor(TTypeSticker sticker)
	{
		return stickers.getFactorSticker(sticker);
	}
	
	public short getIndex(TTypeSticker sticker)
	{
		return stickers.getIndexSticker(sticker);
	}

	public boolean isStickerLoaded(TTypeSticker sticker)
	{
		return stickers.isLoadedSticker(sticker);
	}

	private int getStickerId(TTypeSticker sticker, Context context)
	{
		String nombrePegatina = GameResources.GET_STICKER(sticker, stickers.getIdSticker(sticker));
		return context.getResources().getIdentifier(nombrePegatina, GameResources.RESOURCE_DRAWABLE, context.getPackageName());
	}
}
