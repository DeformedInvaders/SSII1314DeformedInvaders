package com.create.deform;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Handle
{
	private FloatBuffer buffer;
	
	public Handle(int numIter, float radio)
	{
		int longVertices = (numIter+1)*2;
		int longArray = longVertices+2;
		float[] vertices = new float[longArray];
		vertices[0] = 0.0f;
		vertices[1] = 0.0f;
		
		int i = 0;
		while(i < longVertices)
		{
			double theta = (double) (2.0f * Math.PI * i) / (double) numIter;
			
			vertices[longArray-1-(i+1)] = radio *(float) Math.cos(theta);
			vertices[longArray-1-i] = radio * (float) Math.sin(theta);
			
			i = i+2;
		}
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		buffer = byteBuf.asFloatBuffer();
		buffer.put(vertices);
		buffer.position(0);
	}
	
	public FloatBuffer getBuffer()
	{
		return buffer;
	}
}
