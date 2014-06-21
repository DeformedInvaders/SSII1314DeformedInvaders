package com.creation.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.character.display.TStateScreenshot;
import com.creation.data.BitmapImage;
import com.creation.data.Stickers;
import com.creation.data.Polyline;
import com.creation.data.TTypeSticker;
import com.creation.data.Texture;
import com.game.data.Character;
import com.game.data.TTypeEntity;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;
import com.lib.search.TriangleQuadTreeSearcher;
import com.main.model.GamePreferences;

public class PaintOpenGLRenderer extends OpenGLRenderer
{	
	// Estructura de Datos
	private TStatePaint mState;

	private int paletteColor;
	private TTypeSize polylineSize;
	private int stickerActual;
	private TTypeSticker stickerActualType;

	// Detalles
	private List<Polyline> polylineList;
	private VertexArray polylineActual;
	private FloatBuffer bufferPolyline;

	// Pegatinas
	private Stickers stickers;
	private boolean stickerAdded;

	// Esqueleto
	private HullArray hull;
	private FloatBuffer bufferHull;

	private VertexArray vertices;
	private FloatBuffer bufferVertices;

	private TriangleArray triangles;

	private int backgroundColor;
	
	// Buscador de Triángulos
	private TriangleQuadTreeSearcher searcher;

	// Texturas
	private TStateScreenshot mScreenshotState;

	private BitmapImage texture;
	private VertexArray textureCoords;

	// Anterior Siguiente Buffers
	private Stack<Action> prevBuffer;
	private Stack<Action> nextBuffer;

	/* Constructora */
	
	public PaintOpenGLRenderer(Context context, int color, Character character)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character, color);

		mState = TStatePaint.Nothing;

		hull = character.getSkeleton().getHull();
		vertices = character.getSkeleton().getVertices();
		triangles = character.getSkeleton().getTriangles();

		bufferVertices = BufferManager.construirBufferListaTriangulosRellenos(triangles, vertices);
		bufferHull = BufferManager.construirBufferListaIndicePuntos(hull, vertices);
		
		if (character.isTextureReady())
		{
			stickers = character.getTexture().getStickers();
		}
		else
		{
			stickers = new Stickers();
		}
		
		stickerActual = 0;
		stickerAdded = false;
		
		polylineList = new ArrayList<Polyline>();
		polylineActual = null;

		backgroundColor = Color.WHITE;

		paletteColor = Color.RED;
		polylineSize = TTypeSize.Small;

		prevBuffer = new Stack<Action>();
		nextBuffer = new Stack<Action>();

		mScreenshotState = TStateScreenshot.Nothing;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		if (mState == TStatePaint.Screenshot && mScreenshotState == TStateScreenshot.Capturing)
		{
			// Guardar posición actual de la Cámara
			saveCamera();

			// Restaurar Cámara posición inicial
			camaraRestore();

			drawSkeleton(gl);

			// Capturar Pantalla
			texture = getScreenshot(gl);

			// Construir Textura
			textureCoords = buildTexture(vertices, texture.getWidth(), texture.getHeight());

			// Restaurar posición anterior de la Cámara
			restoreCamera();
		}

		// Cargar Pegatinas
		stickers.loadTexture(gl, this, mContext, TTypeEntity.Character, 0);

		drawSkeleton(gl);
		
		if (mState == TStatePaint.Screenshot && mScreenshotState == TStateScreenshot.Capturing)
		{
			// Desactivar Modo Captura
			mScreenshotState = TStateScreenshot.Finished;
		}
	}

	private void drawSkeleton(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		drawInsideFrameBegin(gl);

		// Esqueleto
		OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLES, GamePreferences.SIZE_LINE, backgroundColor, bufferVertices);

		gl.glPushMatrix();
		
			gl.glTranslatef(0.0f, 0.0f, GamePreferences.DEEP_POLYLINES);
			
			// Detalles
			if (polylineActual != null)
			{
				synchronized (polylineActual)
				{
					OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, polylineSize.getSize(), paletteColor, bufferPolyline);
				}
			}
			
			synchronized (polylineList)
			{
				Iterator<Polyline> it = polylineList.iterator();
				while (it.hasNext())
				{
					Polyline polilinea = it.next();
					OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize().getSize(), polilinea.getColor(), polilinea.getBuffer());
				}
			}

		gl.glPopMatrix();
			
		if (mState != TStatePaint.Screenshot)
		{
			// Contorno
			OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferHull);

			// Dibujar Pegatinas
			stickers.drawTexture(gl, this, vertices, triangles, TTypeEntity.Character, 0);
		}

		// Centrado de Marco
		drawInsideFrameEnd(gl);
	}
	
	/* Métodos Abstráctos OpenGLRenderer */

	@Override
	protected boolean onReset()
	{
		polylineActual = null;
		polylineList.clear();

		stickers.deleteTexture(this, TTypeEntity.Character, 0);
		stickers.resetSticker();
		
		stickerActual = 0;
		stickerAdded = false;

		prevBuffer.clear();
		nextBuffer.clear();

		mState = TStatePaint.Nothing;
		backgroundColor = Color.WHITE;
		polylineSize = TTypeSize.Small;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStatePaint.Pencil)
		{
			return addPoint(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (mState == TStatePaint.Bucket)
		{
			return changeBackgroundColor(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (mState == TStatePaint.AddSticker)
		{
			return addSticker(pixelX, pixelY, screenWidth, screenHeight);
		}

		return false;
	}

	private boolean addPoint(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (polylineActual == null)
		{
			polylineActual = new VertexArray();
		}

		boolean addPoint = true;

		if (polylineActual.getNumVertices() > 0)
		{
			float lastFrameX = polylineActual.getLastXVertex();
			float lastFrameY = polylineActual.getLastYVertex();

			float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
			float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

			addPoint = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > GamePreferences.MAX_DISTANCE_PIXELS;
		}

		if (addPoint)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			synchronized (polylineActual)
			{
				polylineActual.addVertex(frameX, frameY);
	
				bufferPolyline = BufferManager.construirBufferListaPuntos(polylineActual);
			}
			
			return true;
		}

		return false;
	}

	private boolean changeBackgroundColor(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

		if (GeometryUtils.isPointInsideMesh(hull, vertices, frameX, frameY))
		{
			if (paletteColor != backgroundColor)
			{
				backgroundColor = paletteColor;

				prevBuffer.push(new ActionColor(paletteColor));
				nextBuffer.clear();

				return true;
			}
		}

		return false;
	}

	private boolean addSticker(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
		
		short triangle = searcher.searchTriangle(frameX, frameY);
		if (triangle != -1)
		{			
			stickers.addSticker(stickerActualType, stickerActual, frameX, frameY, triangle, vertices, triangles);

			deleteTextureRectangle(TTypeEntity.Character, 0, stickerActualType);
			stickerAdded = true;

			prevBuffer.push(new ActionSticker(stickerActualType, stickerActual, frameX, frameY, triangle, stickers.getFactor(stickerActualType), stickers.getTheta(stickerActualType)));
			nextBuffer.clear();
			
			mState = TStatePaint.Nothing;
			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStatePaint.Pencil)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStatePaint.Pencil)
		{
			return savePolyline();
		}

		return false;
	}
	
	@Override
	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (mState == TStatePaint.EditSticker)
		{
			stickers.zoomSticker(stickerActualType, factor);
		}
	}

	@Override
	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (mState == TStatePaint.EditSticker)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);
			
			float dWorldX = frameX - lastFrameX;
			float dWorldY = frameY - lastFrameY;

			if (Math.abs(Intersector.distancePoints(0.0f, 0.0f, dWorldX, dWorldY)) > GamePreferences.MAX_DISTANCE_PIXELS)
			{
				float newStickerFrameX = stickers.getXCoords(stickerActualType, vertices, triangles) + dWorldX;
				float newtStickerFrameY = stickers.getYCoords(stickerActualType, vertices, triangles) + dWorldY;
				
				short triangle = searcher.searchTriangle(newStickerFrameX, newtStickerFrameY);
				if (triangle != -1)
				{			
					stickers.moveSticker(stickerActualType, newStickerFrameX, newtStickerFrameY, triangle, vertices, triangles);
				}
			}
		}
	}

	@Override
	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (mState == TStatePaint.EditSticker)
		{
			stickers.rotateSticker(stickerActualType, (float) Math.toDegrees(angRad));
		}
	}
	
	@Override
	public void pointsRestore()
	{
		if (mState == TStatePaint.EditSticker)
		{
			stickers.restoreSticker(stickerActualType);
		}
	}

	private boolean savePolyline()
	{
		if (polylineActual != null)
		{
			synchronized (polylineList)
			{
				Polyline polilinea = new Polyline(paletteColor, polylineSize, polylineActual, bufferPolyline);
	
				polylineList.add(polilinea);
				prevBuffer.push(new ActionPolyline(polilinea));
				nextBuffer.clear();
				polylineActual = null;
			}

			return true;
		}

		return false;
	}
	
	private boolean saveSticker()
	{
		if (mState == TStatePaint.EditSticker)
		{
			int id = stickers.getId(stickerActualType);
			float x = stickers.getXCoords(stickerActualType, vertices, triangles);
			float y = stickers.getYCoords(stickerActualType, vertices, triangles);
			short indice = stickers.getIndex(stickerActualType);
			float factor = stickers.getFactor(stickerActualType);
			float angulo = stickers.getTheta(stickerActualType);

			prevBuffer.push(new ActionSticker(stickerActualType, id, x, y, indice, factor, angulo));
			nextBuffer.clear();
			
			return true;
		}
		
		return false;
	}
	
	/* Métodos de Selección de Estado */

	public void selectNothing()
	{
		savePolyline();
		saveSticker();
		mState = TStatePaint.Nothing;
	}
	
	public void selectHand()
	{
		savePolyline();
		saveSticker();
		mState = TStatePaint.Hand;
	}

	public void selectPencil()
	{
		savePolyline();
		saveSticker();
		mState = TStatePaint.Pencil;
	}

	public void selectBucket()
	{
		savePolyline();
		saveSticker();
		mState = TStatePaint.Bucket;
	}

	public void selectColor(int color)
	{
		paletteColor = color;
	}

	public void selectSize(TTypeSize size)
	{
		polylineSize = size;
	}

	public void addSticker(int sticker, TTypeSticker tipo)
	{
		savePolyline();

		stickerActual = sticker;
		stickerActualType = tipo;
		mState = TStatePaint.AddSticker;
		
		if (searcher == null)
		{
			searcher = new TriangleQuadTreeSearcher(triangles, vertices, 0.0f, 0.0f, frameWidthMiddle, frameWidthMiddle);
		}
	}
	
	public void deleteSticker(TTypeSticker tipo)
	{
		savePolyline();
		
		if (stickers.isStickerLoaded(tipo))
		{
			deleteTextureRectangle(TTypeEntity.Character, 0, tipo);
			stickers.deleteSticker(tipo);
			
			prevBuffer.push(new ActionSticker(tipo));
			nextBuffer.clear();
		}
		
		mState = TStatePaint.Nothing;
	}
	
	public void editSticker(TTypeSticker tipo)
	{
		savePolyline();
		
		if (searcher == null)
		{
			searcher = new TriangleQuadTreeSearcher(triangles, vertices, 0.0f, 0.0f, frameWidthMiddle, frameWidthMiddle);
		}
		
		if (stickers.isStickerLoaded(tipo))
		{
			stickerActualType = tipo;
			mState = TStatePaint.EditSticker;
		}
		else
		{
			mState = TStatePaint.Nothing;
		}
	}

	public void seleccionarCaptura() 
	{
		savePolyline();

		mState = TStatePaint.Screenshot;
		mScreenshotState = TStateScreenshot.Capturing;
	}

	/* Métodos de modificación de Buffers de estado */

	public void prevAction()
	{
		savePolyline();

		if (!prevBuffer.isEmpty())
		{
			Action accion = prevBuffer.pop();
			nextBuffer.add(accion);
			updateState(prevBuffer);
		}
	}

	public void nextAction()
	{
		savePolyline();

		if (!nextBuffer.isEmpty())
		{
			Action accion = nextBuffer.lastElement();
			nextBuffer.remove(nextBuffer.size() - 1);
			prevBuffer.push(accion);
			updateState(prevBuffer);
		}
	}

	private void updateState(Stack<Action> pila)
	{
		backgroundColor = Color.WHITE;
		polylineList = new ArrayList<Polyline>();
		
		stickers.deleteTexture(this, TTypeEntity.Character, 0);
		stickers.resetSticker();

		Iterator<Action> it = pila.iterator();
		while (it.hasNext())
		{
			Action accion = it.next();
			if (accion.isTypeColor())
			{
				updateState((ActionColor) accion);
			}
			else if (accion.isTypePolyline())
			{
				updateState((ActionPolyline) accion);
			}
			else if (accion.isTypeSticker())
			{
				updateState((ActionSticker) accion);
			}
		}
	}
	
	private void updateState(ActionColor accion)
	{
		backgroundColor = accion.getBackgroundColor();
	}
	
	private void updateState(ActionPolyline accion)
	{
		polylineList.add(accion.getPolyline());
	}
	
	private void updateState(ActionSticker accion)
	{
		stickers.addSticker(accion.getStickerType(), accion.getStickerId(), accion.getStickerCoordX(), accion.getStickerCoordY(), accion.getStickerIndex(), accion.getStickerFactor(), accion.getStickerRotation(), vertices, triangles);
	}

	/* Métodos de Obtención de Información */

	public boolean isNextBufferEmpty()
	{
		return nextBuffer.isEmpty();
	}

	public boolean isPrevBufferEmpty()
	{
		return prevBuffer.isEmpty();
	}

	public boolean isStickerAdded()
	{
		if (stickerAdded)
		{
			stickerAdded = false;
			mState = TStatePaint.Nothing;

			return true;
		}

		return false;
	}

	public boolean isStatePencil()
	{
		return mState == TStatePaint.Pencil;
	}

	public boolean isStateBucket()
	{
		return mState == TStatePaint.Bucket;
	}

	public boolean isStateHand()
	{
		return mState == TStatePaint.Hand;
	}

	public boolean isStateSticker()
	{
		return mState == TStatePaint.AddSticker || mState == TStatePaint.EditSticker;
	}
	
	public Texture getTexture()
	{
		if (mState == TStatePaint.Screenshot && mScreenshotState == TStateScreenshot.Capturing)
		{
			while (mScreenshotState != TStateScreenshot.Finished);

			mState = TStatePaint.Nothing;
			mScreenshotState = TStateScreenshot.Nothing;
			
			return new Texture(texture, textureCoords, stickers);
		}
		
		return null;
	}

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
		// Pegatinas
		stickers.deleteTexture(this, TTypeEntity.Character, 0);
				
		return new PaintDataSaved(prevBuffer, nextBuffer, mState);
	}

	public void restoreData(PaintDataSaved data)
	{
		mState = data.getState();
		prevBuffer = data.getPrevBuffer();
		nextBuffer = data.getNextBuffer();

		updateState(prevBuffer);
	}
}
