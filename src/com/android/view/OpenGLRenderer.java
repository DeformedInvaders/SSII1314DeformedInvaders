package com.android.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
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

import com.creation.data.MapaBits;
import com.game.data.TTipoEntidad;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public abstract class OpenGLRenderer implements Renderer
{	
	// Par�metros de la C�mara
	private float xLeft, xRight, yTop, yBottom, xCentro, yCentro;

	// Copia Seguridad de la C�mara
	private boolean camaraGuardada;
	private float lastXLeft, lastXRight, lastYTop, lastYBot, lastXCentro, lastYCentro;

	// Par�metros del Puerto de Vista
	private int screenHeight, screenWidth;

	//Par�metros de la Escena
	protected static final int SIZELINE = 3;
	protected static final int POINTWIDTH = 7;

	protected static final float MAX_DISTANCE_PIXELS = 10;
	
	protected static final float DIST_PIXELS = 20;
	protected static final float DIST_PIXELS_EXTRA = 14;

	// Par�metros de Texturas	
	private static final int MAX_TEXTURE_BACKGROUND = 3;
	private static final int MAX_TEXTURE_STICKER = 4;
	private static final int MAX_TEXTURE_OBSTACLE = 1;
	private static final int MAX_TEXTURE_ENEMY = 4;

	private static final int POS_TEXTURE_BACKGROUND = 0;
	private static final int POS_TEXTURE_FISSURE = POS_TEXTURE_BACKGROUND + MAX_TEXTURE_BACKGROUND;
	private static final int POS_TEXTURE_OBSTACLE = POS_TEXTURE_FISSURE + 1;
	private static final int POS_TEXTURE_CHARACTER_SKELETON = POS_TEXTURE_OBSTACLE + MAX_TEXTURE_OBSTACLE;
	private static final int POS_TEXTURE_CHARACTER_STICKER = POS_TEXTURE_CHARACTER_SKELETON + 1;
	private static final int POS_TEXTURE_ENEMY_SKELETON = POS_TEXTURE_CHARACTER_STICKER + MAX_TEXTURE_STICKER;
	
	private static final int NUM_TEXTURES = POS_TEXTURE_ENEMY_SKELETON + MAX_TEXTURE_ENEMY * MAX_TEXTURE_STICKER + 1;
	private int[] nombreTexturas;

	private FloatBuffer coordTextura;
	private FloatBuffer[] vertTextura;
	
	private boolean[] cargadaTextura;
	
	// Fondo
	private static final int NUM_REPETICIONES = 3;
	
	private int[] indiceTexturaFondo;
	private float[] posFondo;
	private boolean[] dibujarFondo;
	
	protected boolean fondoFinalFijado;

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

		// Fondo
		indiceTexturaFondo = new int[MAX_TEXTURE_BACKGROUND];
		dibujarFondo = new boolean[MAX_TEXTURE_BACKGROUND];
		posFondo = new float[MAX_TEXTURE_BACKGROUND];
		
		fondoFinalFijado = false;
		
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND; i++)
		{
			indiceTexturaFondo[i] = -1;
		}
		
		// Textura
		nombreTexturas = new int[NUM_TEXTURES];
		cargadaTextura = new boolean[NUM_TEXTURES];
		vertTextura = new FloatBuffer[NUM_TEXTURES];

		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		coordTextura = BufferManager.construirBufferListaPuntos(texture);

		// Se inicializan los par�metros de la c�mara en el 
		// m�todo onSurfaceChanged llamado autom�ticamente
		// despu�s de la constructora.
	}

	/* SECTION M�todos Abstractos */

	protected abstract boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);
	protected abstract boolean onMultiTouchEvent();
	protected abstract boolean reiniciar();

	/* SECTION M�todos Renderer */

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
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{		
		// Cambio de Puerto de Vista
		screenWidth = width;
		screenHeight = height;
		gl.glViewport(0, 0, screenWidth, screenHeight);

		// Perspectiva Ortogonal proporcional al Puerto de Vista
		xRight = screenWidth;
		xLeft = 0.0f;
		yTop = screenHeight;
		yBottom = 0.0f;
		xCentro = (xRight + xLeft)/2.0f;
		yCentro = (yTop + yBottom)/2.0f;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);

		// Copia de Seguridad de la C�mara
		camaraGuardada = false;

		// Marco
		actualizarMarcos();

		// Reiniciar la Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Fondo 
		cargarTexturaFondo(gl, NUM_REPETICIONES);

		actualizarTexturaFondo();
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{	
		// Persepectiva Ortogonal para m�todos de modificaci�n de la c�mara
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);

		// Limpiar Buffer de Color y de Profundidad
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Activar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Background
		dibujarFondo(gl);	
	}
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
	public float getScreenWidth()
	{
		return screenWidth;
	}
	
	public float getScreenHeight()
	{
		return screenHeight;
	}

	/* SECTION M�todos de Modificaci�n de C�mara */

	public void camaraZoom(float factor)
	{	
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBottom)*factor;

		xRight = xCentro + newAncho/2.0f;
		xLeft = xCentro - newAncho/2.0f;
		yTop = yCentro + newAlto/2.0f;
		yBottom = yCentro - newAlto/2.0f;

		actualizarMarcos();
		actualizarTexturaFondo();
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
		actualizarTexturaFondo();
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
		xRight = screenWidth; 
		xLeft = 0.0f;
		yTop = screenHeight;
		yBottom = 0.0f;

		xCentro = (xRight + xLeft)/2.0f;
		yCentro = (yTop + yBottom)/2.0f;

		actualizarMarcos();
		actualizarTexturaFondo();
	}

	/* SECTION M�todos de modificaci�n de puntos */

	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight) { }

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

	/* SECTION M�todos de Copia de Seguridad de la C�mara */

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

	/* SECTION M�todos de Captura de Pantalla y Marcos 	
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
		float camaraHeight = yTop - yBottom;
		float camaraWidth = xRight - xLeft;

		marcoB = 0.1f * camaraHeight;
		marcoA = camaraHeight - 2*marcoB;
		marcoC = (camaraWidth - marcoA)/2;

		float[] recA = {0, 0, 0, camaraHeight, marcoC, 0, marcoC, camaraHeight};		
		recMarcoA = BufferManager.construirBufferListaPuntos(recA);

		float[] recB = {0, 0, 0, marcoB, marcoA, 0, marcoA, marcoB};
		recMarcoB = BufferManager.construirBufferListaPuntos(recB);
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

	protected boolean recortarPoligonoDentroMarco(FloatArray vertices)
	{
		int i = 0;
		while(i < vertices.size)
		{
			float frameX = convertToFrameXCoordinate(vertices.get(i));
			float frameY = convertToFrameYCoordinate(vertices.get(i+1));

			vertices.set(i, frameX);
			vertices.set(i+1, frameY);

			i = i+2;
		}

		return true;
	}

	protected void centrarPersonajeEnMarcoInicio(GL10 gl)
	{
		gl.glTranslatef(marcoC, marcoB, 0.0f);
	}

	protected void centrarPersonajeEnMarcoFinal(GL10 gl)
	{
		gl.glTranslatef(-marcoC, -marcoB, 0.0f);
	}

	protected void dibujarMarcoLateral(GL10 gl)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, 1.0f);
	
			gl.glPushMatrix();
	
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoA);
		
				gl.glTranslatef(marcoC + marcoA, 0, 0);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoA);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	protected void dibujarMarcoCentral(GL10 gl)
	{
		dibujarMarcoSuperior(gl);
		dibujarMarcoInferior(gl);
	}

	protected void dibujarMarcoSuperior(GL10 gl)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, 1.0f);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(marcoC, marcoB + marcoA, 0);
				dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, Color.argb(175, 0, 0, 0), recMarcoB);
	
			gl.glPopMatrix();

		gl.glPopMatrix();		
	}

	protected void dibujarMarcoInferior(GL10 gl)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, 1.0f);
	
				gl.glPushMatrix();
		
				gl.glTranslatef(marcoC, 0, 0);
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

	protected MapaBits capturaPantalla(GL10 gl)
	{		
		return capturaPantalla(gl, (int) marcoC, (int) marcoB, (int) marcoA, (int) marcoA);
	}

	/* SECTION M�todos de Conversi�n de Coordenadas */

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

	protected float convertToFrameXCoordinate(float worldX)
	{
		return worldX - marcoC;
	}

	protected float convertToFrameYCoordinate(float worldY)
	{
		return worldY - marcoB;
	}

	protected float convertFromFrameXCoordinate(float frameX)
	{
		return frameX + marcoC;
	}

	protected float convertFromFrameYCoordinate(float frameY)
	{
		return frameY + marcoB;
	}

	/* SECTION M�todos de B�squeda de Pixeles */

	protected short buscarPixel(FloatArray vertices, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		int minpos = -1;
		int j = 0;
		while(j < vertices.size)
		{
			float framepX = vertices.get(j);
			float framepY = vertices.get(j+1);

			float worldpX = convertFromFrameXCoordinate(framepX);
			float worldpY = convertFromFrameYCoordinate(framepY);

			float lastpX = convertToPixelXCoordinate(worldpX, screenWidth);
			float lastpY = convertToPixelYCoordinate(worldpY, screenHeight);

			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastpX, lastpY));
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

		float frameX = convertToFrameXCoordinate(worldX);
		float frameY = convertToFrameYCoordinate(worldY);

		if(!GeometryUtils.isPointInsideMesh(contorno, vertices, frameX, frameY)) return -1;

		float mindistancia = Float.MAX_VALUE;
		int minpos = -1;

		int j = 0;
		while(j < vertices.size)
		{
			float framepX = vertices.get(j);
			float framepY = vertices.get(j+1);

			float worldpX = convertFromFrameXCoordinate(framepX);
			float worldpY = convertFromFrameYCoordinate(framepY);

			float lastpX = convertToPixelXCoordinate(worldpX, screenWidth);
			float lastpY = convertToPixelYCoordinate(worldpY, screenHeight);

			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastpX, lastpY));
			if(distancia < mindistancia)
			{
				minpos = j/2;
				mindistancia = distancia;
			}

			j = j+2;
		}

		return (short)minpos;
	}	

	/* SECTION M�todos de Pintura en la Tuber�a Gr�fica */

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

	public void dibujarBuffer(GL10 gl, int color, FloatBuffer bufferPuntos)
	{	
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, color, bufferPuntos);
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

	/* SECTION M�todos de Construcci�n de Texturas */
	
	// M�todos de Gesti�n de posici�n de Texturas

	private int obtenerPosicionTexturaMalla(TTipoEntidad tipoEntidad)
	{
		switch(tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_SKELETON;
			case Enemigo:
				return POS_TEXTURE_ENEMY_SKELETON;
			default:
				return -1;
		}
	}

	private int obtenerPosicionTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, int posPegatina)
	{
		switch(tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_STICKER + posPegatina;
			case Enemigo:
				return POS_TEXTURE_ENEMY_SKELETON + posEntidad;
				//return POS_TEXTURE_ENEMY_SKELETON * (posEntidad + 1) + posPegatina;
			case Obstaculo:
				return POS_TEXTURE_OBSTACLE + posEntidad;
			case Grieta:
				return POS_TEXTURE_FISSURE;
			default:
				return -1;
		}
	}
	
	// M�todos de Contrucci�n de Textura

	protected FloatArray construirTextura(FloatArray puntos, float textureWidth, float textureHeight)
	{
		FloatArray textura = new FloatArray(puntos.size);

		int i = 0;
		while(i < puntos.size)
		{
			float frameX = puntos.get(i);
			float frameY = puntos.get(i+1);

			// Conversi�n a Coordenadas de Textura
			float coordX = frameX / textureWidth;
			float coordY = (textureHeight - frameY) / textureHeight;

			textura.add(coordX);
			textura.add(coordY);

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

		cargadaTextura[posTextura] = true;
	}
	
	private void cargarTextura(GL10 gl, int indiceTextura, int posTextura)
	{
		Bitmap textura = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);

			gl.glGenTextures(1, nombreTexturas, posTextura);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, nombreTexturas[posTextura]);
	
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textura, 0);

		gl.glDisable(GL10.GL_TEXTURE_2D);

		cargadaTextura[posTextura] = true;
		
		textura.recycle();
	}

	private void descargarTextura(int posTextura)
	{
		cargadaTextura[posTextura] = false;
	}

	// M�todos de Contrucci�n de Textura para Entidades
	
	public void cargarTexturaMalla(GL10 gl, Bitmap textura, TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if(posTextura != -1 && !cargadaTextura[posTextura])
		{
			cargarTextura(gl, textura, posTextura);
		}
	}
	
	public void descargarTexturaMalla(TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if(posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}
	
	public float cargarTexturaRectangulo(GL10 gl, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, int posPegatina)
	{     
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if(posTextura != -1 && !cargadaTextura[posTextura])
		{
			Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);

			float textureHeight = bitmap.getHeight();
			float textureWidth = bitmap.getWidth();

			cargarTextura(gl, bitmap, posTextura);
			bitmap.recycle();

			FloatArray puntos = new FloatArray();
			puntos.add(0.0f);	puntos.add(0.0f);
			puntos.add(0.0f);	puntos.add(textureHeight);
			puntos.add(textureWidth);	puntos.add(0.0f);
			puntos.add(textureWidth);	puntos.add(textureHeight);	

			vertTextura[posTextura] = BufferManager.construirBufferListaPuntos(puntos);
			
			return textureHeight;
		}
		
		return 0;
	}
	
	public void descargarTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, int posPegatina)
	{     
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if(posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}
	
	// M�todos de Contrucci�n de Textura para Fase Creaci�n

	protected void cargarTexturaMalla(GL10 gl, Bitmap textura)
	{
		cargarTexturaMalla(gl, textura, TTipoEntidad.Personaje);
	}

	protected void descargarTexturaMalla()
	{
		descargarTexturaMalla(TTipoEntidad.Personaje);
	}

	protected void cargarTexturaRectangulo(GL10 gl, int indiceTextura, int posPegatina)
	{     
		cargarTexturaRectangulo(gl, indiceTextura, TTipoEntidad.Personaje, 0, posPegatina);
	}
	
	protected void descargarTexturaRectangulo(int posPegatina)
	{     
		descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, posPegatina);
	}

	// M�todos de Pintura de Texturas

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
	
	// M�todos de Pintura de Texturas para Entidades

	public void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if(posTextura != -1 && cargadaTextura[posTextura])
		{
			dibujarTextura(gl, GL10.GL_TRIANGLES, bufferPuntos, bufferCoordTextura, posTextura);
		}
	}

	public void dibujarTexturaRectangulo(GL10 gl, float x, float y, TTipoEntidad tipoEntidad, int posEntidad, int posPegatina, float scaleX, float scaleY)
	{     
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if(posTextura != -1 && cargadaTextura[posTextura])
		{
			gl.glPushMatrix();

			gl.glTranslatef(x, y, 0.0f);
			
			gl.glScalef(scaleX, scaleY, 0.0f);

			dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);

			gl.glPopMatrix();
		}
	}
	
	public void dibujarTexturaRectangulo(GL10 gl, TTipoEntidad tipoEntidad, int posEntidad, int posPegatina, float scaleX, float scaleY)
	{     
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if(posTextura != -1 && cargadaTextura[posTextura])
		{
			gl.glPushMatrix();
			
			gl.glScalef(scaleX, scaleY, 0.0f);

			dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);

			gl.glPopMatrix();
		}
	}
	
	public void dibujarTexturaRectangulo(GL10 gl, float x, float y, TTipoEntidad tipoEntidad, int posEntidad, int posPegatina)
	{     
		dibujarTexturaRectangulo(gl, x, y, tipoEntidad, posEntidad, posPegatina, 1.0f, 1.0f);
	}
	
	// M�todos de Pintura de Textura para Fase Creaci�n
	
	protected void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura)
	{
		dibujarTexturaMalla(gl, bufferPuntos, bufferCoordTextura, TTipoEntidad.Personaje);
	}

	protected void dibujarTexturaRectangulo(GL10 gl, float x, float y, int posPegatina)
	{
		dibujarTexturaRectangulo(gl, x, y, TTipoEntidad.Personaje, 0, posPegatina);
	}

	/* SECTION M�todos de Pintura de Fondo */

	protected void seleccionarTexturaFondo(int indiceTextura)
	{
		indiceTexturaFondo[0] = indiceTextura;	
		
		dibujarFondo[0] = true;
	}
	
	protected void seleccionarTexturaFondo(int indiceTextura1, int indiceTextura2, int indiceTextura3)
	{
		indiceTexturaFondo[0] = indiceTextura1;
		indiceTexturaFondo[1] = indiceTextura2;
		indiceTexturaFondo[2] = indiceTextura3;		
		
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND - 1; i++)
		{
			dibujarFondo[i] = true;
		}
	}
	
	private void cargarTexturaFondo(GL10 gl, int numRepeticiones)
	{ 
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND; i++)
		{
			if(indiceTexturaFondo[i] != -1)
			{
				cargarTextura(gl, indiceTexturaFondo[i], POS_TEXTURE_BACKGROUND + i);
			}
		}
		
		posFondo[0] = 0;
		posFondo[1] = screenWidth;
		posFondo[2] = numRepeticiones * screenWidth;
	}
	
	private void dibujarTexturaFondo(GL10 gl, boolean dibujarFondo, float posFondo, int posTextura)
	{
		if(dibujarFondo)
		{
			gl.glPushMatrix();
			
				gl.glTranslatef(posFondo, 0.0f, 0.0f);
			
				dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);
		
			gl.glPopMatrix();
		}
	}

	private void dibujarFondo(GL10 gl)
	{
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND; i++)
		{
			if(cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				dibujarTexturaFondo(gl, dibujarFondo[i], posFondo[i], POS_TEXTURE_BACKGROUND + i);
			}
		}
	}

	private void actualizarTexturaFondo()
	{		
		FloatArray puntos = new FloatArray();
		puntos.add(xLeft);	puntos.add(yBottom);
		puntos.add(xLeft);	puntos.add(yTop);
		puntos.add(xRight);	puntos.add(yBottom);
		puntos.add(xRight);	puntos.add(yTop);
		
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND; i++)
		{
			if(cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				vertTextura[POS_TEXTURE_BACKGROUND + i] = BufferManager.construirBufferListaPuntos(puntos);
			}
		}
	}

	protected void desplazarFondo()
	{
		float despX = 0.005f * screenWidth;
		
		int lastFondo = POS_TEXTURE_BACKGROUND + MAX_TEXTURE_BACKGROUND - 1;
		
		// Activado de �ltimo Fondo
		if(posFondo[lastFondo] <= screenWidth)
		{
			dibujarFondo[lastFondo] = true;
			
			if(posFondo[lastFondo] <= 0.0f)
			{
				fondoFinalFijado = true;
				
				for(int i = 0; i < MAX_TEXTURE_BACKGROUND - 1; i++)
				{
					dibujarFondo[i] = false;
				}
			}
		}
		
		// Desplazamiento
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND; i++)
		{
			posFondo[i] -= despX;
		}
		
		// Reinicio de Fondo
		for(int i = 0; i < MAX_TEXTURE_BACKGROUND - 1; i++)
		{
			if(posFondo[i] <= -screenWidth)
			{
				posFondo[i] = screenWidth;
			}
		}
	}
	
	/* SECTION M�todos Gen�ricos */

	// Generar Color Aleatorio
	protected int generarColorAleatorio()
	{
		Random rand = new Random();

		int red = (int)(255*rand.nextFloat());
		int green = (int)(255*rand.nextFloat());
		int blue = (int)(255*rand.nextFloat());

		return Color.rgb(red, green, blue);
	}
	
	protected FloatArray obtenerPuntosInterseccion(int tipo, int size, int color, float minX, float minY, float maxX, float maxY, float distanciaX, float distanciaY, FloatArray vertices)

	{
		FloatArray verticesInterseccion = new FloatArray();

		List lineasHorizontales = new ArrayList();
		List lineasVerticales = new ArrayList();
		
		float ladoX = maxX - minX;
		float ladoY = maxY - minY;
		
		lineasHorizontales = obtenerLineasHorizontales(tipo, size, color, minX, minY, maxX, maxY, distanciaY, ladoX);
		lineasVerticales = obtenerLineasVerticales(tipo, size, color, minX, minY, maxX, maxY, distanciaX, ladoY);
		
		verticesInterseccion = obtenerVerticesInterseccion(tipo, size, color, lineasHorizontales, lineasVerticales, vertices);
		
		
		return verticesInterseccion;
	}
	
	
	private List obtenerLineasHorizontales(int tipo, int size, int color, float minX, float minY, float maxX, float maxY, float distancia, float ladoX) {
		
			List lineasAux = new ArrayList();
			Punto p1 = null;
			Punto p2 = null;
			Recta recta = null;
					
			float  auxMinY = minY;
			while((ladoX > distancia) && (auxMinY < maxY - distancia)){
				auxMinY = auxMinY + distancia;
	
				p1 = new Punto(minX, auxMinY);
				p2 = new Punto(maxX, auxMinY);
			
				recta = construccionRecta(p1,p2);
				lineasAux.add(recta);	
			}
			return lineasAux;

	}
	
	private List obtenerLineasVerticales(int tipo, int size, int color, float minX, float minY, float maxX, float maxY, float distancia, float ladoY) {
		
			List lineasAux = new ArrayList();
			Punto p1 = null;
			Punto p2 = null;
			Recta recta = null;
						
			float auxMinX = minX;
			while((ladoY > distancia) && (auxMinX < maxX - distancia)){
				auxMinX = auxMinX + distancia;
				
				p1 = new Punto(auxMinX, minY);
				p2 = new Punto(auxMinX, maxY);

				recta = construccionRecta(p1,p2);
				lineasAux.add(recta);
			}
			return lineasAux;
		
	}
	
	private FloatArray obtenerVerticesInterseccion(int tipo, int size, int color, List lineasHorizontales, List lineasVerticales, FloatArray vertices){
		
			FloatArray verticesInterseccion = new FloatArray();
			ArrayList verticesInterseccionMasDistancia = new ArrayList();
			Punto p = new Punto();
			for(int i=0; i < lineasHorizontales.size(); i++){
				for(int j=0; j < lineasVerticales.size(); j++){
					p = interseccionRectas((Recta) lineasHorizontales.get(i), (Recta) lineasVerticales.get(j));
					if (p!=null){ //si NO es null entonces se cortan
						if(puntoEnPoligono(vertices, vertices.size, p.getX(), p.getY())){
							if(tieneDistanciaConOtrosVertices(p, vertices, DIST_PIXELS)){
								//a�adimos los vertices a la lista
								verticesInterseccion.add(p.getX());
								verticesInterseccion.add(p.getY());
							}
							else{
								//a�adimos los vertices que no cumplen la distancia minima en una lista auxiliar por si fuera necesario meterlos
								verticesInterseccionMasDistancia.add(p);
							}
							
						}
						
					}	
				}		
			}
			
			int cont = 0;
			ArrayList<Boolean> usados = new ArrayList<Boolean>();
			for(int k=0; k<verticesInterseccionMasDistancia.size(); k++){
				usados.add(k, false);
			}

			while ((verticesInterseccion.size < 20) && (cont < verticesInterseccionMasDistancia.size())){
				int numeroAleatorio = 0 + (int)(Math.random()*usados.size()); 
				if(!usados.get(numeroAleatorio)){
					cont++;
					usados.set(numeroAleatorio, true);
					Punto punto = new Punto(((Punto) verticesInterseccionMasDistancia.get(numeroAleatorio)).getX(), ((Punto) verticesInterseccionMasDistancia.get(numeroAleatorio)).getY());
					if(tieneDistanciaConOtrosVertices((Punto) verticesInterseccionMasDistancia.get(numeroAleatorio), vertices, DIST_PIXELS_EXTRA)){
						verticesInterseccion.add(punto.getX());
						verticesInterseccion.add(punto.getY());
					}
				}
				
			}
			
			return verticesInterseccion;
		
	}
	
	
	private Recta construccionRecta(Punto p1, Punto p2) {
		Recta recta = null;
		if(p1.getX() == p2.getX()){
			recta = new Recta(1, 0, - p1.getX());
		}
		else{
			float aux = ((p2.getY() - p1.getY()) / (p2.getX() - p1.getX()));
			recta = new Recta(-aux, 1, (-p1.getY() + (aux * p1.getX())));
		}
		return recta;
	}

	private Punto interseccionRectas(Recta recta1, Recta recta2) {
		Punto punto;
		float xObtenido, yObtenido;
		float factorXRecta1 = recta1.getFactorX();
		float factorYRecta1 = recta1.getFactorY();
		float coeficienteRecta1 = recta1.getCoeficiente();
		float factorXRecta2 = recta2.getFactorX();
		float factorYRecta2 = recta2.getFactorY();
		float coeficienteRecta2 = recta2.getCoeficiente();

		//la recta de igualacion se consigue dejando las y = 0 y por lo tanto no tiene factorY
		if(factorYRecta1 == factorYRecta2){
			Recta rectaIgualacion = new Recta (0, factorXRecta1 - factorXRecta2, coeficienteRecta1 - coeficienteRecta2);
			//se obtiene la X cogiendo la y=0 porque por la recta de igualacion se ha ido
			xObtenido = rectaIgualacion.darXdadoY(0);
			//se obtiene la y cogiendo la xObtenido y metiendola en cualquiera de las 2 rectas que te pasan por parametro
			yObtenido = recta1.darYdadoX(xObtenido);
		}
		else{
			if(factorYRecta1 != 0){
				yObtenido = - coeficienteRecta1;
				xObtenido = recta2.darXdadoY(yObtenido);
			}
			else{
				xObtenido = - coeficienteRecta1;
				yObtenido = recta1.darYdadoX(xObtenido);
			}
		}
		
		
		//compruebacion de si cortan
		//si no se cortan tienen los mismos factores de x e y
		if((factorXRecta1 == factorXRecta2) && (factorYRecta1 == factorYRecta2)){
			punto = null;
		}
		else{
			punto = new Punto(xObtenido, yObtenido);
		}
		return punto;
	}

	/*	Test del Rayo:
	Consiste en trazar una semirecta, generalmente horizontal, desde el punto
	hasta hasta el infinito, y contar la cantidad de veces que corta al pol�gono.
	Si la cantidad es par entonces se encuentra fuera del pol�gono, y si se
	encuentra dentro la cantidad de cortes ser� impar.*/
	private boolean puntoEnPoligono(FloatArray vertices,int N, float puntoX, float puntoY){
		boolean dentro = false;
		int numIntersecciones = 0;
		int i;
		double xinters;
		float punto1X, punto1Y, punto2X, punto2Y;

		punto1X = vertices.get(0);
		punto1Y = vertices.get(1);
		for (i=2; i<N; i= i+2) {
		   punto2X = vertices.get(i % N);
		   punto2Y = vertices.get((i+1) % N);
		   if (puntoY > min2Puntos(punto1Y,punto2Y)) {
			   if (puntoY <= max2Punto(punto1Y,punto2Y)) {
			      if (puntoX <= max2Punto(punto1X,punto2X)) {
			          if (punto1Y != punto2Y) {
			              xinters = (puntoY - punto1Y)*(punto2X - punto1X)/(punto2Y - punto1Y) + punto1X;
			              if (punto1X == punto2X || puntoX <= xinters){
			            	  numIntersecciones++;
			              }
			          }
			      }
			   } 
		   }
		   
		   punto1X = punto2X;
		   punto1Y = punto2Y;
		}

		if (numIntersecciones % 2 == 0){
			dentro = false;
		}
		else{
			dentro = true;
		}
		return dentro;
		
	}
	

	private float min2Puntos(float punto1Y, float punto2Y) {
		float minimo;
		if(punto1Y < punto2Y){
			minimo = punto1Y;
		}
		else{
			minimo = punto2Y;
		}
		return minimo;
	}

	private float max2Punto(float punto1Y, float punto2Y) {
		float maximo;
		if(punto1Y > punto2Y){
			maximo = punto1Y;
		}
		else{
			maximo = punto2Y;
		}
		return maximo;
	}
	
	private boolean tieneDistanciaConOtrosVertices(Punto p, FloatArray vertices, float distancia){
		boolean tieneDistancia = true;
		int i = 0;
		while((i < vertices.size) && (tieneDistancia)){
			Punto p2 = new Punto(vertices.get(i), vertices.get(i+1));
			if(distancia2Puntos(p, p2) < distancia){
				tieneDistancia = false;
			}

			i = i+2;
		}
		
		return tieneDistancia;
	}
	
	private float distancia2Puntos(Punto p1, Punto p2){
		return (float) (Math.sqrt(Math.pow((p2.getX() - p1.getX()), 2) + Math.pow((p2.getY() - p1.getY()), 2)));
	}
	
	protected float areaRectangulo(float ladoX, float ladoY) {
		return ladoX*ladoY;
	}
	
	//Clases auxiliares
		public class Punto{

			private float x;
			private float y;
			
			public Punto(){
				
			}
			
			public Punto(float a, float b){
				x = a;
				y = b;
			}

			public float getX() {
				return x;
			}

			public void setX(float x) {
				this.x = x;
			}

			public float getY() {
				return y;
			}

			public void setY(float y) {
				this.y = y;
			}
			
			
		}
			
		public class Recta{

			private float factorX, factorY, coeficiente;
			
			public Recta(){
				
			}
			
			public Recta(float fX, float fY, float coef){
				factorX = fX;
				factorY = fY;
				coeficiente = coef;
			}

			public float getFactorX() {
				return factorX;
			}

			public void setFactorX(float factorX) {
				this.factorX = factorX;
			}

			public float getFactorY() {
				return factorY;
			}

			public void setFactorY(float factorY) {
				this.factorY = factorY;
			}

			public float getCoeficiente() {
				return coeficiente;
			}

			public void setCoeficiente(float coeficiente) {
				this.coeficiente = coeficiente;
			}

			public float darXdadoY(float y){
				return (- factorY * y - coeficiente) / factorX;
			}
				
			public float darYdadoX(float x){
				return (- factorX * x - coeficiente) / factorY;
			}
			
		}
}
