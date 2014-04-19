package com.lib.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

import com.creation.data.Handle;
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
	public static FloatBuffer construirBufferListaPuntos(VertexArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para puntos a partir de una lista de indice de vertices
	public static FloatBuffer construirBufferListaIndicePuntos(HullArray contorno, VertexArray vertices)
	{
		float[] arrayVertices = new float[2 * contorno.getNumVertex()];

		int j = 0;
		for (short i = 0; i < contorno.getNumVertex(); i++)
		{
			short a = contorno.getVertex(i);
			
			arrayVertices[j] = vertices.getXVertex(a);
			arrayVertices[j + 1] = vertices.getYVertex(a);

			j = j + 2;
		}

		return construirBufferListaPuntos(arrayVertices);
	}

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaLineas(LineArray lineas, VertexArray vertices)
	{
		float[] arrayVertices = new float[4 * lineas.getNumLines()];

		int j = 0;
		for (short i = 0; i < lineas.getNumLines(); i++)
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

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	public static FloatBuffer construirBufferListaTriangulos(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[12 * triangulos.getNumTriangles()];

		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
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

	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_TRIANGLES
	public static FloatBuffer construirBufferListaTriangulosRellenos(TriangleArray triangulos, VertexArray vertices)
	{
		float[] arrayVertices = new float[6 * triangulos.getNumTriangles()];

		int j = 0;
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
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

	/* Metodos de Actualización de Buffers de Pintura */

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
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
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
		for (short i = 0; i < triangulos.getNumTriangles(); i++)
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
		for (short i = 0; i < contorno.getNumVertex(); i++)
		{
			short a = contorno.getVertex(i);
			
			buffer.put(j, vertices.getXVertex(a));
			buffer.put(j + 1, vertices.getYVertex(a));
			
			j = j + 2;
		}
	}
	
	/* Métodos de Transformación de Puntos */
	
	public static void trasladarVertices(float vx, float vy, VertexArray vertices)
	{
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, x + vx, y + vy);
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
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, x * fx, y * fy);
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
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float x = vertices.getXVertex(i);
			float y = vertices.getYVertex(i);
			
			vertices.setVertex(i, (float) (x * Math.cos(ang) - y * Math.sin(ang)), (float) (x * Math.sin(ang) + y * Math.cos(ang)));
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
	
	//FIXME Eliminar al modificar pegatinas
	public static void dibujarListaHandle(GL10 gl, Handle handle, VertexArray vertices)
	{
		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(vertices.getXVertex(i), vertices.getYVertex(i), GamePreferences.DEEP_HANDLE);
				
				dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, GamePreferences.SIZE_LINE, handle.getColor(), handle.getBufferRelleno());
				dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE / 2, Color.WHITE, handle.getBufferContorno());					
			
			gl.glPopMatrix();
		}
	}
}
