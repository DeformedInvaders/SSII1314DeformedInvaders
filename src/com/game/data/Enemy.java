package com.game.data;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class Enemy extends Mesh
{
	private List<VertexArray> movements;

	/* Constructora */
	
	public Enemy(int texture, int id)
	{
		typeEntity = TTypeEntity.Enemy;
		textureEntity = texture;
		idEntity = id;
	}

	/* Métodos abstractos de Entidad */
	
	@Override
	public void drawTexture(GL10 gl, OpenGLRenderer renderer)
	{
		if (skeletonReady && textureReady && movementsReady)
		{
			gl.glPushMatrix();
						
				gl.glRotatef(180, 0.0f, 1.0f, 0.0f);
				gl.glTranslatef(-getWidth(), 0.0f, 0.0f);
				gl.glScalef(GamePreferences.GAME_SCALE_FACTOR(typeEntity), GamePreferences.GAME_SCALE_FACTOR(typeEntity), 1.0f);
				
				// Pintura de Lados Traseros
				gl.glCullFace(GL10.GL_FRONT);
				
				super.drawTexture(gl, renderer);
				
				// Pintura de Lados Frontales
				gl.glCullFace(GL10.GL_BACK);

			gl.glPopMatrix();
		}
	}
	
	/* Métodos de Animación */
	
	@Override
	public boolean animateTexture()
	{
		boolean endAnimation = super.animateTexture();
		
		animationPosition = animationPosition % listAnimationVertex.size();
		
		return endAnimation;
	}
	
	/* Métodos de Modificación de Información */

	public void setMovements(List<VertexArray> m)
	{
		movements = m;
		movementsReady = true;
		
		listAnimationVertex = movements;
		stopAnimation();
		
		startAnimation();
	}
	
	/* Métodos de Obtención de Información */
	
	public List<VertexArray> getMovements()
	{
		return movements;
	}
}
