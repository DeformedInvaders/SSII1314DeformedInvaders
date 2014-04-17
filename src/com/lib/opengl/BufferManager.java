package com.lib.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.creation.data.Handle;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.model.GamePreferences;

public class BufferManager
{
	/* Métodos de Construcción de Buffer de Pintura */

	// Construcción de un buffer de pintura para puntos a partir de una lista de vertices
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

	// Construcción de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	public static FloatBuffer construirBufferListaPuntos(FloatArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para puntos a partir de una lista de indice de vertices
	public static FloatBuffer construirBufferListaIndicePuntos(ShortArray indices, FloatArray vertices)
	{
		float[] arrayVertices = new float[2 * indices.size];

		int i = 0;
		while (i < indices.size)
		{
			int pos = indices.get(i);
			arrayVertices[2 * pos] = vertices.get(2 * pos);
			arrayVertices[2 * pos + 1] = vertices.get(2 * pos + 1);

			i++;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaLineas(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = 2 * (triangulos.size - 1);
		float[] arrayVertices = new float[2 * arrayLong];

		int j = 0;
		int i = 0;
		while (j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j + 1);

			arrayVertices[i] = vertices.get(2 * a);
			arrayVertices[i + 1] = vertices.get(2 * a + 1);

			arrayVertices[i + 2] = vertices.get(2 * b);
			arrayVertices[i + 3] = vertices.get(2 * b + 1);

			j = j + 1;
			i = i + 4;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaTriangulos(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = 2 * triangulos.size;
		float[] arrayVertices = new float[2 * arrayLong];

		int j = 0;
		int i = 0;
		while (j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j + 1);
			short c = triangulos.get(j + 2);

			arrayVertices[i] = vertices.get(2 * a);
			arrayVertices[i + 1] = vertices.get(2 * a + 1);

			arrayVertices[i + 2] = vertices.get(2 * b);
			arrayVertices[i + 3] = vertices.get(2 * b + 1);

			arrayVertices[i + 4] = vertices.get(2 * b);
			arrayVertices[i + 5] = vertices.get(2 * b + 1);

			arrayVertices[i + 6] = vertices.get(2 * c);
			arrayVertices[i + 7] = vertices.get(2 * c + 1);

			arrayVertices[i + 8] = vertices.get(2 * c);
			arrayVertices[i + 9] = vertices.get(2 * c + 1);

			arrayVertices[i + 10] = vertices.get(2 * a);
			arrayVertices[i + 11] = vertices.get(2 * a + 1);

			j = j + 3;
			i = i + 12;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_TRIANGLES
	public static FloatBuffer construirBufferListaTriangulosRellenos(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = triangulos.size;
		float[] arrayVertices = new float[2 * arrayLong];

		int j = 0;
		int i = 0;
		while (j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j + 1);
			short c = triangulos.get(j + 2);

			arrayVertices[i] = vertices.get(2 * a);
			arrayVertices[i + 1] = vertices.get(2 * a + 1);

			arrayVertices[i + 2] = vertices.get(2 * b);
			arrayVertices[i + 3] = vertices.get(2 * b + 1);

			arrayVertices[i + 4] = vertices.get(2 * c);
			arrayVertices[i + 5] = vertices.get(2 * c + 1);

			j = j + 3;
			i = i + 6;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	/* Metodos de Actualización de Buffers de Pintura */

	// Actualiza los valores de un buffer de pintura para puntos
	public static void actualizarBufferListaPuntos(FloatBuffer buffer, FloatArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		buffer.put(arrayVertices);
		buffer.position(0);
	}

	// Actualizar los valores de un buffer de pintura para triangulos.
	// Uso para GL_LINES
	public static void construirBufferListaTriangulos(FloatBuffer buffer, ShortArray triangulos, FloatArray vertices)
	{
		int j = 0;
		int i = 0;
		while (j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j + 1);
			short c = triangulos.get(j + 2);

			buffer.put(i, vertices.get(2 * a));
			buffer.put(i + 1, vertices.get(2 * a + 1));

			buffer.put(i + 2, vertices.get(2 * b));
			buffer.put(i + 3, vertices.get(2 * b + 1));

			buffer.put(i + 4, vertices.get(2 * b));
			buffer.put(i + 5, vertices.get(2 * b + 1));

			buffer.put(i + 6, vertices.get(2 * c));
			buffer.put(i + 7, vertices.get(2 * c + 1));

			buffer.put(i + 8, vertices.get(2 * c));
			buffer.put(i + 9, vertices.get(2 * c + 1));

			buffer.put(i + 10, vertices.get(2 * a));
			buffer.put(i + 11, vertices.get(2 * a + 1));

			j = j + 3;
			i = i + 12;
		}
	}

	// Actualiza los valores de un buffer de pintura para triangulos
	// Uso para GL_TRIANGLES
	public static void actualizarBufferListaTriangulosRellenos(FloatBuffer buffer, ShortArray triangulos, FloatArray vertices)
	{
		int j = 0;
		int i = 0;
		while (j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j + 1);
			short c = triangulos.get(j + 2);

			buffer.put(i, vertices.get(2 * a));
			buffer.put(i + 1, vertices.get(2 * a + 1));

			buffer.put(i + 2, vertices.get(2 * b));
			buffer.put(i + 3, vertices.get(2 * b + 1));

			buffer.put(i + 4, vertices.get(2 * c));
			buffer.put(i + 5, vertices.get(2 * c + 1));

			j = j + 3;
			i = i + 6;
		}
	}

	// Actualiza los valores de un buffer de pintura para indice puntos
	public static void actualizarBufferListaIndicePuntos(FloatBuffer buffer, ShortArray contorno, FloatArray vertices)
	{
		int j = 0;
		while (j < contorno.size)
		{
			short a = contorno.get(j);

			buffer.put(2 * j, vertices.get(2 * a));
			buffer.put(2 * j + 1, vertices.get(2 * a + 1));

			j++;
		}
	}
	
	/* Métodos de Transformación de Puntos */
	
	public static void trasladarVertices(float vx, float vy, FloatArray vertices)
	{
		int i = 0;
		while (i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i + 1);

			vertices.set(i, x + vx);
			vertices.set(i + 1, y + vy);

			i = i + 2;
		}
	}

	public static void escalarVertices(float fx, float fy, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		escalarVertices(fx, fy, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	public static void escalarVertices(float fx, float fy, FloatArray vertices)
	{
		int i = 0;
		while (i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i + 1);

			vertices.set(i, x * fx);
			vertices.set(i + 1, y * fy);

			i = i + 2;
		}
	}

	public static void rotarVertices(float ang, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		rotarVertices(ang, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	public static void rotarVertices(float ang, FloatArray vertices)
	{
		int i = 0;
		while (i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i + 1);

			vertices.set(i, (float) (x * Math.cos(ang) - y * Math.sin(ang)));
			vertices.set(i + 1, (float) (x * Math.sin(ang) + y * Math.cos(ang)));

			i = i + 2;
		}
	}
	
	/* Métodos de Pintura en la Tubería Gráfica */

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

	// Pintura de una Lista de Handles
	public static void dibujarListaHandle(GL10 gl, int color, Handle handle, FloatArray posiciones)
	{
		gl.glPushMatrix();

		int i = 0;
		while (i < posiciones.size)
		{
			float x = posiciones.get(i);
			float y = posiciones.get(i + 1);
			float z = 0.0f;

			gl.glPushMatrix();
			gl.glTranslatef(x, y, z);
			dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, color, handle.getBufferRelleno());
			
			gl.glTranslatef(0.0f, 0.0f, 1.0f);
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handle.getBufferContorno());
			gl.glPopMatrix();

			i = i + 2;
		}

		gl.glPopMatrix();
	}
}
