package com.creation.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Handle
{
	private FloatBuffer bufferRelleno, bufferContorno;

	/* Constructora */

	public Handle(int numIter, float radio)
	{
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

	public FloatBuffer getBufferRelleno()
	{
		return bufferRelleno;
	}
	
	public FloatBuffer getBufferContorno()
	{
		return bufferContorno;
	}
}
