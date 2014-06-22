package com.game.data;

import java.nio.FloatBuffer;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.creation.data.Skeleton;
import com.creation.data.Stickers;
import com.creation.data.Texture;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;

public abstract class Mesh extends Entity
{
	// Esqueleto
	protected HullArray hull;
	protected FloatBuffer bufferHull;

	protected VertexArray vertices;

	protected TriangleArray triangles;
	protected FloatBuffer bufferTriangles;

	// Animación
	protected List<VertexArray> listAnimationVertex;

	protected int animationPosition;
	protected VertexArray animationVertices;
	protected FloatBuffer bufferAnimationTriangles;
	protected FloatBuffer bufferAnimationHull;

	// Texturas
	protected Texture texture;
	protected FloatBuffer textureCoords;

	// Pegatinas
	protected Stickers stickers;

	protected boolean skeletonReady, textureReady, movementsReady;
	
	/* Métodos abstractos de Entidad */

	@Override
	public void loadTexture(GL10 gl, OpenGLRenderer renderer, Context context)
	{
		if (textureReady)
		{
			// Textura
			texture.loadTexture(gl, renderer, context, typeEntity, idEntity);
	
			// Pegatinas
			stickers.loadTexture(gl, renderer, context, typeEntity, idEntity);
		}
	}

	@Override
	public void deleteTexture(OpenGLRenderer renderer)
	{
		if (textureReady)
		{
			// Textura
			texture.deleteTexture(renderer, typeEntity, idEntity);
	
			// Pegatinas
			stickers.deleteTexture(renderer, typeEntity, idEntity);
		}
	}
	
	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		if (skeletonReady && textureReady && movementsReady)
		{
			// Textura
			texture.drawTexture(gl, renderer, bufferAnimationTriangles, textureCoords, typeEntity, idEntity);
	
			// Contorno
			OpenGLManager.drawBuffer(gl, Color.BLACK, bufferAnimationHull);
			
			// Pegatinas
			stickers.drawTexture(gl, renderer, animationVertices, triangles, typeEntity, idEntity);
		}
	}

	/* Métodos de Animación */
	
	protected void startAnimation()
	{
		if (movementsReady)
		{	
			animationPosition = 0;
			animationVertices = listAnimationVertex.get(animationPosition);
			bufferAnimationTriangles = BufferManager.buildBufferTriangleFillList(triangles, animationVertices);
			bufferAnimationHull = BufferManager.buildBufferVertexIndexList(hull, animationVertices);
		}
	}

	public void stopAnimation()
	{
		if (movementsReady)
		{			
			animationVertices = vertices;
			bufferAnimationTriangles = bufferTriangles;
			bufferAnimationHull = bufferHull;
		}
	}

	public boolean animateTexture()
	{ 
		if (movementsReady)
		{
			animationVertices = listAnimationVertex.get(animationPosition);
			BufferManager.updateBufferTriangleFillList(bufferAnimationTriangles, triangles, animationVertices);
			BufferManager.updateBufferVertexIndexList(bufferAnimationHull, hull, animationVertices);
			animationPosition++;
	
			return animationPosition == listAnimationVertex.size() - 1;
		}
		
		return false;
	}

	/* Métodos de Modificación de Información */

	public void setSkeleton(Skeleton s)
	{
		hull = s.getHull();
		vertices = s.getVertices();
		triangles = s.getTriangles();

		bufferHull = BufferManager.buildBufferVertexIndexList(hull, vertices);
		bufferTriangles = BufferManager.buildBufferTriangleFillList(triangles, vertices);
		
		skeletonReady = true;
	}

	public void setTexture(Texture t)
	{
		texture = t;
		stickers = texture.getStickers();
		textureCoords = BufferManager.buildBufferTriangleFillList(triangles, texture.getTextureCoords());
		textureReady = true;
		
		width = texture.getWidth();
		height = texture.getHeight();
	}
	
	/* Métodos de Obtención de Información */	

	public boolean isSkeletonReady()
	{
		return skeletonReady;
	}
	
	public boolean isTextureReady()
	{
		return textureReady;
	}
	
	public boolean isMovementsReady()
	{
		return movementsReady;
	}
	
	public Skeleton getSkeleton()
	{ 
		if(skeletonReady)
		{
			return new Skeleton(hull, vertices, triangles);
		}
		
		return null;
	}

	public Texture getTexture()
	{
		if (textureReady)
		{
			return texture;
		}
		
		return null;
	}
}
