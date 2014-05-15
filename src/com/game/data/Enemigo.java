package com.game.data;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import com.android.opengl.OpenGLRenderer;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class Enemigo extends Malla
{
	private List<VertexArray> movimiento;

	/* Constructora */
	
	public Enemigo(int indiceTextura, int idEnemigo)
	{
		tipoEntidad = TTipoEntidad.Enemigo;
		texturaEntidad = indiceTextura;
		idEntidad = idEnemigo;
	}

	/* M�todos abstractos de Entidad */
	
	@Override
	public void dibujar(GL10 gl, OpenGLRenderer renderer)
	{
		if (esqueletoReady && texturaReady && movimientosReady)
		{
			float factorEscala = GamePreferences.SCREEN_SCALE_FACTOR() * GamePreferences.GAME_SCALE_FACTOR();
			
			gl.glPushMatrix();
						
				gl.glRotatef(180, 0.0f, 1.0f, 0.0f);
				gl.glTranslatef(-getWidth(), 0.0f, 0.0f);
				gl.glScalef(factorEscala, factorEscala, 1.0f);
				
				// Pintura de Lados Traseros
				gl.glCullFace(GL10.GL_FRONT);
				
				super.dibujar(gl, renderer);
				
				// Pintura de Lados Frontales
				gl.glCullFace(GL10.GL_BACK);

			gl.glPopMatrix();
		}
	}
	
	/* M�todos de Animaci�n */
	
	@Override
	public boolean animar()
	{
		boolean finAnimacion = super.animar();
		
		posicionAnimacion = posicionAnimacion % listaVerticesAnimacion.size();
		
		return finAnimacion;
	}
	
	/* M�todos de Modificaci�n de Informaci�n */

	public void setMovimientos(List<VertexArray> m)
	{
		movimiento = m;
		movimientosReady = true;
		
		listaVerticesAnimacion = movimiento;
		reposo();
		
		iniciar();
	}
	
	/* M�todos de Obtenci�n de Informaci�n */
	
	public List<VertexArray> getMovimientos()
	{
		return movimiento;
	}
}
