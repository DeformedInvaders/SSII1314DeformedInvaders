package com.creation.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.lib.opengl.OpenGLManager;
import com.main.model.GamePreferences;

public class Handle
{
	private int colorHandle;
	private FloatBuffer bufferRelleno, bufferContorno;

	/* Constructora */

	public Handle(int numIter, float radio, int color)
	{
		colorHandle = color;
		
		int longVertices = (numIter + 1) * 2;
		int longArray = longVertices + 2;
		
		float[] vertices = new float[longArray];
		float[] contorno = new float[longVertices];
		
		vertices[0] = 0.0f;
		vertices[1] = 0.0f;

		int i = 0;
		while (i < longVertices)
		{
			double theta = 2.0f * Math.PI * i / numIter;
			
			float posX = radio * (float) Math.sin(theta);
			float posY = radio * (float) Math.cos(theta);
			
			vertices[longArray - 1 - (i + 1)] = posY;
			vertices[longArray - 1 - i] = posX;
		
			contorno[longVertices - 1 - (i + 1)] = posY;
			contorno[longVertices - 1 - i] = posX;

			i = i + 2;
		}

		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		bufferRelleno = byteBuf.asFloatBuffer();
		bufferRelleno.put(vertices);
		bufferRelleno.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(contorno.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		bufferContorno = byteBuf.asFloatBuffer();
		bufferContorno.put(contorno);
		bufferContorno.position(0);
	}

	/* Métodos de Obtención de Información */
	
	public void dibujar(GL10 gl)
	{
		OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, colorHandle, bufferRelleno);
		OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, bufferContorno);
	}
}
