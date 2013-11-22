package com.example.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;

import javax.microedition.khronos.opengles.GL10;

public class Polilinea {

	private ArrayList<Punto> vertices;
	private FloatBuffer buffer;
	
	public Polilinea() {
		
		this.vertices = new ArrayList<Punto>();
	}
	
	public Polilinea(Punto p) {
		
		this.vertices = new ArrayList<Punto>();
		this.vertices.add(p);
	}
	
	public Polilinea(Collection<Punto> c) {
		
		this.vertices = new ArrayList<Punto>(c);
	}
	
	public boolean anyadirPunto(Punto p) {
		
		this.vertices.add(p);

		float[] arrayVertices = new float[3 * vertices.size()];
		for(int i = 0; i < vertices.size(); i++) {
			Punto q = vertices.get(i);
			arrayVertices[3*i] = q.getX();
			arrayVertices[3*i+1] = q.getY();
			arrayVertices[3*i+2] = q.getZ();
		}
				
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(3 * vertices.size() * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		buffer = byteBuf.asFloatBuffer();
		buffer.put(arrayVertices);
		buffer.position(0);
		
		return true;
	}
	
	public void dibujar(GL10 gl) {
		
		/* Dibujar Segmentos de la Polilínea */
		
		if(vertices.size() > 1) {
			// Color
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			// Width Line
			gl.glLineWidth(3.0f);
			
			//Set the face rotation
			gl.glFrontFace(GL10.GL_CW);
			
			//Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, buffer);
			
			//Enable vertex buffer
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
				//Draw the vertices as triangle strip
				gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, vertices.size());
			
			//Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
		
		/* Dibujar Puntos */
		
		for(int i = 0; i < vertices.size(); i++) {
			vertices.get(i).dibujar(gl);
		}
	}
}
