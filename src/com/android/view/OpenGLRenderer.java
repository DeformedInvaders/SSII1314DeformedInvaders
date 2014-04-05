package com.android.view;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

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
import com.game.data.TTipoSticker;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.project.main.GamePreferences;

public abstract class OpenGLRenderer implements Renderer
{
	// Parámetros de la Cámara
	private float xLeft, xRight, yTop, yBottom, xCentro, yCentro;

	// Copia Seguridad de la Cámara
	private boolean camaraGuardada;
	private float lastXLeft, lastXRight, lastYTop, lastYBot, lastXCentro, lastYCentro;

	// Parámetros del Puerto de Vista
	private int screenHeight, screenWidth;

	// Parámetros de la Escena
	protected static final int SIZELINE = 3;
	protected static final int POINTWIDTH = 7;

	protected static final float MAX_DISTANCE_PIXELS = 10.0f;

	// Parámetros de Texturas
	private static final int POS_TEXTURE_BACKGROUND = 0;
	private static final int POS_TEXTURE_FISSURE = POS_TEXTURE_BACKGROUND + GamePreferences.MAX_TEXTURE_BACKGROUND;
	private static final int POS_TEXTURE_OBSTACLE = POS_TEXTURE_FISSURE + GamePreferences.MAX_TEXTURE_FISSURE;
	private static final int POS_TEXTURE_CHARACTER_SKELETON = POS_TEXTURE_OBSTACLE + GamePreferences.MAX_TEXTURE_OBSTACLE;
	private static final int POS_TEXTURE_CHARACTER_STICKER = POS_TEXTURE_CHARACTER_SKELETON + GamePreferences.MAX_TEXTURE_CHARACTER;
	private static final int POS_TEXTURE_ENEMY_SKELETON = POS_TEXTURE_CHARACTER_STICKER + GamePreferences.MAX_TEXTURE_STICKER;
	private static final int POS_TEXTURE_BUBBLE = POS_TEXTURE_ENEMY_SKELETON + GamePreferences.MAX_TEXTURE_ENEMY * (GamePreferences.MAX_TEXTURE_STICKER + 1);
	private static final int POS_TEXTURE_HEART = POS_TEXTURE_BUBBLE + GamePreferences.MAX_TEXTURE_BUBBLE;
	
	private static final int NUM_TEXTURES = POS_TEXTURE_HEART + GamePreferences.MAX_TEXTURE_HEART;
	//private static final int NUM_TEXTURES = GamePreferences.MAX_TEXTURE_BACKGROUND + GamePreferences.MAX_TEXTURE_FISSURE + GamePreferences.MAX_TEXTURE_OBSTACLE + GamePreferences.MAX_TEXTURE_CHARACTER + GamePreferences.MAX_TEXTURE_STICKER + (GamePreferences.MAX_TEXTURE_ENEMY * GamePreferences.MAX_TEXTURE_STICKER + 1) + GamePreferences.MAX_TEXTURE_BUBBLE + GamePreferences.MAX_TEXTURE_HEART;
	
	private int[] nombreTexturas;

	private FloatBuffer coordTextura;
	private FloatBuffer[] vertTextura;

	private boolean[] cargadaTextura;

	// Fondo
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
		indiceTexturaFondo = new int[GamePreferences.MAX_TEXTURE_BACKGROUND];
		dibujarFondo = new boolean[GamePreferences.MAX_TEXTURE_BACKGROUND];
		posFondo = new float[GamePreferences.MAX_TEXTURE_BACKGROUND];

		fondoFinalFijado = false;

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND; i++)
		{
			indiceTexturaFondo[i] = -1;
		}

		// Textura
		nombreTexturas = new int[NUM_TEXTURES];
		cargadaTextura = new boolean[NUM_TEXTURES];
		vertTextura = new FloatBuffer[NUM_TEXTURES];

		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		coordTextura = BufferManager.construirBufferListaPuntos(texture);

		// Se inicializan los parámetros de la cámara en el
		// método onSurfaceChanged llamado automáticamente
		// después de la constructora.
	}

	/* SECTION Métodos Abstractos */

	protected abstract boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer);

	protected abstract boolean onMultiTouchEvent();

	protected abstract boolean reiniciar();

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
		xCentro = (xRight + xLeft) / 2.0f;
		yCentro = (yTop + yBottom) / 2.0f;
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
		cargarTexturaFondo(gl);

		actualizarTexturaFondo();
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

		// Background
		dibujarFondo(gl);
	}

	/* SECTION Métodos de Obtención de Información */

	public float getScreenWidth()
	{
		return screenWidth;
	}

	public float getScreenHeight()
	{
		return screenHeight;
	}

	/* SECTION Métodos de Modificación de Cámara */

	public void camaraZoom(float factor)
	{
		float newAncho = (xRight - xLeft) * factor;
		float newAlto = (yTop - yBottom) * factor;

		xRight = xCentro + newAncho / 2.0f;
		xLeft = xCentro - newAncho / 2.0f;
		yTop = yCentro + newAlto / 2.0f;
		yBottom = yCentro - newAlto / 2.0f;

		actualizarMarcos();
		actualizarTexturaFondo();
	}

	public void camaradrag(float dWorldX, float dWorldY)
	{
		xLeft += dWorldX;
		xRight += dWorldX;
		yBottom += dWorldY;
		yTop += dWorldY;

		xCentro = (xRight + xLeft) / 2.0f;
		yCentro = (yTop + yBottom) / 2.0f;

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

		xCentro = (xRight + xLeft) / 2.0f;
		yCentro = (yTop + yBottom) / 2.0f;

		actualizarMarcos();
		actualizarTexturaFondo();
	}

	/* SECTION Métodos de modificación de puntos */

	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight) { }

	protected void trasladarVertices(float vx, float vy, FloatArray vertices)
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

	protected void escalarVertices(float fx, float fy, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		escalarVertices(fx, fy, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	protected void escalarVertices(float fx, float fy, FloatArray vertices)
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

	protected void rotarVertices(float ang, float cx, float cy, FloatArray vertices)
	{
		trasladarVertices(-cx, -cy, vertices);
		rotarVertices(ang, vertices);
		trasladarVertices(cx, cy, vertices);
	}

	protected void rotarVertices(float ang, FloatArray vertices)
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
		if (camaraGuardada) {
			xLeft = lastXLeft;
			xRight = lastXRight;
			yTop = lastYTop;
			yBottom = lastYBot;
			xCentro = lastXCentro;
			yCentro = lastYCentro;
		}
	}

	/* SECTION Métodos de Captura de Pantalla y Marcos */

	private void actualizarMarcos()
	{
		float camaraHeight = yTop - yBottom;
		float camaraWidth = xRight - xLeft;

		marcoB = 0.1f * camaraHeight;
		marcoA = camaraHeight - 2 * marcoB;
		marcoC = (camaraWidth - marcoA) / 2;

		float[] recA = { 0, 0, 0, camaraHeight, marcoC, 0, marcoC, camaraHeight };
		recMarcoA = BufferManager.construirBufferListaPuntos(recA);

		float[] recB = { 0, 0, 0, marcoB, marcoA, 0, marcoA, marcoB };
		recMarcoB = BufferManager.construirBufferListaPuntos(recB);
	}

	protected boolean isPoligonoDentroMarco(FloatArray vertices)
	{
		int i = 0;
		while (i < vertices.size)
		{
			float x = vertices.get(i);
			float y = vertices.get(i + 1);

			if (x <= marcoC || x >= marcoC + marcoA || y <= marcoB || y >= marcoB + marcoA)
			{
				return false;
			}

			i = i + 2;
		}

		return true;
	}

	protected boolean recortarPoligonoDentroMarco(FloatArray vertices)
	{
		int i = 0;
		while (i < vertices.size)
		{
			float frameX = convertToFrameXCoordinate(vertices.get(i));
			float frameY = convertToFrameYCoordinate(vertices.get(i + 1));

			vertices.set(i, frameX);
			vertices.set(i + 1, frameY);

			i = i + 2;
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

	/* SECTION Métodos de Conversión de Coordenadas */

	protected float convertToWorldXCoordinate(float pixelX, float screenWidth)
	{
		return xLeft + (xRight - xLeft) * pixelX / screenWidth;
	}

	protected float convertToWorldYCoordinate(float pixelY, float screenHeight)
	{
		return yBottom + (yTop - yBottom) * (screenHeight - pixelY)
				/ screenHeight;
	}

	protected float convertToPixelXCoordinate(float worldX, float screenWidth)
	{
		return (worldX - xLeft) * screenWidth / (xRight - xLeft);
	}

	protected float convertToPixelYCoordinate(float worldY, float screenHeight)
	{
		return screenHeight - (worldY - yBottom) * screenHeight / (yTop - yBottom);
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

	/* SECTION Métodos de Búsqueda de Pixeles */

	protected short buscarPixel(FloatArray vertices, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		int minpos = -1;
		int j = 0;
		while (j < vertices.size)
		{
			float framepX = vertices.get(j);
			float framepY = vertices.get(j + 1);

			float worldpX = convertFromFrameXCoordinate(framepX);
			float worldpY = convertFromFrameYCoordinate(framepY);

			float lastpX = convertToPixelXCoordinate(worldpX, screenWidth);
			float lastpY = convertToPixelYCoordinate(worldpY, screenHeight);

			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastpX, lastpY));
			if (distancia < MAX_DISTANCE_PIXELS)
			{
				minpos = j / 2;
				return (short) minpos;
			}

			j = j + 2;
		}

		return (short) minpos;
	}

	/* SECTION Métodos de Pintura en la Tubería Gráfica */

	// Pintura de un Buffer de Puntos
	protected void dibujarBuffer(GL10 gl, int type, int size, int color, FloatBuffer bufferPuntos)
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

	public void dibujarBuffer(GL10 gl, int color, FloatBuffer bufferPuntos)
	{
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, color, bufferPuntos);
	}

	// Pintura de una Lista de Handles
	protected void dibujarListaIndiceHandle(GL10 gl, int color, FloatBuffer handle, FloatArray posiciones)
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
				dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
				gl.glPopMatrix();
			}

			i = i + 4;
		}

		gl.glPopMatrix();
	}

	// Pintura de una Lista de Handles
	protected void dibujarListaHandle(GL10 gl, int color, FloatBuffer handle, FloatArray posiciones)
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
			dibujarBuffer(gl, GL10.GL_TRIANGLE_FAN, SIZELINE, color, handle);
			gl.glPopMatrix();

			i = i + 2;
		}

		gl.glPopMatrix();
	}

	/* SECTION Métodos de Construcción de Texturas */

	// Métodos de Gestión de posición de Texturas

	private int obtenerPosicionTexturaMalla(TTipoEntidad tipoEntidad)
	{
		switch (tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_SKELETON;
			case Enemigo:
				return POS_TEXTURE_ENEMY_SKELETON;
			default:
				return -1;
		}
	}

	private int obtenerPosicionTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker tipoPegatina)
	{
		switch (tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_STICKER + tipoPegatina.ordinal();
			case Enemigo:
				// FIXME:
				return POS_TEXTURE_ENEMY_SKELETON + posEntidad;
				// return POS_TEXTURE_ENEMY_SKELETON * (posEntidad + 1) + tipoPegatina.ordinal();
			case Obstaculo:
				return POS_TEXTURE_OBSTACLE + posEntidad;
			case Grieta:
				return POS_TEXTURE_FISSURE;
			case Burbuja:
				return POS_TEXTURE_BUBBLE + posEntidad;
			case Corazon:
				return POS_TEXTURE_HEART + tipoPegatina.ordinal();
			default:
				return -1;
		}
	}

	// Métodos de Contrucción de Textura

	protected FloatArray construirTextura(FloatArray puntos, float textureWidth, float textureHeight)
	{
		FloatArray textura = new FloatArray(puntos.size);

		int i = 0;
		while (i < puntos.size)
		{
			float frameX = puntos.get(i);
			float frameY = puntos.get(i + 1);

			// Conversión a Coordenadas de Textura
			float coordX = frameX / textureWidth;
			float coordY = (textureHeight - frameY) / textureHeight;

			textura.add(coordX);
			textura.add(coordY);

			i = i + 2;
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

	// Métodos de Contrucción de Textura para Entidades

	public void cargarTexturaMalla(GL10 gl, Bitmap textura, TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if (posTextura != -1 && !cargadaTextura[posTextura])
		{
			cargarTextura(gl, textura, posTextura);
		}
	}

	public void descargarTexturaMalla(TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if (posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}

	public float cargarTexturaRectangulo(GL10 gl, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);

		float textureHeight = bitmap.getHeight();
		float textureWidth = bitmap.getWidth();
		
		return cargarTexturaRectangulo(gl, bitmap, textureHeight, textureWidth, indiceTextura, tipoEntidad, posEntidad, posPegatina);
	}
	
	public float cargarTexturaRectangulo(GL10 gl, float textureHeight, float textureWidth, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);
		
		return cargarTexturaRectangulo(gl, bitmap, textureHeight, textureWidth, indiceTextura, tipoEntidad, posEntidad, posPegatina);
	}
	
	private float cargarTexturaRectangulo(GL10 gl, Bitmap bitmap, float textureHeight, float textureWidth, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && !cargadaTextura[posTextura])
		{			
			cargarTextura(gl, bitmap, posTextura);
			bitmap.recycle();

			FloatArray puntos = new FloatArray();
			
			if(tipoEntidad == TTipoEntidad.Personaje)// || tipoEntidad == TTipoEntidad.Enemigo)
			{
				puntos.add(-textureWidth/2);	puntos.add(-textureHeight/2);
				puntos.add(-textureWidth/2);	puntos.add(textureHeight/2);
				puntos.add(textureWidth/2);		puntos.add(-textureHeight/2);
				puntos.add(textureWidth/2);		puntos.add(textureHeight/2);
			}
			else
			{
				puntos.add(0.0f);				puntos.add(0.0f);
				puntos.add(0.0f);				puntos.add(textureHeight);
				puntos.add(textureWidth);		puntos.add(0.0f);
				puntos.add(textureWidth);		puntos.add(textureHeight);
			}
			
			vertTextura[posTextura] = BufferManager.construirBufferListaPuntos(puntos);

			return textureHeight;
		}

		return 0;
	}

	public void descargarTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}

	// Métodos de Contrucción de Textura para Fase Creación

	protected void cargarTexturaMalla(GL10 gl, Bitmap textura)
	{
		cargarTexturaMalla(gl, textura, TTipoEntidad.Personaje);
	}

	protected void descargarTexturaMalla()
	{
		descargarTexturaMalla(TTipoEntidad.Personaje);
	}

	protected void cargarTexturaRectangulo(GL10 gl, int indiceTextura, TTipoSticker posPegatina)
	{
		cargarTexturaRectangulo(gl, indiceTextura, TTipoEntidad.Personaje, 0, posPegatina);
	}

	protected void descargarTexturaRectangulo(TTipoSticker posPegatina)
	{
		descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, posPegatina);
	}

	// Métodos de Pintura de Texturas

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
			
			gl.glDrawArrays(type, 0, bufferPuntos.capacity() / 2);
			
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	// Métodos de Pintura de Texturas para Entidades

	public void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, TTipoEntidad tipo)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipo);

		if (posTextura != -1 && cargadaTextura[posTextura])
		{
			dibujarTextura(gl, GL10.GL_TRIANGLES, bufferPuntos, bufferCoordTextura, posTextura);
		}
	}

	public void dibujarTexturaRectangulo(GL10 gl, float x, float y, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina, float scaleX, float scaleY)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && cargadaTextura[posTextura])
		{
			gl.glPushMatrix();

				gl.glTranslatef(x, y, 0.0f);
	
				gl.glScalef(scaleX, scaleY, 0.0f);
	
				dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);

			gl.glPopMatrix();
		}
	}

	public void dibujarTexturaRectangulo(GL10 gl, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina, float scaleX, float scaleY)
	{
		dibujarTexturaRectangulo(gl, 0.0f, 0.0f, tipoEntidad, posEntidad, posPegatina, scaleX, scaleY);
	}

	public void dibujarTexturaRectangulo(GL10 gl, float x, float y, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		dibujarTexturaRectangulo(gl, x, y, tipoEntidad, posEntidad, posPegatina, 1.0f, 1.0f);
	}

	// Métodos de Pintura de Textura para Fase Creación

	protected void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura)
	{
		dibujarTexturaMalla(gl, bufferPuntos, bufferCoordTextura, TTipoEntidad.Personaje);
	}

	protected void dibujarTexturaRectangulo(GL10 gl, float x, float y, TTipoSticker posPegatina)
	{
		dibujarTexturaRectangulo(gl, x, y, TTipoEntidad.Personaje, 0, posPegatina);
	}

	/* SECTION Métodos de Pintura de Fondo */

	protected void seleccionarTexturaFondo(int... indiceTexturas)
	{		
		if(indiceTexturas.length == 1)
		{
			indiceTexturaFondo[0] = indiceTexturas[0];
			dibujarFondo[0] = true;
		}	
		else
		{
			int i = 0;
			while(i < GamePreferences.MAX_TEXTURE_BACKGROUND - 1)
			{
				indiceTexturaFondo[i] = indiceTexturas[i % (indiceTexturas.length - 1)];
				dibujarFondo[i] = true;		
				i++;
			}
			
			indiceTexturaFondo[i] = indiceTexturas[indiceTexturas.length - 1];
			dibujarFondo[i] = false;
		}
	}

	private void cargarTexturaFondo(GL10 gl)
	{
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND; i++)
		{
			if (indiceTexturaFondo[i] != -1)
			{
				cargarTextura(gl, indiceTexturaFondo[i], POS_TEXTURE_BACKGROUND + i);
			}
		}

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND - 1; i++)
		{
			posFondo[i] = i * screenWidth;
		}

		posFondo[GamePreferences.MAX_TEXTURE_BACKGROUND - 1] = GamePreferences.NUM_ITERATION_BACKGROUND * screenWidth;
	}

	private void dibujarTexturaFondo(GL10 gl, boolean dibujarFondo, float posFondo, int posTextura)
	{
		if (dibujarFondo)
		{
			gl.glPushMatrix();

				gl.glTranslatef(posFondo, 0.0f, 0.0f);
	
				dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTextura, posTextura);

			gl.glPopMatrix();
		}
	}

	private void dibujarFondo(GL10 gl)
	{
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND; i++)
		{
			if (cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				dibujarTexturaFondo(gl, dibujarFondo[i], posFondo[i], POS_TEXTURE_BACKGROUND + i);
			}
		}
	}

	private void actualizarTexturaFondo()
	{
		FloatArray puntos = new FloatArray();
		puntos.add(xLeft);		puntos.add(yBottom);
		puntos.add(xLeft);		puntos.add(yTop);
		puntos.add(xRight);		puntos.add(yBottom);
		puntos.add(xRight);		puntos.add(yTop);

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND; i++)
		{
			if (cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				vertTextura[POS_TEXTURE_BACKGROUND + i] = BufferManager.construirBufferListaPuntos(puntos);
			}
		}
	}

	protected void desplazarFondo()
	{
		int lastFondo = POS_TEXTURE_BACKGROUND + GamePreferences.MAX_TEXTURE_BACKGROUND - 1;

		// Activado de Último Fondo
		if (posFondo[lastFondo] <= screenWidth)
		{
			dibujarFondo[lastFondo] = true;

			if (posFondo[lastFondo] <= 0.0f)
			{
				fondoFinalFijado = true;

				for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND - 1; i++)
				{
					dibujarFondo[i] = false;
				}
			}
		}

		// Desplazamiento
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND; i++)
		{
			posFondo[i] -= GamePreferences.DIST_MOVIMIENTO_BACKGROUND;
		}

		// Reinicio de Fondo
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_BACKGROUND - 1; i++)
		{
			if (posFondo[i] <= -screenWidth)
			{
				posFondo[i] = screenWidth;
			}
		}
	}
}
