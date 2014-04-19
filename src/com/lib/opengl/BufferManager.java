package com.lib.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.creation.data.Handle;
import com.lib.utils.FloatArray;
import com.project.model.GamePreferences;

public class BufferManager
{
	/* M�todos de Construcci�n de Buffer de Pintura */

	// Construcci�n de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	public static FloatBuffer construirBufferListaPuntos(float[] vertices)
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuf.asFloatBuffer();
		buffer.put(vertices);
		buffer.position(0);

		return buffer;
	}

	// Construcci�n de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	public static FloatBuffer construirBufferListaPuntos(VertexArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcci�n de un buffer de pintura para puntos a partir de una lista de indice de vertices
	public static FloatBuffer construirBufferListaIndicePuntos(HullArray contorno, VertexArray vertices)
	{
		float[] arrayVertices = new float[2 * contorno.getNumVertex()];

		int j = 0;
		for (int i = 0; i < contorno.getNumVertex(); i++)
		{
			int a = contorno.getVertex(i);
			
			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			j = j + 2;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcci�n de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaLineas(LineArray lineas, VertexArray vertices)
	{
		float[] arrayVertices = new float[4 * lineas.getNumLines()];

		int j = 0;
		for (int i = 0; i < lineas.getNumLines(); i++)
		{
			short a = lineas.getAVertex(i);
			short b = lineas.getBVertex(i);
			
			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			arrayVertices[j + 2] = vertices.getXVertex(b);
			arrayVertices[j + 3] = vertices.getYVertex(b);
			
			j = j + 4;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcci�n de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaTriangulos(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[12 * triangulos.getNumTriangles()];

		int j = 0;
		for (int i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			arrayVertices[j + 2] = vertices.getXVertex(b);
			arrayVertices[j + 3] = vertices.getYVertex(b);

			arrayVertices[j + 4] = vertices.getXVertex(b);
			arrayVertices[j + 5] = vertices.getYVertex(b);

			arrayVertices[j + 6] = vertices.getXVertex(c);
			arrayVertices[j + 7] = vertices.getYVertex(c);

			arrayVertices[j + 8] = vertices.getXVertex(c);
			arrayVertices[j + 9] = vertices.getYVertex(c);

			arrayVertices[j + 10] = vertices.getXVertex(a);
			arrayVertices[j + 11] = vertices.getYVertex(a);
			
			j = j + 12;			
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcci�n de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_TRIANGLES
	public static FloatBuffer construirBufferListaTriangulosRellenos(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[6 * triangulos.getNumTriangles()];

		int j = 0;
		for (int i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			arrayVertices[j + 2] = vertices.getXVertex(b);
			arrayVertices[j + 3] = vertices.getYVertex(b);

			arrayVertices[j + 4] = vertices.getXVertex(c);
			arrayVertices[j + 5] = vertices.getYVertex(c);

			j = j + 6;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	/* Metodos de Actualizaci�n de Buffers de Pintura */

	// Actualiza los valores de un buffer de pintura para puntos
	public static void actualizarBufferListaPuntos(FloatBuffer buffer, VertexArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		buffer.put(arrayVertices);
		buffer.position(0);
	}

	// Actualizar los valores de un buffer de pintura para triangulos.
	// Uso para GL_LINES
	public static void actualizarBufferListaTriangulos(FloatBuffer buffer, TriangleArray triangulos, VertexArray vertices)
	{
		int j = 0;
		for (int i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));

			buffer.put(j + 2, vertices.getXVertex(b));
			buffer.put(j + 3, vertices.getYVertex(b));

			buffer.put(j + 4, vertices.getXVertex(b));
			buffer.put(j + 5, vertices.getYVertex(b));

			buffer.put(j + 6, vertices.getXVertex(c));
			buffer.put(j + 7, vertices.getYVertex(c));

			buffer.put(j + 8, vertices.getXVertex(c));
			buffer.put(j + 9, vertices.getYVertex(c));

			buffer.put(j + 10, vertices.getXVertex(a));
			buffer.put(j + 11, vertices.getYVertex(a));

			j = j + 12;
		}
	}

	// Actualiza los valores de un buffer de pintura para triangulos
	// Uso para GL_TRIANGLES
	public static void actualizarBufferListaTriangulosRellenos(FloatBuffer buffer, TriangleArray triangulos, VertexArray vertices)
	{
		int j = 0;
		for (int i = 0; i < triangulos.getNumTriangles(); i++)
		{
			short a = triangulos.getAVertex(i);
			short b = triangulos.getBVertex(i);
			short c = triangulos.getCVertex(i);

			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));

			buffer.put(j + 2, vertices.getXVertex(b));
			buffer.put(j + 3, vertices.getYVertex(b));

			buffer.put(j + 4, vertices.getXVertex(c));
			buffer.put(j + 5, vertices.getYVertex(c));

			j = j + 6;
		}
	}

	// Actualiza los valores de un buffer de pintura para indice puntos
	public static void actualizarBufferListaIndicePuntos(FloatBuffer buffer, HullArray contorno, VertexArray vertices)
	{
		int j = 0;
		for (int i = 0; i < contorno.getNumVertex(); i++)
		{
			short a = contorno.getVertex(i);
			
			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));
			
			j = j + 2;
		}
	}
	
	/* M�todos de Transformaci�n de Puntos */
	
	public static void trasladarVertices(float vx, float vy, VertexArray vertices)
	{
		for (int i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setXVertex(i, x + vx);
			vertices.setYVertex(i, y + vy);
		}
	}

	public static void escalarVertices(float fx, float fy, float cx, float cy, VertexArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		escalarVertices(fx, fy, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	public static void escalarVertices(float fx, float fy, VertexArray vertices)
	{
		for (int i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setXVertex(i, x * fx);
			vertices.setYVertex(i, y * fy);
		}
	}

	public static void rotarVertices(float ang, float cx, float cy, VertexArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		rotarVertices(ang, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	public static void rotarVertices(float ang, VertexArray vertices)
	{
		for (int i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setXVertex(i, (float) (x * Math.cos(ang) - y * Math.sin(ang)));
			vertices.setYVertex(i, (float) (x * Math.sin(ang) + y * Math.cos(ang)));
		}
	}
	
	/* M�todos de Pintura en la Tuber�a Gr�fica */

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
		BufferManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, color, bufferPuntos);
	}

	// FIXME revisar
	// Pintura de una Lista de Handles
	public static void dibujarListaIndiceHandle(GL10 gl, int color, Handle handle, FloatArray posiciones)
	{
		gl.glPushMatrix();

		int i = 0;
		while (i < posiciones.size)
		{
			float estado = posiciones.get(i + 1);

			if (estado == 1)
			{
				float x = posiciones.get(i + 2);
				float y = posiciones.get(i + 3);
				float z = 0.0f;

				gl.glPushMatrix();
				gl.glTranslatef(x, y, z);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, color, handle.getBufferRelleno());
				
				gl.glTranslatef(0.0f, 0.0f, 1.0f);
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handle.getBufferContorno());
				
				gl.glPopMatrix();
			}

			i = i + 4;
		}

		gl.glPopMatrix();
	}

	// FIXME revisar
	// Pintura de una Lista de Handles
	public static void dibujarListaHandle(GL10 gl, int color, Handle handle, FloatArray posiciones)
	{
		gl.glPushMatrix();

		int i = 0;
		while (i < posiciones.size)
		{
			float x = posiciones.get(i);
			float y = posiciones.get(i + 1);

			gl.glPushMatrix();
			gl.glTranslatef(x, y, 0.0f);
			dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, color, handle.getBufferRelleno());
			
			gl.glTranslatef(0.0f, 0.0f, 1.0f);
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handle.getBufferContorno());
			gl.glPopMatrix();

			i = i + 2;
		}

		gl.glPopMatrix();
	}
}
