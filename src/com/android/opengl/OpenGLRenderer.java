package com.android.opengl;

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
	private TTipoFondoRenderer tipoFondo;
	private TTipoTexturasRenderer tipoTexturas;
	private FloatBuffer coordTexturaRectangulo;
	
	private int numFondos, numCharacters, numTexturas;
	
	// Texturas Personaje
	private int POS_TEXTURE_CHARACTER_SKELETON, POS_TEXTURE_CHARACTER_STICKER, POS_TEXTURE_CHARACTER_BUBBLE, POS_TEXTURE_CHARACTER_PLATFORM;
	
	// Texturas Video
	private int POS_TEXTURE_ANIMATED_OBJECT, POS_TEXTURE_INANIMATED_OBJECT;
	
	// Texturas Juego
	private int POS_TEXTURE_BOSS_SKELETON, POS_TEXTURE_BOSS_STICKER;
	private int POS_TEXTURE_BOSS_BUBBLE, POS_TEXTURE_BOSS_PLATFORM;
	private int POS_TEXTURE_MISSILE, POS_TEXTURE_OBSTACLE;
	private int POS_TEXTURE_ENEMY_SKELETON, POS_TEXTURE_ENEMY_STICKER;
	private int POS_TEXTURE_CHARACTER_SHOT, POS_TEXTURE_BOSS_SHOT;
	private int POS_TEXTURE_CHARACTER_WEAPON, POS_TEXTURE_BOSS_WEAPON;

	// Entidades
	private int[] nombreTexturaEntidades;
	private boolean[] cargadaTexturaEntidades;
	private FloatBuffer[] verticesTexturaEntidades;
	
	// Fondo
	private int[] nombreTexturaFondo, indiceTexturaFondo;
	private float[] posicionTexturaFondo;
	private boolean[] dibujarTexturaFondo, cargadaTexturaFondo;
	private FloatBuffer[] verticesTexturaFondo;
	
	private int fondoActual;
	private boolean fondosCargados, fondoFinalFijado;

	// Marco
	protected float marcoAnchuraInterior, marcoAlturaLateral, marcoAnchuraLateral;
	private FloatBuffer recMarcoLateral, recMarcoFrontal, recMarcoInterior;

	// Contexto
	protected Context mContext;

	/* Constructoras */
	
	public OpenGLRenderer(Context context, TTipoFondoRenderer fondo, TTipoTexturasRenderer texturas)
	{
		this(context, fondo, texturas, Color.argb(0, 0, 0, 0));
	}

	public OpenGLRenderer(Context context, TTipoFondoRenderer fondo, TTipoTexturasRenderer texturas, int color)
	{
		mContext = context;
		
		colorFondo = color;
		tipoFondo = fondo;
		numFondos = tipoFondo.getNumBackgrounds();
		tipoTexturas = texturas;
		numCharacters = tipoTexturas.getNumCharacters();
		numTexturas = tipoTexturas.getNumTextures();

		// Marcos
		actualizarMarcos();

		// Fondos
		if (numFondos > 0)
		{
			nombreTexturaFondo = new int[numFondos];
			indiceTexturaFondo = new int[numFondos];
			posicionTexturaFondo = new float[numFondos];
			dibujarTexturaFondo = new boolean[numFondos];
			cargadaTexturaFondo = new boolean[numFondos];
			verticesTexturaFondo = new FloatBuffer[numFondos];
	
			fondosCargados = false;
			fondoFinalFijado = false;
			fondoActual = 0;
	
			for (int i = 0; i < numFondos; i++)
			{
				indiceTexturaFondo[i] = -1;
			}
		}

		// Textura
		if (numTexturas > 0)
		{
			nombreTexturaEntidades = new int[numTexturas];
			cargadaTexturaEntidades = new boolean[numTexturas];
			verticesTexturaEntidades = new FloatBuffer[numTexturas];
	
			coordTexturaRectangulo = BufferManager.construirBufferTextura();
		}
		
		// Personaje
		POS_TEXTURE_CHARACTER_SKELETON = 0;
		POS_TEXTURE_CHARACTER_STICKER = POS_TEXTURE_CHARACTER_SKELETON + numCharacters;
		
		// Video
		POS_TEXTURE_INANIMATED_OBJECT = POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * numCharacters);
		POS_TEXTURE_ANIMATED_OBJECT = POS_TEXTURE_INANIMATED_OBJECT + GamePreferences.NUM_TYPE_INANIMATED_OBJECTS;
		
		// Juego
		POS_TEXTURE_CHARACTER_BUBBLE = POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * numCharacters);
		POS_TEXTURE_CHARACTER_PLATFORM = POS_TEXTURE_CHARACTER_BUBBLE + GamePreferences.NUM_TYPE_BUBBLES;
		
		POS_TEXTURE_BOSS_SKELETON = POS_TEXTURE_CHARACTER_PLATFORM + GamePreferences.NUM_TYPE_PLATFORMS;
		POS_TEXTURE_BOSS_STICKER = POS_TEXTURE_BOSS_SKELETON + 1;
		
		POS_TEXTURE_BOSS_BUBBLE = POS_TEXTURE_BOSS_STICKER + GamePreferences.NUM_TYPE_STICKERS;
		POS_TEXTURE_BOSS_PLATFORM = POS_TEXTURE_BOSS_BUBBLE + GamePreferences.NUM_TYPE_BUBBLES;
				
		POS_TEXTURE_MISSILE = POS_TEXTURE_BOSS_PLATFORM + GamePreferences.NUM_TYPE_PLATFORMS;
		POS_TEXTURE_OBSTACLE = POS_TEXTURE_MISSILE + GamePreferences.NUM_TYPE_MISSILES;
		
		POS_TEXTURE_ENEMY_SKELETON = POS_TEXTURE_OBSTACLE + GamePreferences.NUM_TYPE_OBSTACLES;
		POS_TEXTURE_ENEMY_STICKER = POS_TEXTURE_ENEMY_SKELETON + GamePreferences.NUM_TYPE_ENEMIES;

		POS_TEXTURE_CHARACTER_SHOT = POS_TEXTURE_ENEMY_STICKER + (GamePreferences.NUM_TYPE_STICKERS * GamePreferences.NUM_TYPE_ENEMIES);
		POS_TEXTURE_BOSS_SHOT = POS_TEXTURE_CHARACTER_SHOT + GamePreferences.NUM_TYPE_SHOTS;
		
		POS_TEXTURE_CHARACTER_WEAPON = POS_TEXTURE_BOSS_SHOT + GamePreferences.NUM_TYPE_SHOTS;
		POS_TEXTURE_BOSS_WEAPON = POS_TEXTURE_CHARACTER_WEAPON + GamePreferences.NUM_TYPE_WEAPONS;
		
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
	}

	/* Métodos de modificación de puntos */

	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight) { }

	public void pointsRestore() { }
	
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
		
		marcoAlturaLateral = GamePreferences.MARCO_ALTURA_LATERAL(camaraWidth, camaraHeight);
		marcoAnchuraInterior = GamePreferences.MARCO_ANCHURA_INTERIOR(camaraWidth, camaraHeight);
		marcoAnchuraLateral = GamePreferences.MARCO_ANCHURA_LATERAL(camaraWidth, camaraHeight);

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

	protected void dibujarMarcoExterior(GL10 gl, int color, float deep)
	{
		dibujarMarcoFrontal(gl, color, deep);
		dibujarMarcoLateral(gl, color, deep);
	}
	
	protected void dibujarMarcoCompleto(GL10 gl, int color, float deep)
	{
		dibujarMarcoFrontal(gl, color, deep);
		dibujarMarcoInterior(gl, color, deep);
		dibujarMarcoLateral(gl, color, deep);
	}

	protected void dibujarMarcoInterior(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(marcoAnchuraLateral, marcoAlturaLateral, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoInterior);
	
			gl.glPopMatrix();
	
		gl.glPopMatrix();
	}
	
	private void dibujarMarcoLateral(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(0, marcoAlturaLateral, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoLateral);
		
				gl.glTranslatef(marcoAnchuraLateral + marcoAnchuraInterior, 0, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoLateral);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	private void dibujarMarcoFrontal(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
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
				return POS_TEXTURE_CHARACTER_SKELETON + posEntidad;
			case Enemigo:
				return POS_TEXTURE_ENEMY_SKELETON + posEntidad;
			case Jefe:
				return POS_TEXTURE_BOSS_SKELETON;
			default:
				return -1;
		}
	}

	private int obtenerPosicionTexturaRectangulo(TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker tipoPegatina)
	{
		switch (tipoEntidad)
		{
			case Personaje:
				return POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * posEntidad) + tipoPegatina.ordinal();
			case Jefe:
				return POS_TEXTURE_BOSS_STICKER + tipoPegatina.ordinal();
			case Enemigo:
				return POS_TEXTURE_ENEMY_STICKER + (GamePreferences.NUM_TYPE_STICKERS * posEntidad) + tipoPegatina.ordinal();
			case Obstaculo:
				return POS_TEXTURE_OBSTACLE + posEntidad;
			case Misil:
				return POS_TEXTURE_MISSILE + posEntidad;
			case BurbujaPersonaje:
				return POS_TEXTURE_CHARACTER_BUBBLE + posEntidad;
			case BurbujaBoss:
				return POS_TEXTURE_BOSS_BUBBLE + posEntidad;
			case PlataformaPersonaje:
				return POS_TEXTURE_CHARACTER_PLATFORM + posEntidad;
			case PlataformaBoss:
				return POS_TEXTURE_BOSS_PLATFORM + posEntidad;
			case DisparoPersonaje:
				return POS_TEXTURE_CHARACTER_SHOT + posEntidad;
			case DisparoBoss:
				return POS_TEXTURE_BOSS_SHOT + posEntidad;
			case ArmaPersonaje:
				return POS_TEXTURE_CHARACTER_WEAPON + posEntidad;
			case ArmaBoss:
				return POS_TEXTURE_BOSS_WEAPON + posEntidad;
			case ObjetoInanimado:
				return POS_TEXTURE_INANIMATED_OBJECT + posEntidad;
			case ObjetoAnimado:
				return POS_TEXTURE_ANIMATED_OBJECT + posEntidad;
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

	private void cargarTextura(GL10 gl, Bitmap textura, int[] nombreTextura, boolean[] cargadaTextura, int posTextura)
	{
		OpenGLManager.cargarTextura(gl, textura, nombreTextura, posTextura);

		cargadaTextura[posTextura] = true;
	}

	private void cargarTextura(GL10 gl, int indiceTextura, int[] nombreTextura, boolean[] cargadaTextura, int posTextura)
	{
		Bitmap textura = BitmapFactory.decodeResource(mContext.getResources(), indiceTextura);

		cargarTextura(gl, textura, nombreTextura, cargadaTextura, posTextura);
	}

	private void descargarTextura(boolean[] cargadaTextura, int posTextura)
	{
		cargadaTextura[posTextura] = false;
	}

	// Métodos de Contrucción de Textura para Entidades

	public void cargarTexturaMalla(GL10 gl, Bitmap textura, TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1 && !cargadaTexturaEntidades[posTextura])
		{
			cargarTextura(gl, textura, nombreTexturaEntidades, cargadaTexturaEntidades, posTextura);
		}
	}

	public void descargarTexturaMalla(TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1)
		{
			descargarTextura(cargadaTexturaEntidades, posTextura);
		}
	}
	
	private Dimensiones cargarTexturaRectangulo(GL10 gl, Bitmap bitmap, float textureHeight, float textureWidth, int indiceTextura, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && !cargadaTexturaEntidades[posTextura])
		{			
			cargarTextura(gl, bitmap, nombreTexturaEntidades, cargadaTexturaEntidades, posTextura);
			bitmap.recycle();

			VertexArray vertices = new VertexArray();
			
			if(tipoEntidad == TTipoEntidad.Personaje || tipoEntidad == TTipoEntidad.Enemigo || tipoEntidad == TTipoEntidad.Jefe)
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
			
			verticesTexturaEntidades[posTextura] = BufferManager.construirBufferListaPuntos(vertices);

			return new Dimensiones(textureHeight, textureWidth);
		}

		android.util.Log.d("TEST", "NULL TEXTURE: "+tipoEntidad.toString()+" posTextura "+posTextura);
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
			descargarTextura(cargadaTexturaEntidades, posTextura);
		}
	}

	// Métodos de Pintura de Texturas para Entidades

	public void dibujarTexturaMalla(GL10 gl, FloatBuffer bufferPuntos, FloatBuffer bufferCoordTextura, TTipoEntidad tipoEntidad, int posEntidad)
	{
		int posTextura = obtenerPosicionTexturaMalla(tipoEntidad, posEntidad);

		if (posTextura != -1 && cargadaTexturaEntidades[posTextura])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLES, bufferPuntos, bufferCoordTextura, nombreTexturaEntidades[posTextura]);
		}
	}

	public void dibujarTexturaRectangulo(GL10 gl, TTipoEntidad tipoEntidad, int posEntidad, TTipoSticker posPegatina)
	{
		int posTextura = obtenerPosicionTexturaRectangulo(tipoEntidad, posEntidad, posPegatina);

		if (posTextura != -1 && cargadaTexturaEntidades[posTextura])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, verticesTexturaEntidades[posTextura], coordTexturaRectangulo, nombreTexturaEntidades[posTextura]);
		}
	}

	/* Métodos de Pintura de Fondo */

	protected void seleccionarTexturaFondo(int... indiceTexturas)
	{		
		if (tipoFondo != TTipoFondoRenderer.Nada)
		{
			if (indiceTexturas.length > 0)
			{
				if (tipoFondo == TTipoFondoRenderer.Fijo)
				{
					indiceTexturaFondo[0] = indiceTexturas[0];
					dibujarTexturaFondo[0] = true;
				}	
				else
				{
					if (numFondos > indiceTexturas.length)
					{
						numFondos = indiceTexturas.length;
					}
					
					for (int i = 0; i < indiceTexturas.length; i++)
					{
						indiceTexturaFondo[i] = indiceTexturas[i];
					}
				}
			}
		}
	}
	
	private void posicionarTexturaFondoDesplazable()
	{
		for (int i = 0; i < numFondos - 1; i++)
		{
			posicionTexturaFondo[i] = i * screenWidth;
			dibujarTexturaFondo[i] = true;	
		}

		posicionTexturaFondo[numFondos - 1] = GamePreferences.NUM_ITERATION_BACKGROUND() * screenWidth;
		dibujarTexturaFondo[numFondos - 1] = false;
	}
	
	private void posicionarTexturaFondoIntercambiable()
	{
		for (int i = 0; i < numFondos; i++)
		{
			posicionTexturaFondo[i] = 0.0f;
			dibujarTexturaFondo[i] = false;
		}
		
		dibujarTexturaFondo[0] = true;
	}

	private void cargarTexturaFondo(GL10 gl)
	{
		if (tipoFondo != TTipoFondoRenderer.Nada)
		{
			for (int i = 0; i < numFondos; i++)
			{
				if (indiceTexturaFondo[i] != -1)
				{
					cargarTextura(gl, indiceTexturaFondo[i], nombreTexturaFondo, cargadaTexturaFondo, i);
				}
			}
	
			if (!fondosCargados)
			{			
				if (tipoFondo == TTipoFondoRenderer.Desplazable)
				{
					posicionarTexturaFondoDesplazable();
				}
				else
				{
					posicionarTexturaFondoIntercambiable();
				}
				
				fondosCargados = true;
			}
		}
	}
	
	private void descargarTexturaFondo()
	{
		if (tipoFondo != TTipoFondoRenderer.Nada)
		{
			for (int i = 0; i < numFondos; i++)
			{
				descargarTextura(cargadaTexturaFondo, i);
			}
		}
	}

	private void dibujarFondo(GL10 gl)
	{
		if (tipoFondo != TTipoFondoRenderer.Nada)
		{
			for (int i = 0; i < numFondos; i++)
			{
				if (cargadaTexturaFondo[i])
				{
					if (dibujarTexturaFondo[i])
					{
						gl.glPushMatrix();
			
							gl.glTranslatef(posicionTexturaFondo[i], 0.0f, GamePreferences.DEEP_BACKGROUND);
				
							OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, verticesTexturaFondo[i], coordTexturaRectangulo, nombreTexturaFondo[i]);
			
						gl.glPopMatrix();
					}
				}
			}
		}
	}

	private void actualizarTexturaFondo()
	{
		if (tipoFondo != TTipoFondoRenderer.Nada)
		{
			VertexArray vertices = new VertexArray();
			vertices.addVertex(xLeft, yBottom);
			vertices.addVertex(xLeft, yTop);
			vertices.addVertex(xRight, yBottom);
			vertices.addVertex(xRight, yTop);
	
			for (int i = 0; i < numFondos; i++)
			{
				if (cargadaTexturaFondo[i])
				{
					verticesTexturaFondo[i] = BufferManager.construirBufferListaPuntos(vertices);
				}
			}
		}
	}
	
	private void animarTexturaFondoIntercambiable()
	{
		if (fondoActual < numFondos - 1)
		{
			for (int i = 0; i < numFondos; i++)
			{
				dibujarTexturaFondo[i] = false;
			}
			
			fondoActual++;
			dibujarTexturaFondo[fondoActual] = true;
			fondoFinalFijado = fondoActual == numFondos - 1;
		}
	}

	private void animarTexturaFondoDesplazable()
	{
		int lastFondo = numFondos - 1;

		// Activado de Último Fondo
		if (posicionTexturaFondo[lastFondo] >= screenWidth - GamePreferences.DIST_MOVIMIENTO_BACKGROUND() && posicionTexturaFondo[lastFondo] <= screenWidth + GamePreferences.DIST_MOVIMIENTO_BACKGROUND())
		{
			dibujarTexturaFondo[lastFondo] = true;
			
			for (int i = 0; i < numFondos - 1; i++)
			{
				if (posicionTexturaFondo[i] > screenWidth)
				{
					dibujarTexturaFondo[i] = false;
				}
			}
		}
		
		if (posicionTexturaFondo[lastFondo] <= 0.0f)
		{
			fondoFinalFijado = true;

			for (int i = 0; i < numFondos - 1; i++)
			{
				dibujarTexturaFondo[i] = false;
			}
		}

		// Desplazamiento
		for (int i = 0; i < numFondos; i++)
		{
			posicionTexturaFondo[i] -= GamePreferences.DIST_MOVIMIENTO_BACKGROUND();
		}

		// Reinicio de Fondo
		if (posicionTexturaFondo[lastFondo] > screenWidth)
		{
			for (int i = 0; i < numFondos - 1; i++)
			{
				if (posicionTexturaFondo[i] <= -screenWidth)
				{
					posicionTexturaFondo[i] = (numFondos - 2) * screenWidth;
				}
			}
		}
	}
	
	protected void animarFondo()
	{
		if (tipoFondo == TTipoFondoRenderer.Desplazable)
		{
			animarTexturaFondoDesplazable();
		}
		else if (tipoFondo == TTipoFondoRenderer.Intercambiable)
		{
			animarTexturaFondoIntercambiable();
		}
	}
	
	protected boolean isFondoFinal()
	{
		return fondoFinalFijado;
	}
	
	/* Métodos de Guardado de Información */
	
	protected BackgroundDataSaved backgroundSaveData()
	{		
		descargarTexturaFondo();
		
		return new BackgroundDataSaved(indiceTexturaFondo, posicionTexturaFondo, dibujarTexturaFondo);
	}

	protected void backgroundRestoreData(BackgroundDataSaved data)
	{		
		indiceTexturaFondo = data.getIndiceTexturaFondo();
		posicionTexturaFondo = data.getPosFondo();
		dibujarTexturaFondo = data.getDibujarFondo();
		fondosCargados = true;
	}
}
