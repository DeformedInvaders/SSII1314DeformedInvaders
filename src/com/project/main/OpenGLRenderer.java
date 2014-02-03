package com.project.main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public abstract class OpenGLRenderer implements Renderer
{	
	// Parámetros de la Cámara
	protected float xLeft, xRight, yTop, yBot, xCentro, yCentro;
	
	// Parámetros del Puerto de Vista
	protected int height;
	protected int width;
	
	//Parámetros de la Escena
	protected static final int SIZELINE = 3;
	protected static final int POINTWIDTH = 7;
	
	protected static final float EPSILON = 10.0f;
	
	// Contexto
	protected Context context;
	
	/* Constructoras */
	
	public OpenGLRenderer(Context context)
	{
		this.context = context;
		
		// Se inicializan los parámetros de la cámara en el 
		// método onSurfaceChanged llamado automáticamente
		// después de la constructora.
	}
	
	/* Métodos abstractos a implementar */
	
	public abstract void onTouchDown(float x, float y, float width, float height);
	public abstract void onTouchMove(float x, float y, float width, float height);
	public abstract void onTouchUp(float x, float y, float width, float height);
	public abstract void reiniciar();
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{		
		// Sombreado Suave
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		// Color de Fondo Blanco
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// Limpiar Buffer de Profundidad
		gl.glClearDepthf(1.0f);

		// Activar Test de Profundidad
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		
		// Activar Back-Face Culling
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		
		// Perspectiva Ortogonal
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		// Reiniciar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{		
		// Cambio de Puerto de Vista
		this.width = width;
		this.height = height;
		gl.glViewport(0, 0, width, height);
		
		// Perspectiva Ortogonal proporcional al Puerto de Vista
		this.xRight = width;
		this.xLeft = 0.0f;
		this.yTop = height;
		this.yBot = 0.0f;
		this.xCentro = (xRight + xLeft)/2.0f;
		this.yCentro = (yTop + yBot)/2.0f;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);

		// Reiniciar la Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{	
		// Persepectiva Ortogonal para métodos de modificación de la cámara
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		// Limpiar Buffer de Color y de Profundidad
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Activar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/* Métodos de Modificación de Cámara */
	
	public void zoom(float factor)
	{	
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBot)*factor;
		
		this.xRight = xCentro + newAncho/2.0f;
		this.xLeft = xCentro - newAncho/2.0f;
		this.yTop = yCentro + newAlto/2.0f;
		this.yBot = yCentro - newAlto/2.0f;
	}
	
	public void drag(float width, float height, float dx, float dy)
	{			
		this.xLeft += dx * width;
		this.xRight += dx *width;
		this.yBot += dy * height;
		this.yTop += dy * height;
		
		this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}
	
	public void restore()
	{
        this.xRight = width; 
        this.xLeft = 0.0f;
        this.yTop = height;
        this.yBot = 0.0f;
        
        this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}
	
	/* Métodos de Construcción de Buffer de Pintura */
	
	// Construcción de un buffer de pintura para puntos a partir de una lista de vertices
	// Uso para GL_POINTS o GL_LINE_LOOP
	protected FloatBuffer construirBufferListaPuntos(float[] vertices)
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
	protected FloatBuffer construirBufferListaPuntos(FloatArray vertices)
	{			
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);
		
		return construirBufferListaPuntos(arrayVertices);
	}
	
	// Construcción de un buffer de pintura para puntos a partir de una lista de indice de vertices
	protected FloatBuffer construirBufferListaIndicePuntos(ShortArray indices, FloatArray vertices)
	{
		float[] arrayVertices = new float[2*indices.size];
		
		int i = 0;
		while(i < indices.size)
		{
			int pos = indices.get(i);
			arrayVertices[2*pos] = vertices.get(2*pos);
			arrayVertices[2*pos+1] = vertices.get(2*pos+1);
			
			i++;
		}
		
		return construirBufferListaPuntos(arrayVertices);
	}
	
	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos. 
	// Uso para GL_LINES
	protected FloatBuffer construirBufferListaLineas(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = 2*(triangulos.size-1);
		float[] arrayVertices = new float[2*arrayLong];
		
		int j = 0;
		int i = 0;
		while(j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j+1);
			
			arrayVertices[i] = vertices.get(2*a);
			arrayVertices[i+1] = vertices.get(2*a+1);
			
			arrayVertices[i+2] = vertices.get(2*b);
			arrayVertices[i+3] = vertices.get(2*b+1);
			
			j = j+1;
			i = i+4;
		}	
		
		return construirBufferListaPuntos(arrayVertices);
	}
	
	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_LINES
	protected FloatBuffer construirBufferListaTriangulos(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = 2*triangulos.size;
		float[] arrayVertices = new float[2*arrayLong];
		
		int j = 0;
		int i = 0;
		while(j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j+1);
			short c = triangulos.get(j+2);
			
			arrayVertices[i] = vertices.get(2*a);
			arrayVertices[i+1] = vertices.get(2*a+1);
			
			arrayVertices[i+2] = vertices.get(2*b);
			arrayVertices[i+3] = vertices.get(2*b+1);
			
			arrayVertices[i+4] = vertices.get(2*b);
			arrayVertices[i+5] = vertices.get(2*b+1);
			
			arrayVertices[i+6] = vertices.get(2*c);
			arrayVertices[i+7] = vertices.get(2*c+1);		
			
			arrayVertices[i+8] = vertices.get(2*c);
			arrayVertices[i+9] = vertices.get(2*c+1);
			
			arrayVertices[i+10] = vertices.get(2*a);
			arrayVertices[i+11] = vertices.get(2*a+1);
			
			j = j+3;
			i = i+12;
		}	
		
		return construirBufferListaPuntos(arrayVertices);
	}
	
	// Construcción de un buffer de pintura para lineas a partir de una lista de triangulos.
	// Uso para GL_TRIANGLES
	protected FloatBuffer construirBufferListaTriangulosRellenos(ShortArray triangulos, FloatArray vertices)
	{
		int arrayLong = triangulos.size;
		float[] arrayVertices = new float[2*arrayLong];
		
		int j = 0;
		int i = 0;
		while(j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j+1);
			short c = triangulos.get(j+2);
			
			arrayVertices[i] = vertices.get(2*a);
			arrayVertices[i+1] = vertices.get(2*a+1);
			
			arrayVertices[i+2] = vertices.get(2*b);
			arrayVertices[i+3] = vertices.get(2*b+1);
			
			arrayVertices[i+4] = vertices.get(2*c);
			arrayVertices[i+5] = vertices.get(2*c+1);
			
			j = j+3;
			i = i+6;
		}	
		
		return construirBufferListaPuntos(arrayVertices);
	}
	
	// Construcción de Coordenadas de Textura a partir de una lista de puntos.
	protected FloatArray construirTextura(FloatArray puntos, float width, float height)
	{
		FloatArray textura = new FloatArray(puntos.size);
		
		int i = 0;
		while(i < puntos.size)
		{
			float x = puntos.get(i);
			float y = puntos.get(i+1);
			
			// Conversión a Pixeles
			float px = (x - xLeft)*width/(xRight-xLeft);
			float py = (y - yBot)*height/(yTop-yBot);
			
			// Conversión a Coordenadas de Textura
			float cx = px / width;
			float cy = (height - py)/ height;
			
			textura.add(cx);
			textura.add(cy);
			
			i = i+2;
		}
		return textura;
	}
	
	/* Metodos de Actualización de Buffers de Pintura */
	
	// Actualiza los valores de un buffer de pintura para puntos
	protected void actualizarBufferListaPuntos(FloatBuffer buffer, FloatArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		buffer.put(arrayVertices);
		buffer.position(0);
	}
	
	// Actualiza los valores de un buffer de pintura para triangulos
	protected void actualizarBufferListaTriangulosRellenos(FloatBuffer buffer, ShortArray triangulos, FloatArray vertices)
	{
		int j = 0;
		int i = 0;
		while(j < triangulos.size)
		{
			short a = triangulos.get(j);
			short b = triangulos.get(j+1);
			short c = triangulos.get(j+2);
			
			buffer.put(i, vertices.get(2*a));
			buffer.put(i+1, vertices.get(2*a+1));
			
			buffer.put(i+2, vertices.get(2*b));
			buffer.put(i+3, vertices.get(2*b+1));
			
			buffer.put(i+4, vertices.get(2*c));
			buffer.put(i+5, vertices.get(2*c+1));
			
			j = j+3;
			i = i+6;
		}
	}
	
	// Actualiza los valores de un buffer de pintura para indice puntos
	protected void actualizarBufferListaIndicePuntos(FloatBuffer buffer, ShortArray contorno, FloatArray vertices)
	{
		int j = 0;
		while(j < contorno.size)
		{
			short a = contorno.get(j);
			
			buffer.put(2*j, vertices.get(2*a));
			buffer.put(2*j+1, vertices.get(2*a+1));
			
			j++;
		}
	}

	/* Métodos de Pintura en la Tubería Gráfica */
	
	// Pintura de un Buffer de Puntos
	protected void dibujarBuffer(GL10 gl, int type, int size, int color, FloatBuffer bufferPuntos)
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
	
	// Pintura de una Lista de Handles
	protected void dibujarListaIndiceHandle(GL10 gl, int color, FloatBuffer handle, FloatArray posiciones)
	{
		gl.glPushMatrix();
		
			int i = 0;
			while(i < posiciones.size)
			{
				float pos = posiciones.get(i);
				
				if(pos != -1)
				{
					float x = posiciones.get(i+1);
					float y = posiciones.get(i+2);
					float z = 0.0f;
					
					gl.glPushMatrix();
						gl.glTranslatef(x, y, z);
						dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
					gl.glPopMatrix();
				}
				
				i = i+3;
			}
		
		gl.glPopMatrix();
	}
	
	// Pintura de una Lista de Handles
	protected void dibujarListaHandle(GL10 gl, int color, FloatBuffer handle, FloatArray posiciones)
	{
		gl.glPushMatrix();
		
			int i = 0;
			while(i < posiciones.size)
			{
				float x = posiciones.get(i);
				float y = posiciones.get(i+1);
				float z = 0.0f;
					
				gl.glPushMatrix();
					gl.glTranslatef(x, y, z);
					dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
				gl.glPopMatrix();
				
				i = i+2;
			}
		
		gl.glPopMatrix();
	}
	
	/* TESTING */
	protected void dibujarListaHandleMultitouch(GL10 gl, FloatBuffer handle, FloatArray posiciones)
	{
		gl.glPushMatrix();
		
		int i = 0;
		while(i < posiciones.size)
		{
			float estado = posiciones.get(i);
			
			// estado = 0 SUELTO
			// estado = 1 PULSADO
			if(estado == 1)
			{
				float x = posiciones.get(i+1);
				float y = posiciones.get(i+2);
				float z = 0.0f;
				
				int color = Color.BLACK;
				switch(i/3)
				{
					case 0:
						color = Color.BLUE;
					break;
					case 1:
						color = Color.YELLOW;
					break;
					case 2:
						color = Color.RED;
					break;
					case 3:
						color = Color.GREEN;
					break;
				}
				
				gl.glPushMatrix();
					gl.glTranslatef(x, y, z);
					dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
				gl.glPopMatrix();
			}
			
			i = i+3;
		}
	
		gl.glPopMatrix();
	}
	/* TESTING */
	
	// Cargado de Textura
	protected void cargarTextura(GL10 gl, Bitmap textura, int[] nombreTextura, int pos)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
			
			gl.glGenTextures(1, nombreTextura, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTextura[pos]);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	// Dibujar Textura para una Lista de Puntos asociada a una Lista de Coordenadas de Textura
	protected void dibujarTextura(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, int[] nombreTextura, int posTextura)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTextura[posTextura]);
			gl.glFrontFace(GL10.GL_CW);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufferCoordTextura);
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDrawArrays(GL10.GL_TRIANGLES, 0, bufferPuntos.capacity()/2);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	/* Métodos Genéricos */
	
	// Generar Color Aleatorio
	protected int generarColorAleatorio()
	{
		Random rand = new Random();
		
		int red = (int)(255*rand.nextFloat());
		int green = (int)(255*rand.nextFloat());
		int blue = (int)(255*rand.nextFloat());
		
		return Color.rgb(red, green, blue);
	}
}
