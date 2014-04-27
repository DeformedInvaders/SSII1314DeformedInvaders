package com.android.view;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import com.creation.data.MapaBits;
import com.creation.data.TTipoSticker;
import com.game.data.TTipoEntidad;
import com.lib.buffer.Dimensiones;
import com.lib.buffer.VertexArray;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;
import com.main.model.GamePreferences;

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
	private int colorFondo;

	// Parámetros de Texturas
	private static final int POS_TEXTURE_BACKGROUND = 0;
	private static final int POS_TEXTURE_MISSILE = POS_TEXTURE_BACKGROUND + GamePreferences.NUM_TYPE_BACKGROUNDS;
	private static final int POS_TEXTURE_OBSTACLE = POS_TEXTURE_MISSILE + GamePreferences.NUM_TYPE_MISSILES;
	private static final int POS_TEXTURE_CHARACTER_SKELETON = POS_TEXTURE_OBSTACLE + GamePreferences.NUM_TYPE_OBSTACLES;
	private static final int POS_TEXTURE_CHARACTER_STICKER = POS_TEXTURE_CHARACTER_SKELETON + GamePreferences.NUM_TYPE_CHARACTER;
	private static final int POS_TEXTURE_ENEMY_SKELETON = POS_TEXTURE_CHARACTER_STICKER + GamePreferences.NUM_TYPE_STICKERS;
	private static final int POS_TEXTURE_BUBBLE = POS_TEXTURE_ENEMY_SKELETON + GamePreferences.NUM_TYPE_OPPONENTS * (GamePreferences.NUM_TYPE_STICKERS + 1);
	
	private static final int NUM_TEXTURES = POS_TEXTURE_BUBBLE + GamePreferences.NUM_TYPE_BUBBLES;
	
	private int[] nombreTexturas;

	private FloatBuffer coordTexturaRectangulo;
	private FloatBuffer[] vertTextura;

	private boolean[] cargadaTextura;

	// Fondo
	private int[] indiceTexturaFondo;
	private float[] posFondo;
	private boolean[] dibujarFondo;

	protected boolean fondoFinalFijado;

	// Marco
	protected float marcoAnchuraInterior, marcoAlturaLateral, marcoAnchuraLateral;
	private FloatBuffer recMarcoLateral, recMarcoFrontal, recMarcoInterior;

	// Contexto
	protected Context mContext;

	/* Constructoras */

	public OpenGLRenderer(Context context, int color)
	{
		mContext = context;
		
		colorFondo = color;

		// Marcos
		actualizarMarcos();

		// Fondo
		indiceTexturaFondo = new int[GamePreferences.NUM_TYPE_BACKGROUNDS];
		dibujarFondo = new boolean[GamePreferences.NUM_TYPE_BACKGROUNDS];

		fondoFinalFijado = false;

		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			indiceTexturaFondo[i] = -1;
		}

		// Textura
		nombreTexturas = new int[NUM_TEXTURES];
		cargadaTextura = new boolean[NUM_TEXTURES];
		vertTextura = new FloatBuffer[NUM_TEXTURES];

		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
		coordTexturaRectangulo = BufferManager.construirBufferListaPuntos(texture);

		// Se inicializan los parámetros de la cámara en el
		// método onSurfaceChanged llamado automáticamente
		// después de la constructora.
	}

	/* Métodos Abstractos */
	
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchPointerDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean onTouchPointerUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	protected boolean reiniciar()
	{
		return false;
	}
	
	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		// Sombreado Suave
		gl.glShadeModel(GL10.GL_SMOOTH);

		// Color de Fondo Blanco
		gl.glClearColor(Color.red(colorFondo), Color.green(colorFondo), Color.blue(colorFondo), Color.alpha(colorFondo));

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
		
		// Fondo
		cargarTexturaFondo(gl);

		actualizarTexturaFondo();

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
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);

		// Limpiar Buffer de Color y de Profundidad
		gl.glClearColor(Color.red(colorFondo), Color.green(colorFondo), Color.blue(colorFondo), Color.alpha(colorFondo));
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Activar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Background
		dibujarFondo(gl);
	}

	/* Métodos de Obtención de Información */

	public float getScreenWidth()
	{
		return screenWidth;
	}

	public float getScreenHeight()
	{
		return screenHeight;
	}

	/* Métodos de Modificación de Cámara */

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

	private void camaraDrag(float dWorldX, float dWorldY)
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

	public void camaraDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		float worldX = convertPixelXToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertPixelYToWorldYCoordinate(pixelY, screenHeight);

		float lastWorldX = convertPixelXToWorldXCoordinate(lastPixelX, screenWidth);
		float lastWorldY = convertPixelYToWorldYCoordinate(lastPixelY, screenHeight);

		float dWorldX = lastWorldX - worldX;
		float dWorldY = lastWorldY - worldY;

		camaraDrag(dWorldX, dWorldY);
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

	/* Métodos de modificación de puntos */

	public void coordsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void coordsRotate(float ang, float pixelX, float pixelY, float screenWidth, float screenHeight) { }

	protected void trasladarVertices(float vx, float vy, VertexArray vertices)
	{
		BufferManager.trasladarVertices(vx, vy, vertices);
	}

	protected void escalarVertices(float fx, float fy, float cx, float cy, VertexArray vertices)
	{
		BufferManager.escalarVertices(fx, fy, cx, cy, vertices);
	}

	protected void escalarVertices(float fx, float fy, VertexArray vertices)
	{
		BufferManager.escalarVertices(fx, fy, vertices);
	}

	protected void rotarVertices(float ang, float cx, float cy, VertexArray vertices)
	{
		BufferManager.rotarVertices(ang, cx, cy, vertices);
	}

	protected void rotarVertices(float ang, VertexArray vertices)
	{
		BufferManager.rotarVertices(ang, vertices);
	}

	/* Métodos de Copia de Seguridad de la Cámara */

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
		if (camaraGuardada)
		{
			xLeft = lastXLeft;
			xRight = lastXRight;
			yTop = lastYTop;
			yBottom = lastYBot;
			xCentro = lastXCentro;
			yCentro = lastYCentro;
		}
	}

	/* Métodos de Captura de Pantalla y Marcos */

	private void actualizarMarcos()
	{
		float camaraHeight = yTop - yBottom;
		float camaraWidth = xRight - xLeft;

		marcoAlturaLateral = 0.1f * camaraHeight;
		marcoAnchuraInterior = camaraHeight - 2 * marcoAlturaLateral;
		marcoAnchuraLateral = (camaraWidth - marcoAnchuraInterior) / 2;

		float[] recA = { 0, 0, 0, marcoAnchuraInterior, marcoAnchuraLateral, 0, marcoAnchuraLateral, marcoAnchuraInterior };
		recMarcoLateral = BufferManager.construirBufferListaPuntos(recA);

		float[] recB = { 0, 0, 0, marcoAlturaLateral, camaraWidth, 0, camaraWidth, marcoAlturaLateral };
		recMarcoFrontal = BufferManager.construirBufferListaPuntos(recB);
		
		float[] recC = { 0, 0, 0, marcoAnchuraInterior, marcoAnchuraInterior, 0, marcoAnchuraInterior,marcoAnchuraInterior };
		recMarcoInterior = BufferManager.construirBufferListaPuntos(recC);
	}
	
	protected boolean isPuntoFueraMarco(float x, float y)
	{
		return (x < xLeft || x > xLeft + marcoAnchuraInterior || y < yBottom || y > yBottom + marcoAnchuraInterior);
	}

	protected void centrarPersonajeEnMarcoInicio(GL10 gl)
	{
		gl.glTranslatef(marcoAnchuraLateral, marcoAlturaLateral, 0.0f);
	}

	protected void centrarPersonajeEnMarcoFinal(GL10 gl)
	{
		gl.glTranslatef(-marcoAnchuraLateral, -marcoAlturaLateral, 0.0f);
	}

	protected void dibujarMarcoExterior(GL10 gl, int color)
	{
		dibujarMarcoFrontal(gl, color);
		dibujarMarcoLateral(gl, color);
	}

	protected void dibujarMarcoInterior(GL10 gl, int color)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, GamePreferences.DEEP_INSIDE_FRAMES);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(marcoAnchuraLateral, marcoAlturaLateral, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoInterior);
	
			gl.glPopMatrix();
	
		gl.glPopMatrix();
	}
	
	private void dibujarMarcoLateral(GL10 gl, int color)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, GamePreferences.DEEP_OUTSIDE_FRAMES);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(0, marcoAlturaLateral, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoLateral);
		
				gl.glTranslatef(marcoAnchuraLateral + marcoAnchuraInterior, 0, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoLateral);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	private void dibujarMarcoFrontal(GL10 gl, int color)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, GamePreferences.DEEP_OUTSIDE_FRAMES);
	
			gl.glPushMatrix();
			
			OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoFrontal);
	
				gl.glTranslatef(0, marcoAlturaLateral + marcoAnchuraInterior, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoFrontal);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	protected MapaBits capturaPantalla(GL10 gl)
	{
		return OpenGLManager.capturaPantalla(gl, (int) marcoAnchuraLateral, (int) marcoAlturaLateral, (int) marcoAnchuraInterior, (int) marcoAnchuraInterior);
	}

	/* Métodos de Conversión de Coordenadas */

	protected float convertPixelXToWorldXCoordinate(float pixelX, float screenWidth)
	{
		return xLeft + (xRight - xLeft) * pixelX / screenWidth;
	}

	protected float convertPixelYToWorldYCoordinate(float pixelY, float screenHeight)
	{
		return yBottom + (yTop - yBottom) * (screenHeight - pixelY) / screenHeight;
	}
	
	protected float convertPixelXToFrameXCoordinate(float pixelX, float screenWidth)
	{
		return convertWorldXToFrameXCoordinate(convertPixelXToWorldXCoordinate(pixelX, screenWidth));
	}
	
	protected float convertPixelYToFrameYCoordinate(float pixelY, float screenHeight)
	{
		return convertWorldYToFrameYCoordinate(convertPixelYToWorldYCoordinate(pixelY, screenHeight));
	}

	protected float convertWorldXToPixelXCoordinate(float worldX, float screenWidth)
	{
		return (worldX - xLeft) * screenWidth / (xRight - xLeft);
	}

	protected float convertWorldYToPixelYCoordinate(float worldY, float screenHeight)
	{
		return screenHeight - (worldY - yBottom) * screenHeight / (yTop - yBottom);
	}

	protected float convertWorldXToFrameXCoordinate(float worldX)
	{
		return worldX - marcoAnchuraLateral;
	}

	protected float convertWorldYToFrameYCoordinate(float worldY)
	{
		return worldY - marcoAlturaLateral;
	}

	protected float convertFrameXToWorldXCoordinate(float frameX)
	{
		return frameX + marcoAnchuraLateral;
	}

	protected float convertFrameYToWorldYCoordinate(float frameY)
	{
		return frameY + marcoAlturaLateral;
	}
	
	protected float convertFrameXToPixelXCoordinate(float frameX, float screenWidth)
	{
		return convertWorldXToPixelXCoordinate(convertFrameXToWorldXCoordinate(frameX), screenWidth);
	}

	protected float convertFrameYToPixelYCoordinate(float frameY, float screenHeight)
	{
		return convertWorldYToPixelYCoordinate(convertFrameYToWorldYCoordinate(frameY), screenHeight);
	}
	
	protected float convertFrameXToTextureXCoordinate(float frameX, float textureWidth)
	{
		return frameX / textureWidth;
	}
	
	protected float convertFrameYToTextureYCoordinate(float frameY, float textureHeight)
	{
		return (textureHeight - frameY) / textureHeight;
	}

	/* Métodos de Construcción de Texturas */

	// Métodos de Gestión de posición de Texturas

	private int obtenerPosicionTexturaMalla(TTipoEntidad tipoEntidad, int posEntidad)
	{
		switch (tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_SKELETON;
			case Enemigo:
				return POS_TEXTURE_ENEMY_SKELETON + posEntidad * (GamePreferences.NUM_TYPE_STICKERS + 1);
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
				return POS_TEXTURE_ENEMY_SKELETON + posEntidad * (GamePreferences.NUM_TYPE_STICKERS + 1) + tipoPegatina.ordinal() + 1;
			case Obstaculo:
				return POS_TEXTURE_OBSTACLE + posEntidad;
			case Misil:
				return POS_TEXTURE_MISSILE;
			case Burbuja:
				return POS_TEXTURE_BUBBLE + posEntidad;
			default:
				return -1;
		}
	}

	// Métodos de Contrucción de Textura

	protected VertexArray construirTextura(VertexArray vertices, float textureWidth, float textureHeight)
	{
		VertexArray textura = new VertexArray(vertices.getNumVertices());

		for (short i = 0; i < vertices.getNumVertices(); i++)
		{
			float frameX = vertices.getXVertex(i);
			float frameY = vertices.getYVertex(i);
			
			float coordX = convertFrameXToTextureXCoordinate(frameX, textureWidth);
			float coordY = convertFrameYToTextureYCoordinate(frameY, textureHeight);
			
			textura.addVertex(coordX, coordY);
		}
		
		return textura;
	}

	private void cargarTextura(GL10 gl, Bitmap textura, int posTextura)
	{
		OpenGLManager.cargarTextura(gl, textura, nombreTexturas, posTextura);

		cargadaTextura[posTextura] = true;
	}

	private void cargarTextura(GL10 gl, int indiceTextura, int posTextura)
	{
		Bitmap textura = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);

		cargarTextura(gl, textura, posTextura);
	}

	private void descargarTextura(int posTextura)
	{
		cargadaTextura[posTextura] = false;
	}

	// Métodos de Contrucción de Textura para Entidades

	public void cargarTexturaMalla(GL10 gl, Bitmap textura, TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1 && !cargadaTextura[posTextura])
		{
			cargarTextura(gl, textura, posTextura);
		}
	}

	public void descargarTexturaMalla(TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}
	
	private Dimensiones cargarTexturaRectangulo(GL10 gl, Bitmap bitmap, float textureHeight, float textureWidth, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && !cargadaTextura[posTextura])
		{			
			cargarTextura(gl, bitmap, posTextura);
			bitmap.recycle();

			VertexArray vertices = new VertexArray();
			
			if(tipoEntidad == TTipoEntidad.Personaje || tipoEntidad == TTipoEntidad.Enemigo)
			{
				vertices.addVertex(-textureWidth/2, -textureHeight/2);
				vertices.addVertex(-textureWidth/2, textureHeight/2);
				vertices.addVertex(textureWidth/2, -textureHeight/2);
				vertices.addVertex(textureWidth/2, textureHeight/2);
			}
			else
			{
				vertices.addVertex(0.0f, 0.0f);
				vertices.addVertex(0.0f, textureHeight);
				vertices.addVertex(textureWidth, 0.0f);
				vertices.addVertex(textureWidth, textureHeight);
			}
			
			vertTextura[posTextura] = BufferManager.construirBufferListaPuntos(vertices);

			return new Dimensiones(textureHeight, textureWidth);
		}

		return null;
	}

	public Dimensiones cargarTexturaRectangulo(GL10 gl, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);

		float textureHeight = bitmap.getHeight();
		float textureWidth = bitmap.getWidth();
		
		return cargarTexturaRectangulo(gl, bitmap, textureHeight, textureWidth, indiceTextura, tipoEntidad, posEntidad, posPegatina);
	}
	
	public Dimensiones cargarTexturaRectangulo(GL10 gl, float textureHeight, float textureWidth, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);
		
		return cargarTexturaRectangulo(gl, bitmap, textureHeight, textureWidth, indiceTextura, tipoEntidad, posEntidad, posPegatina);
	}

	public void descargarTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1)
		{
			descargarTextura(posTextura);
		}
	}

	// Métodos de Pintura de Texturas para Entidades

	public void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1 && cargadaTextura[posTextura])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLES, bufferPuntos, bufferCoordTextura, nombreTexturas[posTextura]);
		}
	}

	public void dibujarTexturaRectangulo(GL10 gl, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && cargadaTextura[posTextura])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[posTextura], coordTexturaRectangulo, nombreTexturas[posTextura]);
		}
	}

	/* Métodos de Pintura de Fondo */

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
			while(i < GamePreferences.NUM_TYPE_BACKGROUNDS - 1)
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
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			if (indiceTexturaFondo[i] != -1)
			{
				cargarTextura(gl, indiceTexturaFondo[i], POS_TEXTURE_BACKGROUND + i);
			}
		}

		if (posFondo == null)
		{
			posFondo = new float[GamePreferences.NUM_TYPE_BACKGROUNDS];
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS - 1; i++)
			{
				posFondo[i] = i * screenWidth;
			}
	
			posFondo[GamePreferences.NUM_TYPE_BACKGROUNDS - 1] = GamePreferences.NUM_ITERATION_BACKGROUND() * screenWidth;
		}
	}
	
	private void descargarTexturaFondo()
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			descargarTextura(POS_TEXTURE_BACKGROUND + i);
		}
	}

	private void dibujarFondo(GL10 gl)
	{
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			if (cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				if (dibujarFondo[i])
				{
					gl.glPushMatrix();
		
						gl.glTranslatef(posFondo[i], 0.0f, 0.0f);
			
						OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, vertTextura[POS_TEXTURE_BACKGROUND + i], coordTexturaRectangulo, nombreTexturas[POS_TEXTURE_BACKGROUND + i]);
		
					gl.glPopMatrix();
				}
			}
		}
	}

	private void actualizarTexturaFondo()
	{
		VertexArray vertices = new VertexArray();
		vertices.addVertex(xLeft, yBottom);
		vertices.addVertex(xLeft, yTop);
		vertices.addVertex(xRight, yBottom);
		vertices.addVertex(xRight, yTop);

		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			if (cargadaTextura[POS_TEXTURE_BACKGROUND + i])
			{
				vertTextura[POS_TEXTURE_BACKGROUND + i] = BufferManager.construirBufferListaPuntos(vertices);
			}
		}
	}

	protected void desplazarTexturaFondo()
	{
		int lastFondo = POS_TEXTURE_BACKGROUND + GamePreferences.NUM_TYPE_BACKGROUNDS - 1;

		// Activado de Último Fondo
		if (posFondo[lastFondo] >= screenWidth - GamePreferences.DIST_MOVIMIENTO_BACKGROUND() && posFondo[lastFondo] <= screenWidth + GamePreferences.DIST_MOVIMIENTO_BACKGROUND())
		{
			dibujarFondo[lastFondo] = true;
			
			for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS - 1; i++)
			{
				if (posFondo[i] > screenWidth)
				{
					dibujarFondo[i] = false;
				}
			}
		}
		
		if (posFondo[lastFondo] <= 0.0f)
		{
			fondoFinalFijado = true;

			for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS - 1; i++)
			{
				dibujarFondo[i] = false;
			}
		}

		// Desplazamiento
		for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS; i++)
		{
			posFondo[i] -= GamePreferences.DIST_MOVIMIENTO_BACKGROUND();
		}

		// Reinicio de Fondo
		if (posFondo[lastFondo] > screenWidth)
		{
			for (int i = 0; i < GamePreferences.NUM_TYPE_BACKGROUNDS - 1; i++)
			{
				if (posFondo[i] <= -screenWidth)
				{
					posFondo[i] = (GamePreferences.NUM_TYPE_BACKGROUNDS - 2) * screenWidth;
				}
			}
		}
	}
	
	/* Métodos de Guardado de Información */
	
	protected BackgroundDataSaved backgroundSaveData()
	{		
		descargarTexturaFondo();
		
		return new BackgroundDataSaved(indiceTexturaFondo, posFondo, dibujarFondo);
	}

	protected void backgroundRestoreData(BackgroundDataSaved data)
	{		
		indiceTexturaFondo = data.getIndiceTexturaFondo();
		posFondo = data.getPosFondo();
		dibujarFondo = data.getDibujarFondo();
	}
}
