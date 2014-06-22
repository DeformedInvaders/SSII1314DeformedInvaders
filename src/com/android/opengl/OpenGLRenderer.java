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

import com.creation.data.BitmapImage;
import com.creation.data.TTypeSticker;
import com.game.data.TTypeEntity;
import com.lib.buffer.Dimensions;
import com.lib.buffer.VertexArray;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;
import com.main.model.GamePreferences;

public abstract class OpenGLRenderer implements Renderer
{
	// Parámetros de la Cámara
	private float xLeft, xRight, yTop, yBottom, xCenter, yCenter;

	// Copia Seguridad de la Cámara
	private boolean cameraSaved;
	private float lastXLeft, lastXRight, lastYTop, lastYBot, lastXCenter, lastYCenter;

	// Parámetros del Puerto de Vista
	private int screenHeight, screenWidth;

	// Parámetros de la Escena
	private int backgroundColor;

	// Parámetros de Texturas
	private TTypeBackgroundRenderer backgroundType;
	private TTypeTexturesRenderer textureType;
	private FloatBuffer coordTexturaRectangulo;
	
	private int numBackgrounds, numCharacters, numTextures;
	
	// Texturas Personaje
	private int POS_TEXTURE_CHARACTER_SKELETON, POS_TEXTURE_CHARACTER_STICKER, POS_TEXTURE_CHARACTER_SHIELD, POS_TEXTURE_CHARACTER_PLATFORM;
	
	// Texturas Video
	private int POS_TEXTURE_ANIMATED_OBJECT, POS_TEXTURE_INANIMATED_OBJECT;
	
	// Texturas Juego
	private int POS_TEXTURE_BOSS_SKELETON, POS_TEXTURE_BOSS_STICKER;
	private int POS_TEXTURE_BOSS_SHIELD, POS_TEXTURE_BOSS_PLATFORM;
	private int POS_TEXTURE_MISSILE, POS_TEXTURE_OBSTACLE;
	private int POS_TEXTURE_ENEMY_SKELETON, POS_TEXTURE_ENEMY_STICKER;
	private int POS_TEXTURE_CHARACTER_SHOT, POS_TEXTURE_BOSS_SHOT;
	private int POS_TEXTURE_CHARACTER_WEAPON, POS_TEXTURE_BOSS_WEAPON;

	// Entities
	private int[] textureName;
	private boolean[] textureLoaded;
	private FloatBuffer[] textureVertex;
	
	// Backgrounds
	private int[] backgroundName, backgroundId;
	private float[] backgroundPosition;
	private boolean[] backgroundEnabled, backgroundLoaded;
	private FloatBuffer[] backgroundVertex;
	
	private int backgroundActual;
	private boolean backgroundSelected, backgroundEnded;

	// Marco
	protected float frameWidthMiddle, frameHeightSide, frameWidthSide;
	private FloatBuffer recFrameSide, recMarcoFrontal, recMarcoInterior;

	// Contexto
	protected Context mContext;

	/* Constructoras */
	
	public OpenGLRenderer(Context context, TTypeBackgroundRenderer background, TTypeTexturesRenderer texture)
	{
		this(context, background, texture, Color.argb(0, 0, 0, 0));
	}

	public OpenGLRenderer(Context context, TTypeBackgroundRenderer background, TTypeTexturesRenderer texture, int color)
	{
		mContext = context;
		
		backgroundColor = color;
		backgroundType = background;
		numBackgrounds = backgroundType.getNumBackgrounds();
		textureType = texture;
		numCharacters = textureType.getNumCharacters();
		numTextures = textureType.getNumTextures();

		// Marcos
		updateFrame();

		// Fondos
		if (numBackgrounds > 0)
		{
			backgroundName = new int[numBackgrounds];
			backgroundId = new int[numBackgrounds];
			backgroundPosition = new float[numBackgrounds];
			backgroundEnabled = new boolean[numBackgrounds];
			backgroundLoaded = new boolean[numBackgrounds];
			backgroundVertex = new FloatBuffer[numBackgrounds];
	
			backgroundSelected = false;
			backgroundEnded = false;
			backgroundActual = 0;
	
			for (int i = 0; i < numBackgrounds; i++)
			{
				backgroundId[i] = -1;
			}
		}

		// Textura
		if (numTextures > 0)
		{
			textureName = new int[numTextures];
			textureLoaded = new boolean[numTextures];
			textureVertex = new FloatBuffer[numTextures];
	
			coordTexturaRectangulo = BufferManager.construirBufferTextura();
		}
		
		// Personaje
		POS_TEXTURE_CHARACTER_SKELETON = 0;
		POS_TEXTURE_CHARACTER_STICKER = POS_TEXTURE_CHARACTER_SKELETON + numCharacters;
		
		// Video
		POS_TEXTURE_INANIMATED_OBJECT = POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * numCharacters);
		POS_TEXTURE_ANIMATED_OBJECT = POS_TEXTURE_INANIMATED_OBJECT + GamePreferences.NUM_TYPE_INANIMATED_OBJECTS;
		
		// Juego
		POS_TEXTURE_CHARACTER_SHIELD = POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * numCharacters);
		POS_TEXTURE_CHARACTER_PLATFORM = POS_TEXTURE_CHARACTER_SHIELD + GamePreferences.NUM_TYPE_SHIELD;
		
		POS_TEXTURE_BOSS_SKELETON = POS_TEXTURE_CHARACTER_PLATFORM + GamePreferences.NUM_TYPE_PLATFORMS;
		POS_TEXTURE_BOSS_STICKER = POS_TEXTURE_BOSS_SKELETON + 1;
		
		POS_TEXTURE_BOSS_SHIELD = POS_TEXTURE_BOSS_STICKER + GamePreferences.NUM_TYPE_STICKERS;
		POS_TEXTURE_BOSS_PLATFORM = POS_TEXTURE_BOSS_SHIELD + GamePreferences.NUM_TYPE_SHIELD;
				
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
	
	protected boolean onReset()
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
		gl.glClearColor(Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor), Color.alpha(backgroundColor));

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
		xCenter = (xRight + xLeft) / 2.0f;
		yCenter = (yTop + yBottom) / 2.0f;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBottom, yTop);

		// Copia de Seguridad de la Cámara
		cameraSaved = false;

		// Marco
		updateFrame();
		
		// Fondo
		loadBackground(gl);

		updateTextureBackground();

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
		gl.glClearColor(Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor), Color.alpha(backgroundColor));
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// Activar Matriz de ModeladoVista
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Background
		drawBackground(gl);
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

		xRight = xCenter + newAncho / 2.0f;
		xLeft = xCenter - newAncho / 2.0f;
		yTop = yCenter + newAlto / 2.0f;
		yBottom = yCenter - newAlto / 2.0f;

		updateFrame();
	}

	private void camaraDrag(float dWorldX, float dWorldY)
	{
		xLeft += dWorldX;
		xRight += dWorldX;
		yBottom += dWorldY;
		yTop += dWorldY;

		xCenter = (xRight + xLeft) / 2.0f;
		yCenter = (yTop + yBottom) / 2.0f;

		updateFrame();
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

		xCenter = (xRight + xLeft) / 2.0f;
		yCenter = (yTop + yBottom) / 2.0f;

		updateFrame();
	}

	/* Métodos de modificación de puntos */

	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight) { }

	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight) { }

	public void pointsRestore() { }
	
	/* Métodos de Copia de Seguridad de la Cámara */

	public void saveCamera()
	{
		lastXLeft = xLeft;
		lastXRight = xRight;
		lastYTop = yTop;
		lastYBot = yBottom;
		lastXCenter = xCenter;
		lastYCenter = yCenter;

		cameraSaved = true;
	}

	public void restoreCamera()
	{
		if (cameraSaved)
		{
			xLeft = lastXLeft;
			xRight = lastXRight;
			yTop = lastYTop;
			yBottom = lastYBot;
			xCenter = lastXCenter;
			yCenter = lastYCenter;
		}
	}

	/* Métodos de Captura de Pantalla y Marcos */

	private void updateFrame()
	{
		float camaraHeight = yTop - yBottom;
		float camaraWidth = xRight - xLeft;
		
		frameHeightSide = GamePreferences.FRAME_HEIGHT_SIDE(camaraWidth, camaraHeight);
		frameWidthMiddle = GamePreferences.FRAME_WIDTH_MIDDLE(camaraWidth, camaraHeight);
		frameWidthSide = GamePreferences.FRAME_WIDTH_SIDE(camaraWidth, camaraHeight);

		float[] recA = { 0, 0, 0, frameWidthMiddle, frameWidthSide, 0, frameWidthSide, frameWidthMiddle };
		recFrameSide = BufferManager.construirBufferListaPuntos(recA);

		float[] recB = { 0, 0, 0, frameHeightSide, camaraWidth, 0, camaraWidth, frameHeightSide };
		recMarcoFrontal = BufferManager.construirBufferListaPuntos(recB);
		
		float[] recC = { 0, 0, 0, frameWidthMiddle, frameWidthMiddle, 0, frameWidthMiddle, frameWidthMiddle };
		recMarcoInterior = BufferManager.construirBufferListaPuntos(recC);
	}
	
	protected boolean isPointOutsideFrame(float x, float y)
	{
		return (x < xLeft || x > xLeft + frameWidthMiddle || y < yBottom || y > yBottom + frameWidthMiddle);
	}

	protected void drawInsideFrameBegin(GL10 gl)
	{
		gl.glTranslatef(frameWidthSide, frameHeightSide, 0.0f);
	}

	protected void drawInsideFrameEnd(GL10 gl)
	{
		gl.glTranslatef(-frameWidthSide, -frameHeightSide, 0.0f);
	}

	protected void drawFrameHull(GL10 gl, int color, float deep)
	{
		drawFrameFrontal(gl, color, deep);
		drawFrameSide(gl, color, deep);
	}
	
	protected void drawFrameFill(GL10 gl, int color, float deep)
	{
		drawFrameFrontal(gl, color, deep);
		drawFrameInside(gl, color, deep);
		drawFrameSide(gl, color, deep);
	}

	protected void drawFrameInside(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(frameWidthSide, frameHeightSide, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoInterior);
	
			gl.glPopMatrix();
	
		gl.glPopMatrix();
	}
	
	private void drawFrameSide(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
			gl.glPushMatrix();
	
				gl.glTranslatef(0, frameHeightSide, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recFrameSide);
		
				gl.glTranslatef(frameWidthSide + frameWidthMiddle, 0, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recFrameSide);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	private void drawFrameFrontal(GL10 gl, int color, float deep)
	{
		gl.glPushMatrix();

			gl.glTranslatef(xLeft, yBottom, deep);
	
			gl.glPushMatrix();
			
			OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoFrontal);
	
				gl.glTranslatef(0, frameHeightSide + frameWidthMiddle, 0);
				OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLE_STRIP, 0, color, recMarcoFrontal);
	
			gl.glPopMatrix();

		gl.glPopMatrix();
	}

	protected BitmapImage getScreenshot(GL10 gl)
	{
		return OpenGLManager.capturaPantalla(gl, (int) frameWidthSide, (int) frameHeightSide, (int) frameWidthMiddle, (int) frameWidthMiddle);
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
		return worldX - frameWidthSide;
	}

	protected float convertWorldYToFrameYCoordinate(float worldY)
	{
		return worldY - frameHeightSide;
	}

	protected float convertFrameXToWorldXCoordinate(float frameX)
	{
		return frameX + frameWidthSide;
	}

	protected float convertFrameYToWorldYCoordinate(float frameY)
	{
		return frameY + frameHeightSide;
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
	
	private int positionTextureMesh(TTypeEntity entity, int position)
	{
		switch (entity)
		{
			case Character:
				return POS_TEXTURE_CHARACTER_SKELETON + position;
			case Enemy:
				return POS_TEXTURE_ENEMY_SKELETON + position;
			case Boss:
				return POS_TEXTURE_BOSS_SKELETON;
			default:
				return -1;
		}
	}

	private int positionTextureRectangle(TTypeEntity entity, int position, TTypeSticker sticker)
	{
		switch (entity)
		{
			case Character:
				return POS_TEXTURE_CHARACTER_STICKER + (GamePreferences.NUM_TYPE_STICKERS * position) + sticker.ordinal();
			case Boss:
				return POS_TEXTURE_BOSS_STICKER + sticker.ordinal();
			case Enemy:
				return POS_TEXTURE_ENEMY_STICKER + (GamePreferences.NUM_TYPE_STICKERS * position) + sticker.ordinal();
			case Obstacle:
				return POS_TEXTURE_OBSTACLE + position;
			case Missil:
				return POS_TEXTURE_MISSILE + position;
			case CharacterShield:
				return POS_TEXTURE_CHARACTER_SHIELD + position;
			case BossShield:
				return POS_TEXTURE_BOSS_SHIELD + position;
			case CharacterPlatform:
				return POS_TEXTURE_CHARACTER_PLATFORM + position;
			case BossPlatform:
				return POS_TEXTURE_BOSS_PLATFORM + position;
			case CharacterShot:
				return POS_TEXTURE_CHARACTER_SHOT + position;
			case BossShot:
				return POS_TEXTURE_BOSS_SHOT + position;
			case CharacterWeapon:
				return POS_TEXTURE_CHARACTER_WEAPON + position;
			case BossWeapon:
				return POS_TEXTURE_BOSS_WEAPON + position;
			case InanimatedObject:
				return POS_TEXTURE_INANIMATED_OBJECT + position;
			case AnimatedObject:
				return POS_TEXTURE_ANIMATED_OBJECT + position;
			default:
				return -1;
		}
	}

	// Métodos de Contrucción de Textura

	protected VertexArray buildTexture(VertexArray vertices, float textureWidth, float textureHeight)
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

	private void loadTexture(GL10 gl, Bitmap textura, int[] textureName, boolean[] textureLoaded, int texturePosition)
	{
		OpenGLManager.cargarTextura(gl, textura, textureName, texturePosition);

		textureLoaded[texturePosition] = true;
	}

	private void loadTexture(GL10 gl, int textureId, int[] textureName, boolean[] textureLoaded, int texturePosition)
	{
		Bitmap textura = BitmapFactory.decodeResource(mContext.getResources(), textureId);

		loadTexture(gl, textura, textureName, textureLoaded, texturePosition);
	}

	private void deleteTexture(boolean[] textureLoaded, int texturePosition)
	{
		textureLoaded[texturePosition] = false;
	}

	// Métodos de Contrucción de Textura para Entidades

	public void loadTextureMesh(GL10 gl, Bitmap texture, TTypeEntity entity, int position)
	{
		int texturePosition = positionTextureMesh(entity, position);

		if (texturePosition != -1 && !textureLoaded[texturePosition])
		{
			loadTexture(gl, texture, textureName, textureLoaded, texturePosition);
		}
	}

	public void deleteTextureMesh(TTypeEntity entity, int position)
	{
		int texturePosition = positionTextureMesh(entity, position);

		if (texturePosition != -1)
		{
			deleteTexture(textureLoaded, texturePosition);
		}
	}
	
	private Dimensions loadTextureRectangle(GL10 gl, Bitmap bitmap, float textureHeight, float textureWidth, int textureId, TTypeEntity entity, int position, TTypeSticker sticker)
	{
		int texturePosition = positionTextureRectangle(entity, position, sticker);

		if (texturePosition != -1 && !textureLoaded[texturePosition])
		{			
			loadTexture(gl, bitmap, textureName, textureLoaded, texturePosition);
			bitmap.recycle();

			VertexArray vertices = new VertexArray();
			
			if(entity == TTypeEntity.Character || entity == TTypeEntity.Enemy || entity == TTypeEntity.Boss)
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
			
			textureVertex[texturePosition] = BufferManager.construirBufferListaPuntos(vertices);

			return new Dimensions(textureHeight, textureWidth);
		}

		return null;
	}

	public Dimensions loadTextureRectangle(GL10 gl, int textureId, TTypeEntity entity, int position, TTypeSticker sticker)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), textureId);

		float textureHeight = bitmap.getHeight();
		float textureWidth = bitmap.getWidth();
		
		return loadTextureRectangle(gl, bitmap, textureHeight, textureWidth, textureId, entity, position, sticker);
	}
	
	public Dimensions loadTextureRectangle(GL10 gl, float textureHeight, float textureWidth, int textureId, TTypeEntity entity, int position, TTypeSticker sticker)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), textureId);
		
		return loadTextureRectangle(gl, bitmap, textureHeight, textureWidth, textureId, entity, position, sticker);
	}

	public void deleteTextureRectangle(TTypeEntity entity, int position, TTypeSticker posPegatina)
	{
		int texturePosition = positionTextureRectangle(entity, position, posPegatina);

		if (texturePosition != -1)
		{
			deleteTexture(textureLoaded, texturePosition);
		}
	}

	// Métodos de Pintura de Texturas para Entidades

	public void drawTextureMesh(GL10 gl, FloatBuffer bufferTextureVertex, FloatBuffer bufferTextureCoord, TTypeEntity entity, int position)
	{
		int texturePosition = positionTextureMesh(entity, position);

		if (texturePosition != -1 && textureLoaded[texturePosition])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLES, bufferTextureVertex, bufferTextureCoord, textureName[texturePosition]);
		}
	}

	public void drawTextureRectangle(GL10 gl, TTypeEntity entity, int position, TTypeSticker sticker)
	{
		int texturePosition = positionTextureRectangle(entity, position, sticker);

		if (texturePosition != -1 && textureLoaded[texturePosition])
		{
			OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, textureVertex[texturePosition], coordTexturaRectangulo, textureName[texturePosition]);
		}
	}

	/* Métodos de Pintura de Fondo */

	protected void selectBackground(int... index)
	{		
		if (backgroundType != TTypeBackgroundRenderer.Blank)
		{
			if (index.length > 0)
			{
				if (backgroundType == TTypeBackgroundRenderer.Static)
				{
					backgroundId[0] = index[0];
					backgroundEnabled[0] = true;
				}	
				else
				{
					if (numBackgrounds > index.length)
					{
						numBackgrounds = index.length;
					}
					
					for (int i = 0; i < index.length; i++)
					{
						backgroundId[i] = index[i];
					}
				}
			}
		}
	}
	
	private void loadBackgroundMovable()
	{
		for (int i = 0; i < numBackgrounds - 1; i++)
		{
			backgroundPosition[i] = i * screenWidth;
			backgroundEnabled[i] = true;	
		}

		backgroundPosition[numBackgrounds - 1] = GamePreferences.NUM_ITERATION_BACKGROUND() * screenWidth;
		backgroundEnabled[numBackgrounds - 1] = false;
	}
	
	private void loadBackgroundSwappable()
	{
		for (int i = 0; i < numBackgrounds; i++)
		{
			backgroundPosition[i] = 0.0f;
			backgroundEnabled[i] = false;
		}
		
		backgroundEnabled[0] = true;
	}

	private void loadBackground(GL10 gl)
	{
		if (backgroundType != TTypeBackgroundRenderer.Blank)
		{
			for (int i = 0; i < numBackgrounds; i++)
			{
				if (backgroundId[i] != -1)
				{
					loadTexture(gl, backgroundId[i], backgroundName, backgroundLoaded, i);
				}
			}
	
			if (!backgroundSelected)
			{			
				if (backgroundType == TTypeBackgroundRenderer.Movable)
				{
					loadBackgroundMovable();
				}
				else
				{
					loadBackgroundSwappable();
				}
				
				backgroundSelected = true;
			}
		}
	}
	
	private void deleteBackground()
	{
		if (backgroundType != TTypeBackgroundRenderer.Blank)
		{
			for (int i = 0; i < numBackgrounds; i++)
			{
				deleteTexture(backgroundLoaded, i);
			}
		}
	}

	private void drawBackground(GL10 gl)
	{
		if (backgroundType != TTypeBackgroundRenderer.Blank)
		{
			for (int i = 0; i < numBackgrounds; i++)
			{
				if (backgroundLoaded[i])
				{
					if (backgroundEnabled[i])
					{
						gl.glPushMatrix();
			
							gl.glTranslatef(backgroundPosition[i], 0.0f, GamePreferences.DEEP_BACKGROUND);
				
							OpenGLManager.dibujarTextura(gl, GL10.GL_TRIANGLE_STRIP, backgroundVertex[i], coordTexturaRectangulo, backgroundName[i]);
			
						gl.glPopMatrix();
					}
				}
			}
		}
	}

	private void updateTextureBackground()
	{
		if (backgroundType != TTypeBackgroundRenderer.Blank)
		{
			VertexArray vertices = new VertexArray();
			vertices.addVertex(xLeft, yBottom);
			vertices.addVertex(xLeft, yTop);
			vertices.addVertex(xRight, yBottom);
			vertices.addVertex(xRight, yTop);
	
			for (int i = 0; i < numBackgrounds; i++)
			{
				if (backgroundLoaded[i])
				{
					backgroundVertex[i] = BufferManager.construirBufferListaPuntos(vertices);
				}
			}
		}
	}
	
	private void moveBackgroundSwappable()
	{
		if (backgroundActual < numBackgrounds - 1)
		{
			for (int i = 0; i < numBackgrounds; i++)
			{
				backgroundEnabled[i] = false;
			}
			
			backgroundActual++;
			backgroundEnabled[backgroundActual] = true;
			backgroundEnded = backgroundActual == numBackgrounds - 1;
		}
	}

	private void moveBackgroundMovable()
	{
		int lastBackground = numBackgrounds - 1;

		// Activado de Último Fondo
		if (backgroundPosition[lastBackground] >= screenWidth - GamePreferences.DIST_MOVIMIENTO_BACKGROUND() && backgroundPosition[lastBackground] <= screenWidth + GamePreferences.DIST_MOVIMIENTO_BACKGROUND())
		{
			backgroundEnabled[lastBackground] = true;
			
			for (int i = 0; i < numBackgrounds - 1; i++)
			{
				if (backgroundPosition[i] > screenWidth)
				{
					backgroundEnabled[i] = false;
				}
			}
		}
		
		if (backgroundPosition[lastBackground] <= 0.0f)
		{
			backgroundEnded = true;

			for (int i = 0; i < numBackgrounds - 1; i++)
			{
				backgroundEnabled[i] = false;
			}
		}

		// Desplazamiento
		for (int i = 0; i < numBackgrounds; i++)
		{
			backgroundPosition[i] -= GamePreferences.DIST_MOVIMIENTO_BACKGROUND();
		}

		// Reinicio de Fondo
		if (backgroundPosition[lastBackground] > screenWidth)
		{
			for (int i = 0; i < numBackgrounds - 1; i++)
			{
				if (backgroundPosition[i] <= -screenWidth)
				{
					backgroundPosition[i] = (numBackgrounds - 2) * screenWidth;
				}
			}
		}
	}
	
	protected void moveBackground()
	{
		if (backgroundType == TTypeBackgroundRenderer.Movable)
		{
			moveBackgroundMovable();
		}
		else if (backgroundType == TTypeBackgroundRenderer.Swappable)
		{
			moveBackgroundSwappable();
		}
	}
	
	protected boolean isBackgroundEnded()
	{
		return backgroundEnded;
	}
	
	/* Métodos de Guardado de Información */
	
	protected BackgroundDataSaved backgroundSaveData()
	{		
		deleteBackground();
		
		return new BackgroundDataSaved(backgroundId, backgroundPosition, backgroundEnabled);
	}

	protected void backgroundRestoreData(BackgroundDataSaved data)
	{		
		backgroundId = data.getBackgroundId();
		backgroundPosition = data.getBackgroundPosition();
		backgroundEnabled = data.getBackgroundEnabled();
		backgroundSelected = true;
	}
}
