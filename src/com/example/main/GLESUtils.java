package com.example.main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class GLESUtils
{
	public static FloatBuffer construirBuffer(FloatArray lista)
	{	
		int arrayLong = lista.size;
		float[] arrayPuntos = new float[arrayLong];
		for(int i = 0; i < lista.size; i++)
		{
			arrayPuntos[i] = lista.get(i);
		}
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(arrayPuntos);
		buffer.position(0);
		
		return buffer;
	}
	
	public static ArrayList<FloatBuffer> construirLineasBuffer(ShortArray lista, FloatArray puntos)
	{
		int arrayLong = 2 * 2;
		ArrayList<FloatBuffer> listabuffer = new ArrayList<FloatBuffer>();
		
		int j = 0;
		while(j < lista.size)
		{
			short a = lista.get(j);
			short b = lista.get(j+1);
			
			float[] arrayPuntos = new float[arrayLong];
			
			arrayPuntos[0] = puntos.get(2*a);
			arrayPuntos[1] = puntos.get(2*a+1);
			
			arrayPuntos[2] = puntos.get(2*b);
			arrayPuntos[3] = puntos.get(2*b+1);		
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			FloatBuffer buffer = byteBuf.asFloatBuffer();
			buffer.put(arrayPuntos);
			buffer.position(0);

			listabuffer.add(buffer);
			
			j = j+2;
		}		
		
		return listabuffer;
	}
	
	public static ArrayList<FloatBuffer> construirTriangulosBuffer(ShortArray lista, FloatArray puntos)
	{
		int arrayLong = 2 * 3;
		ArrayList<FloatBuffer> listabuffer = new ArrayList<FloatBuffer>();
		
		int j = 0;
		while(j < lista.size)
		{
			short a = lista.get(j);
			short b = lista.get(j+1);
			short c = lista.get(j+2);
			
			float[] arrayPuntos = new float[arrayLong];
			
			arrayPuntos[0] = puntos.get(2*a);
			arrayPuntos[1] = puntos.get(2*a+1);
			
			arrayPuntos[2] = puntos.get(2*b);
			arrayPuntos[3] = puntos.get(2*b+1);
			
			arrayPuntos[4] = puntos.get(2*c);
			arrayPuntos[5] = puntos.get(2*c+1);			
			
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(arrayLong * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			FloatBuffer buffer = byteBuf.asFloatBuffer();
			buffer.put(arrayPuntos);
			buffer.position(0);

			listabuffer.add(buffer);
			
			j = j+3;
		}		
		
		return listabuffer;
	}

	public static void dibujarBuffer(GL10 gl, int type, float size, float r, float g, float b, float a, FloatBuffer lista)
	{	
		gl.glColor4f(r, g, b, a);
		gl.glLineWidth(size);
		gl.glFrontFace(GL10.GL_CW);
		
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, lista);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDrawArrays(type, 0, lista.capacity() / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public static void dibujarBuffer(GL10 gl, int type, float size, float r, float g, float b, float a, ArrayList<FloatBuffer> lista)
	{
		Iterator<FloatBuffer> it = lista.iterator();
		while(it.hasNext())
		{
			FloatBuffer buffer = it.next();
			dibujarBuffer(gl, type, size, r, g, b, a, buffer);
		}
	}
}
