package com.project.main;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;

import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.MapaBits;
import com.project.data.Pegatinas;

public abstract class OpenGLRenderer implements Renderer
{	
	// Parámetros de la Cámara
	private float xLeft, xRight, yTop, yBottom, xCentro, yCentro;
	
	// Copia Seguridad de la Cámara
	private boolean camaraGuardada;
	private float lastXLeft, lastXRight, lastYTop, lastYBot, lastXCentro, lastYCentro;
	
	// Parámetros del Puerto de Vista
	private int height, width;
	
	//Parámetros de la Escena
	protected static final int SIZELINE = 3;
	protected static final int POINTWIDTH = 7;
	
	protected static final float MAX_DISTANCE_PIXELS = 10;
	
	// Parámetros de Texturas
	private static final int NUM_TEXTURES = 5;
	private int[] nombreTexturas;
	
	private static final int POS_TEXTURE_BACKGROUND = 0;
	private static final int POS_TEXTURE_SKELETON = 1;
	private static final int POS_TEXTURE_STICKER = 2;
	
	private FloatBuffer coordTextura;
	private FloatBuffer[] vertTextura;
	
	protected int indiceTexturaFondo;
	protected boolean[] cargadaTextura;
	
	// Marco
	private float marcoA, marcoB, marcoC;
	private FloatBuffer recMarcoA, recMarcoB;
	
	// Contexto
	protected Context mContext;
	
	/* SECTION Constructoras */
	
	public OpenGLRenderer(Context context)
	{				
		mContext = context;
		
		// Marcos
		actualizarMarcos();
		
		// Textura
		indiceTexturaFondo = -1;
		
		nombreTexturas = new int[NUM_TEXTURES];
		cargadaTextura = new boolean[NUM_TEXTURES];
		vertTextura = new FloatBuffer[NUM_TEXTURES];
		
		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		coordTextura = construirBufferListaPuntos(texture);
		
		// Se inicializan los parámetros de la cámara en el 
		// método onSurfaceChanged llamado automáticamente
		// después de la constructora.
	}
	
	/* SECTION Métodos Abstractos */
	
	protected abstract void onTouchDown(float x, float y, float width, float height, int pos);
	protected abstract void onTouchMove(float x, float y, float width, float height, int pos);
	protected abstract void onTouchUp(float x, float y, float width, float height, int pos);
	protected abstract void onMultiTouchEvent();
	protected abstract void reiniciar();
	
	/* SECTION Métodos Renderer */
	
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
		
		// Activar Transparencia
		gl.glEnable(GL10.GL_BLEND); 
	    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		// Perspectiva Ortogonal
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);
		
		// Reiniciar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int screenWidth, int screenHeight)
	{		
		// Cambio de Puerto de Vista
		width = screenWidth;
		height = screenHeight;
		gl.glViewport(0, 0, width, height);
		
		// Perspectiva Ortogonal proporcional al Puerto de Vista
		xRight = width;
		xLeft = 0.0f;
		yTop = height;
		yBottom = 0.0f;
		xCentro = (xRight + xLeft)/2.0f;
		yCentro = (yTop + yBottom)/2.0f;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);
		
		// Copia de Seguridad de la Cámara
		camaraGuardada = false;
		
		// Marco
		actualizarMarcos();
		
		// Reiniciar la Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// Fondo 
		if(indiceTexturaFondo != -1)
		{
			cargarTexturaFondo(gl, indiceTexturaFondo, POS_TEXTURE_BACKGROUND);
			cargadaTextura[POS_TEXTURE_BACKGROUND] = true;
		}
		
		actualizarFondo();
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{	
		// Persepectiva Ortogonal para métodos de modificación de la cámara
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);
		
		// Limpiar Buffer de Color y de Profundidad
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		// Activar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
	
	/* SECTION Métodos de Modificación de Cámara */
	
	public void camaraZoom(float factor)
	{	
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBottom)*factor;
		
		xRight = xCentro + newAncho/2.0f;
		xLeft = xCentro - newAncho/2.0f;
		yTop = yCentro + newAlto/2.0f;
		yBottom = yCentro - newAlto/2.0f;
		
		actualizarMarcos();
		actualizarFondo();
	}
	
	public void camaradrag(float dWorldX, float dWorldY)
	{			
		xLeft += dWorldX;
		xRight += dWorldX;
		yBottom += dWorldY;
		yTop += dWorldY;
		
		xCentro = (xRight + xLeft)/2.0f;
        yCentro = (yTop + yBottom)/2.0f;
        
        actualizarMarcos();
        actualizarFondo();
	}
	
	public void camaradrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		float lastWorldX = convertToWorldXCoordinate(lastPixelX, screenWidth);
		float lastWorldY = convertToWorldYCoordinate(lastPixelY, screenHeight);
		
		float dWorldX = lastWorldX - worldX;
		float dWorldY = lastWorldY - worldY;
		
		camaradrag(dWorldX, dWorldY);
	}
	
	public void camaraRestore()
	{
        xRight = width; 
        xLeft = 0.0f;
        yTop = height;
        yBottom = 0.0f;
        
        xCentro = (xRight + xLeft)/2.0f;
        yCentro = (yTop + yBottom)/2.0f;
        
        actualizarMarcos();
        actualizarFondo();
	}
	
	/* SECTION Métodos de modificación de puntos */
	
	public void coordZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }
	
	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }
	
	protected void trasladarVertices(float vx, float vy, FloatArray vertices)
	{
		int i = 0;
		while(i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i+1);
			
			vertices.set(i, x + vx);
			vertices.set(i+1, y + vy);
			
			i = i+2;
		}
	}
	
	protected void escalarVertices(float fx, float fy, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		escalarVertices(fx, fy, vertices);
		trasladarVertices(cx, cy, vertices);
	}
	
	protected void escalarVertices(float fx, float fy, FloatArray vertices)
	{
		int i = 0;
		while(i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i+1);
			
			vertices.set(i, x * fx);
			vertices.set(i+1, y * fy);
			
			i = i+2;
		}
	}
	
	protected void rotarVertices(float ang, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		rotarVertices(ang, vertices);
		trasladarVertices(cx, cy, vertices);
	}
	
	protected void rotarVertices(float ang, FloatArray vertices)
	{
		int i = 0;
		while(i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i+1);
			
			vertices.set(i, (float) (x * Math.cos(ang) - y * Math.sin(ang)));
			vertices.set(i+1, (float) (x * Math.sin(ang) + y * Math.cos(ang)));
			
			i = i+2;
		}
	}
	
	/* SECTION Métodos de Copia de Seguridad de la Cámara */
	
	public void salvarCamara()
	{
		lastXLeft = xLeft;
		lastXRight = xRight;
		lastYTop = yTop;
		lastYBot = yBottom;
		lastXCentro = xCentro;
		lastYCentro = yCentro;
		
		camaraGuardada = true;
	}
	
	public void recuperarCamara()
	{
		if(camaraGuardada)
		{
			xLeft = lastXLeft;
			xRight = lastXRight;
			yTop = lastYTop;
			yBottom = lastYBot;
			xCentro = lastXCentro;
			yCentro = lastYCentro;
		}
	}
	
	/* SECTION Métodos de Captura de Pantalla y Marcos */
	
	/*	
		____________________________________
		|			|___________|			| B
		|			|			|			|
		|			|			|			| A
		|			|			|			|
		|			|___________|			|
		|___________|___________|___________| B
			recA		recB			C
	*/
	
	private void actualizarMarcos()
	{
		float height = yTop - yBottom;
		float width = xRight - xLeft;
		
		marcoB = 0.1f * height;
		marcoA = height - 2*marcoB;
		marcoC = (width - marcoA)/2;
		
		float[] recA = {0, 0, 0, height, marcoC, 0, marcoC, height};		
		recMarcoA = construirBufferListaPuntos(recA);
		
		float[] recB = {0, 0, 0, marcoB, marcoA, 0, marcoA, marcoB};
		recMarcoB = construirBufferListaPuntos(recB);
	}
	
	protected void dibujarMarco(GL10 gl)
	{
		gl.glPushMatrix();
		
			gl.glTranslatef(xLeft, yBottom, 1.0f);
			
			gl.glPushMatrix();
				
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoA);
				
				gl.glTranslatef(marcoC + marcoA, 0, 0);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoA);
			
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			
				gl.glTranslatef(marcoC, 0, 0);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoB);
				
				gl.glTranslatef(0, marcoB + marcoA, 0);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoB);
			
			gl.glPopMatrix();
			
		gl.glPopMatrix();
	}
	
	private MapaBits capturaPantalla(GL10 gl, int leftX, int leftY, int width, int height)
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
	
	// FIXME Deprecated
	protected MapaBits capturaPantalla(GL10 gl, int width, int height)
	{
		return capturaPantalla(gl, 0, 0, width, height);
	}
		
	protected MapaBits capturaPantallaPolariod(GL10 gl, int width, int height)
	{		
		return capturaPantalla(gl, (int) marcoC, (int) marcoB, (int) marcoA, (int) marcoA);
	}
	
	protected boolean isPoligonoDentroMarco(FloatArray vertices)
	{
		int i = 0;
		while(i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i+1);
			
			if(x <= marcoC || x >= marcoC + marcoA || y <= marcoB || y >= marcoB + marcoA)
			{
				return false;
			}
			
			i = i+2;
		}
		
		return true;
	}
	
	/* SECTION Métodos de Conversión de Coordenadas */
	
	protected float convertToWorldXCoordinate(float pixelX, float screenWidth)
	{
		return xLeft + (xRight-xLeft)*pixelX/screenWidth;
	}
	
	protected float convertToWorldYCoordinate(float pixelY, float screenHeight)
	{
		return yBottom + (yTop-yBottom)*(screenHeight-pixelY)/screenHeight;
	}
	
	protected float convertToPixelXCoordinate(float worldX, float screenWidth)
	{
		return (worldX - xLeft)*screenWidth/(xRight-xLeft);
	}
	
	protected float convertToPixelYCoordinate(float worldY, float screenHeight)
	{
		return screenHeight - (worldY - yBottom)*screenHeight/(yTop-yBottom);
	}
	
	protected boolean inPixelInCanvas(float worldX, float worldY)
	{
		return worldX >= xLeft && worldX <= xRight && worldY >= yBottom && worldY <= yTop;
	}
	
	/* SECTION Métodos de Búsqueda de Pixeles */
	
	protected short buscarPixel(FloatArray vertices, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		if(!inPixelInCanvas(worldX, worldY)) return -1;
		
		int minpos = -1;
		
		int j = 0;
		while(j < vertices.size)
		{
			float px = vertices.get(j);
			float py = vertices.get(j+1);	
			
			float lastpx = convertToPixelXCoordinate(px, screenWidth);
			float lastpy = convertToPixelYCoordinate(py, screenHeight);
						
			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastpx, lastpy));
			if(distancia < MAX_DISTANCE_PIXELS)
			{
				minpos = j/2;
				return (short)minpos;
			}
			
			j = j+2;
		}
		
		return (short)minpos;
	}	
	
	protected short buscarPixel(ShortArray contorno, FloatArray vertices, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
				
		if(!GeometryUtils.isPointInsideMesh(contorno, vertices, worldX, worldY)) return -1;
		
		float mindistancia = Float.MAX_VALUE;
		int minpos = -1;
		
		int j = 0;
		while(j < vertices.size)
		{
			float px = vertices.get(j);
			float py = vertices.get(j+1);	
			
			float lastpx = convertToPixelXCoordinate(px, screenWidth);
			float lastpy = convertToPixelYCoordinate(py, screenHeight);
						
			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastpx, lastpy));
			if(distancia < mindistancia)
			{
				minpos = j/2;
				mindistancia = distancia;
			}
			
			j = j+2;
		}
		
		return (short)minpos;
	}	
	
	/* SECTION Métodos de Construcción de Buffer de Pintura */
	
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
	
	/* SECTION Metodos de Actualización de Buffers de Pintura */
	
	// Actualiza los valores de un buffer de pintura para puntos
	protected void actualizarBufferListaPuntos(FloatBuffer buffer, FloatArray vertices)
	{
		float[] arrayVertices = new float[vertices.size];
		System.arraycopy(vertices.items, 0, arrayVertices, 0, vertices.size);

		buffer.put(arrayVertices);
		buffer.position(0);
	}
	
	// Actualizar los valores de un buffer de pintura para triangulos.
	// Uso para GL_LINES
	protected void construirBufferListaTriangulos(FloatBuffer buffer, ShortArray triangulos, FloatArray vertices)
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
			
			buffer.put(i+4, vertices.get(2*b));
			buffer.put(i+5, vertices.get(2*b+1));
			
			buffer.put(i+6, vertices.get(2*c));
			buffer.put(i+7, vertices.get(2*c+1));
			
			buffer.put(i+8, vertices.get(2*c));
			buffer.put(i+9, vertices.get(2*c+1));
			
			buffer.put(i+10, vertices.get(2*a));
			buffer.put(i+11, vertices.get(2*a+1));
			
			j = j+3;
			i = i+12;
		}
	}
	
	// Actualiza los valores de un buffer de pintura para triangulos
	// Uso para GL_TRIANGLES
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

	/* SECTION Métodos de Pintura en la Tubería Gráfica */
	
	// Pintura de un Buffer de Puntos
	protected void dibujarBuffer(GL10 gl, int type, int size, int color, FloatBuffer bufferPuntos)
	{	
		gl.glColor4f(Color.red(color)/255.0f, Color.green(color)/255.0f, Color.blue(color)/255.0f, Color.alpha(color)/255.0f);
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
				float estado = posiciones.get(i+1);
				
				if(estado == 1)
				{
					float x = posiciones.get(i+2);
					float y = posiciones.get(i+3);
					float z = 0.0f;
					
					gl.glPushMatrix();
						gl.glTranslatef(x, y, z);
						dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
					gl.glPopMatrix();
				}
				
				i = i+4;
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
	
	// FIXME TESTING
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
	
	/* SECTION Métodos de Pintura de Personajes */
	
	protected void dibujarPersonaje(GL10 gl, FloatBuffer triangulos, FloatBuffer contorno, FloatBuffer coordTriangulos, Pegatinas pegatinas, FloatArray vertices)
	{				
		// Textura
		dibujarTexturaEsqueleto(gl, triangulos, coordTriangulos);
			
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, contorno);
		
		// Pegatinas
		for(int i = 0; i < pegatinas.getNumPegatinas(); i++)
		{
			if(pegatinas.isCargada(i))
			{
				int indice = pegatinas.getVertice(i);
				dibujarTexturaPegatina(gl, vertices.get(2*indice), vertices.get(2*indice+1), i);
			}
		}
	}
	
	/* SECTION Métodos de Construcción de Texturas */
	
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
			float py = (y - yBottom)*height/(yTop-yBottom);
			
			// Conversión a Coordenadas de Textura
			float cx = px / width;
			float cy = (height - py)/ height;
			
			textura.add(cx);
			textura.add(cy);
			
			i = i+2;
		}
		return textura;
	}
	
	private void cargarTextura(GL10 gl, Bitmap textura, int posTextura)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
			
			gl.glGenTextures(1, nombreTexturas, posTextura);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTexturas[posTextura]);
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
	
	protected void cargarTexturaEsqueleto(GL10 gl, Bitmap textura)
	{
		cargarTextura(gl, textura, POS_TEXTURE_SKELETON);
	}
	
	protected void cargarTexturaPegatinas(GL10 gl, int indiceTextura, int pos)
	{     
		int posTextura = POS_TEXTURE_STICKER + pos;
		
		if(!cargadaTextura[posTextura])
		{
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);
			
			float height = bitmap.getHeight()/2;
			float width = bitmap.getWidth()/2;
			
			cargarTextura(gl, bitmap, posTextura);
			bitmap.recycle();
			
			FloatArray puntos = new FloatArray();
			puntos.add(-width);	puntos.add(-height);
			puntos.add(-width);	puntos.add(height);
			puntos.add(width);	puntos.add(-height);
			puntos.add(width);	puntos.add(height);	
			
			vertTextura[posTextura] = construirBufferListaPuntos(puntos);
			cargadaTextura[posTextura] = true;
		}
	}
	
	protected void descargarTexturaPegatinas(int pos)
	{
		int posTextura = POS_TEXTURE_STICKER + pos;
		
		cargadaTextura[posTextura] = false;
	}
	
	protected void cargarTexturaFondo(GL10 gl, int indiceTextura, int posTextura)
	{        
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);
		
		cargarTextura(gl, bitmap, posTextura);
		bitmap.recycle();
	}
	
	/* SECTION Métodos de Pintura de Texturas */
	
	private void dibujarTextura(GL10 gl, int type, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, int posTextura)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTexturas[posTextura]);
			gl.glFrontFace(GL10.GL_CW);
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferPuntos);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, bufferCoordTextura);
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				gl.glDrawArrays(type, 0, bufferPuntos.capacity()/2);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	protected void dibujarTexturaEsqueleto(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura)
	{
		dibujarTextura(gl, GL10.GL_TRIANGLES, bufferPuntos, bufferCoordTextura, POS_TEXTURE_SKELETON);
	}
	
	protected void dibujarTexturaPegatina(GL10 gl, float x, float y, int pos)
	{
		int posTextura = POS_TEXTURE_STICKER + pos;
		
		if(cargadaTextura[posTextura])
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(x, y, 0.0f);
				
				dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);
		
			gl.glPopMatrix();
		}
	}
	
	protected void dibujarTexturaFondo(GL10 gl)
	{
		if(cargadaTextura[POS_TEXTURE_BACKGROUND])
		{
			dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[POS_TEXTURE_BACKGROUND], coordTextura, POS_TEXTURE_BACKGROUND);
		}
	}
	
	private void actualizarFondo()
	{
		if(cargadaTextura[POS_TEXTURE_BACKGROUND])
		{			
			FloatArray puntos = new FloatArray();
			puntos.add(xLeft);	puntos.add(yBottom);
			puntos.add(xLeft);	puntos.add(yTop);
			puntos.add(xRight);	puntos.add(yBottom);
			puntos.add(xRight);	puntos.add(yTop);	
			
			vertTextura[POS_TEXTURE_BACKGROUND] = construirBufferListaPuntos(puntos);
		}
	}
	
	/* SECTION Métodos Genéricos */
	
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
