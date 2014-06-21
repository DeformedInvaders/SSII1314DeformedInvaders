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
			OpenGLManager.dibujarBuffer(gl, Color.BLACK, bufferAnimationHull);
			
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
			bufferAnimationTriangles = BufferManager.construirBufferListaTriangulosRellenos(triangles, animationVertices);
			bufferAnimationHull = BufferManager.construirBufferListaIndicePuntos(hull, animationVertices);
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
			BufferManager.actualizarBufferListaTriangulosRellenos(bufferAnimationTriangles, triangles, animationVertices);
			BufferManager.actualizarBufferListaIndicePuntos(bufferAnimationHull, hull, animationVertices);
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

		bufferHull = BufferManager.construirBufferListaIndicePuntos(hull, vertices);
		bufferTriangles = BufferManager.construirBufferListaTriangulosRellenos(triangles, vertices);
		
		skeletonReady = true;
	}

	public void setTexture(Texture t)
	{
		texture = t;
		stickers = texture.getStickers();
		textureCoords = BufferManager.construirBufferListaTriangulosRellenos(triangles, texture.getTextureCoords());
		textureReady = true;
		
		width = texture.getWidth();
		height = texture.getHeight();
	}
	
	/* Métodos de Obtención de Información */	
	public int getAnimationLength()
	{
		if (listAnimationVertex != null)
		{
			return listAnimationVertex.size();
		}
		
		return 0;
	}

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
