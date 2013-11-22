package com.example.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cuadrado {

	private FloatBuffer buffer;
	
	private float vertices[] = { 
		-100.0f,	-100.0f,
		100.0f,		-100.0f,
		-100.0f,	100.0f,
		100.0f,		100.0f
	};
	
	public Cuadrado() {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		buffer = byteBuf.asFloatBuffer();
		buffer.put(vertices);
		buffer.position(0);
	}
	
	public void dibujar(GL10 gl) {
		//Set the face rotation
		gl.glFrontFace(GL10.GL_CW);
		
		//Point to our vertex buffer
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, buffer);
		
		//Enable vertex buffer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
			//Draw the vertices as triangle strip
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 2);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
