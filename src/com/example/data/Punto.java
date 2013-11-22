package com.example.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Punto {

	private float[] coord;
	private FloatBuffer buffer;
	
	public Punto() {
		this.coord = new float[3];
		this.coord[0] = 0.0f;
		this.coord[1] = 0.0f;
		this.coord[2] = 0.0f;
		
		this.actualizarBuffer();
	}
	
	public Punto(float x, float y) {
		this.coord = new float[3];
		this.coord[0] = x;
		this.coord[1] = y;
		this.coord[2] = 0.0f;
		
		this.actualizarBuffer();
	}
	
	public Punto(float x, float y, float z) {
		this.coord = new float[3];
		this.coord[0] = x;
		this.coord[1] = y;
		this.coord[2] = z;
		
		this.actualizarBuffer();
	}
	
	public float getX() {
		return this.coord[0];
	}
	
	public float getY() {
		return this.coord[1];
	}
	
	public float getZ() {
		return this.coord[2];
	}
	
	public void setX(float x) {
		this.coord[0] = x;
		
		this.actualizarBuffer();
	}
	
	public void setY(float y) {
		this.coord[1] = y;
		
		this.actualizarBuffer();
	}
	
	public void setZ(float z) {
		this.coord[2] = z;
		
		this.actualizarBuffer();
	}
	
	private void actualizarBuffer() {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(coord.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		buffer = byteBuf.asFloatBuffer();
		buffer.put(coord);
		buffer.position(0);
	}
	
	public void dibujar(GL10 gl) {
		
		// Color
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		
		// Size Point
		gl.glPointSize(10.0f);
		
		//Point to our vertex buffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
		
		//Enable vertex buffer
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		
			//Draw the vertices as triangle strip
			gl.glDrawArrays(GL10.GL_POINTS, 0, 1);
		
		//Disable the client state before leaving
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
