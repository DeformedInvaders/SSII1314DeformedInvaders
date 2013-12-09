package com.example.main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLUtils;

import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class GLESUtils
{
	public static FloatBuffer construirBuffer(float[] lista)
	{			
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lista.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(lista);
		buffer.position(0);
		
		return buffer;
	}
	
	public static FloatBuffer construirBuffer(FloatArray lista)
	{	
		/*int arrayLong = lista.size;
		
		for(int i = 0; i < lista.size; i++)
		{
			arrayPuntos[i] = lista.get(i);
		}*/
		
		float[] arrayPuntos = new float[lista.size];
		System.arraycopy(lista.items, 0, arrayPuntos, 0, lista.size);
		
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(lista.size * 4);
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
	
	public static FloatArray construirTextura(FloatArray puntos, float width, float height, float xRight, float xLeft, float yTop, float yBot)
	{
		FloatArray textura = new FloatArray(puntos.size);
		float dx= width/(xRight-xLeft);
		float dy = height/(yTop-yBot);
		int i = 0;
		while(i < puntos.size)
		{
			float x = puntos.get(i);
			float y = puntos.get(i+1);
			float px=(x-xLeft)*dx;
			float py=(y-yBot)+dy;
			float cx = px / width;
			float cy = py / height;
			
			textura.add(cx);
			textura.add(cy);
			
			i = i+2;
		}
		return textura;
	}

	public static void dibujarBuffer(GL10 gl, int type, float size, int color, FloatBuffer bufferPuntos)
	{	
		gl.glColor4f(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, 1.0f);
		gl.glFrontFace(GL10.GL_CW);
		
		if(type == GL10.GL_POINTS)
		{
			gl.glPointSize(size);
		}
		else {
			gl.glLineWidth(size);
		}
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDrawArrays(type, 0, bufferPuntos.capacity() / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public static void dibujarBuffer(GL10 gl, int type, float size, int color, ArrayList<FloatBuffer> bufferLista)
	{
		Iterator<FloatBuffer> it = bufferLista.iterator();
		while(it.hasNext())
		{
			FloatBuffer buffer = it.next();
			dibujarBuffer(gl, type, size, color, buffer);
		}
	}
	
	public static void cargarTextura(GL10 gl, Bitmap textura, int[] nombreTextura, int pos)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
			
			gl.glGenTextures(1, nombreTextura, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTextura[pos]);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public static void dibujarBuffer(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferTextura, int[] nombreTextura, int pos)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTextura[pos]);
			gl.glFrontFace(GL10.GL_CW);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufferTextura);
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferPuntos.capacity()/2);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public static void dibujarBuffer(GL10 gl, ArrayList<FloatBuffer> bufferPuntos, ArrayList<FloatBuffer> bufferTextura, int[] nombreTextura, int pos)
	{
		Iterator<FloatBuffer> itp = bufferPuntos.iterator();
		Iterator<FloatBuffer> itt = bufferTextura.iterator();
		while(itp.hasNext() && itt.hasNext())
		{
			dibujarBuffer(gl, itp.next(), itt.next(), nombreTextura, pos);
		}
	}
	
	public static int generarColor()
	{
		Random rand = new Random();
		
		int red = (int)(255*rand.nextFloat());
		int green = (int)(255*rand.nextFloat());
		int blue = (int)(255*rand.nextFloat());
		
		return Color.rgb(red, green, blue);
	}
}
