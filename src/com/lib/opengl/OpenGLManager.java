package com.lib.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLUtils;

import com.creation.data.Handle;
import com.creation.data.MapaBits;
import com.lib.buffer.HandleArray;
import com.main.model.GamePreferences;

public class OpenGLManager
{
	public static MapaBits capturaPantalla(GL10 gl, int leftX, int leftY, int width, int height)
	{
		int screenshotSize = width * height;
		ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
		bb.order(ByteOrder.nativeOrder());

		gl.glReadPixels(leftX, leftY, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);

		int pixelsBuffer[] = new int[screenshotSize];
		bb.asIntBuffer().get(pixelsBuffer);
		bb = null;

		for (int i = 0; i < screenshotSize; ++i)
		{
			pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
		}

		MapaBits textura = new MapaBits(pixelsBuffer, width, height);
		return textura;
	}
	
	public static void cargarTextura(GL10 gl, Bitmap textura, int[] nombreTexturas, int posTextura)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);

			gl.glGenTextures(1, nombreTexturas, posTextura);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTexturas[posTextura]);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);

		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	public static void dibujarTextura(GL10 gl, int type, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, int nombreTextura)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTextura);
			gl.glFrontFace(GL10.GL_CW);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufferCoordTextura);
	
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			
			gl.glDrawArrays(type, 0, bufferPuntos.capacity() / 2);
			
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	/* Pintura de Buffers */
	
	// Pintura de un Buffer de Puntos
	public static void dibujarBuffer(GL10 gl, int type, int size, int color, FloatBuffer bufferPuntos)
	{
		gl.glColor4f(Color.red(color) / 255.0f, Color.green(color) / 255.0f, Color.blue(color) / 255.0f, Color.alpha(color) / 255.0f);
		gl.glFrontFace(GL10.GL_CW);

		if (type == GL10.GL_POINTS)
		{
			gl.glPointSize(size);
		}
		else
		{
			gl.glLineWidth(size);
		}

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDrawArrays(type, 0, bufferPuntos.capacity() / 2);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
	
	public static void dibujarBuffer(GL10 gl, int color, FloatBuffer bufferPuntos)
	{
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, color, bufferPuntos);
	}

	// Pintura de una Lista de Handles
	public static void dibujarListaHandle(GL10 gl, Handle handle, Handle handleSeleccionado, HandleArray handles)
	{
		for (short i = 0; i < handles.getNumHandles(); i++)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(handles.getXCoordHandle(i), handles.getYCoordHandle(i), GamePreferences.DEEP_HANDLE);
				
				if (handles.isSelectedHandle(i))
				{
					dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, handleSeleccionado.getColor(), handleSeleccionado.getBufferRelleno());
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handleSeleccionado.getBufferContorno());
				}
				else
				{
					dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, handle.getColor(), handle.getBufferRelleno());
					dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handle.getBufferContorno());					
				}
			
			gl.glPopMatrix();
		}
	}
}
