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
	private int handleColor;
	private FloatBuffer bufferFill, bufferHull;

	/* Constructora */
	
	public Handle(float x, float y, float width, float height, int color)
	{
		handleColor = color;
		
		int longArray = 4 * 2;
		
		float[] vertices = new float[longArray];
		
		vertices[0] = x;
		vertices[1] = y;
		
		vertices[2] = x;
		vertices[3] = y + height;
		
		vertices[4] = x + width;
		vertices[5] = y + height;
		
		vertices[6] = x + width;
		vertices[7] = y;
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		bufferFill = byteBuf.asFloatBuffer();
		bufferFill.put(vertices);
		bufferFill.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		bufferHull = byteBuf.asFloatBuffer();
		bufferHull.put(vertices);
		bufferHull.position(0);
	}

	public Handle(int numIter, float radio, int color)
	{
		handleColor = color;
		
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
		bufferFill = byteBuf.asFloatBuffer();
		bufferFill.put(vertices);
		bufferFill.position(0);
		
		byteBuf = ByteBuffer.allocateDirect(contorno.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		bufferHull = byteBuf.asFloatBuffer();
		bufferHull.put(contorno);
		bufferHull.position(0);
	}

	/* Métodos de Obtención de Información */
	
	public void dibujar(GL10 gl)
	{
		OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, handleColor, bufferFill);
		OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, bufferHull);
	}
}
