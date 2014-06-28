package com.creation.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.Handle;
import com.creation.data.Stickers;
import com.creation.data.TTypeMovement;
import com.creation.data.Texture;
import com.game.data.Character;
import com.game.data.TTypeEntity;
import com.lib.buffer.HandleArray;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;
import com.lib.search.TriangleQuadTreeSearcher;
import com.main.model.GamePreferences;
import com.project.main.R;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;
	
	private OnDeformListener mListener;

	// Modo Grabado
	private TStateDeform mState;
	private boolean recordActive;

	/* Movimientos */

	// Información de Movimiento
	private List<HandleArray> animationHandles;
	private List<VertexArray> animationListVertices;

	// Informacion para la reproduccion de la animacion
	private VertexArray animationVertices;
	private FloatBuffer animationTriangles;
	private FloatBuffer animationHull;
	private int animationPosition;

	/* Esqueleto */

	// Indice de Vertices que forman en ConvexHull
	private HullArray hull;
	private FloatBuffer bufferHull;

	// Coordenadas de Vertices
	private VertexArray vertices;
	private VertexArray verticesModified;

	// Indice de Vertices que forman Triángulos
	private TriangleArray triangles;
	private FloatBuffer bufferTriangles;
	
	// Buscador de Triángulos
	private TriangleQuadTreeSearcher searcher;

	/* Handles */

	// Coordenadas de Handles
	private HandleArray handles;
	private short[] pointers;
	private int numPointers;
	
	private Handle handleObject, handleSelectedObject;

	/* Textura */
	private Texture texture;
	private FloatBuffer textureCoords;
	
	// Pegatinas
	private Stickers stickers;

	/* Constructora */

	public DeformOpenGLRenderer(Context context, int color, OnDeformListener listener, Character personaje, TTypeMovement movimiento)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character, color);
		
		mListener = listener;
		mState = TStateDeform.Nothing;
		recordActive = false;
		
		animationHandles = new ArrayList<HandleArray>();
		
		if (personaje.isMovementsReady())
		{
			animationListVertices = personaje.getMovements().get(movimiento);
		}
		else
		{
			animationListVertices = new ArrayList<VertexArray>();
		}

		// Esqueleto
		hull = personaje.getSkeleton().getHull();
		vertices = personaje.getSkeleton().getVertices();
		verticesModified = vertices.clone();
		triangles = personaje.getSkeleton().getTriangles();

		bufferHull = BufferManager.buildBufferVertexIndexList(hull, vertices);
		bufferTriangles = BufferManager.buildBufferTriangleFillList(triangles, vertices);

		// Handles
		handles = new HandleArray();
		pointers = new short[GamePreferences.NUM_HANDLES];
		numPointers = 0;
		
		resetHandles();
		
		// Textura
		texture = personaje.getTexture();
		textureCoords = BufferManager.buildBufferTriangleFillList(triangles, texture.getTextureCoords());
		stickers = texture.getStickers();

		handleObject = new Handle(20, GamePreferences.POINT_WIDTH, Color.BLACK);
		handleSelectedObject = new Handle(20, 2 * GamePreferences.POINT_WIDTH, Color.RED);
	}

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// Textura
		texture.loadTexture(gl, this, mContext, TTypeEntity.Character, 0);

		// Pegatinas
		stickers.loadTexture(gl, this, mContext, TTypeEntity.Character, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (mState == TStateDeform.Playing)
		{
			// Centrado de Marco
			drawInsideFrameBegin(gl);

			drawCharacter(gl, animationTriangles, animationHull, animationVertices);

			// Centrado de Marco
			drawInsideFrameEnd(gl);
		}
		else
		{
			// Marcos
			drawFrameInside(gl, Color.LTGRAY, GamePreferences.DEEP_INSIDE_FRAMES);
			
			// Centrado de Marco
			drawInsideFrameBegin(gl);

			drawCharacter(gl, bufferTriangles, bufferHull, verticesModified);

			// Handles
			OpenGLManager.drawHandleList(gl, handleObject, handleSelectedObject, handles);

			// Centrado de Marco
			drawInsideFrameEnd(gl);
		}
	}

	public void drawCharacter(GL10 gl, FloatBuffer malla, FloatBuffer contorno, VertexArray vertices)
	{
		// Textura
		texture.drawTexture(gl, this, malla, textureCoords, TTypeEntity.Character, 0);

		// Contorno
		OpenGLManager.drawBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, contorno);

		// Pegatinas
		stickers.drawTexture(gl, this, vertices, triangles, TTypeEntity.Character, 0);
	}

	/* Métodos de Selección de Estado */

	public void selectAdding()
	{		
		mState = TStateDeform.Adding;
		
		if (deformator == null)
		{
			deformator = new Deformator(verticesModified, triangles, handles);
		}
		
		if (searcher == null)
		{
			searcher = new TriangleQuadTreeSearcher(triangles, verticesModified, 0.0f, 0.0f, frameWidthMiddle, frameWidthMiddle);
		}
	}

	public void selectDeleting()
	{
		mState = TStateDeform.Deleting;
	}

	public void selectMoving()
	{
		mState = TStateDeform.Moving;
		
		searcher = null;
	}

	/* Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean onReset()
	{
		mState = TStateDeform.Nothing;
		recordActive = false;
		
		searcher = null;

		handles.clear();

		verticesModified = vertices.clone();
		BufferManager.updateBufferTriangleFillList(bufferTriangles, triangles, verticesModified);
		BufferManager.updateBufferVertexIndexList(bufferHull, hull, verticesModified);

		animationHandles.clear();
		animationListVertices.clear();

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStateDeform.Adding)
		{
			return addHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (mState == TStateDeform.Deleting)
		{
			return deleteHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (mState == TStateDeform.Moving)
		{
			return selectHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	private boolean addHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
		
		short triangle = searcher.searchTriangle(frameX, frameY);
		if (triangle != -1)
		{			
			handles.addHandle(frameX, frameY, triangle, verticesModified, triangles);
			
			// Añadir Handle Nuevo
			deformator.addHandles(handles);
			
			return true;
		}

		return false;
	}
	
	private short searchHandle(HandleArray handles, VertexArray vertices, TriangleArray triangulos, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		for (short i = 0; i < handles.getNumHandles(); i++)
		{
			float frameX = handles.getXCoordHandle(i);
			float frameY = handles.getYCoordHandle(i);
			
			float lastPixelX = convertFrameXToPixelXCoordinate(frameX, screenWidth);
			float lastPixelY = convertFrameYToPixelYCoordinate(frameY, screenHeight);

			float distancia = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY));
			if (distancia < GamePreferences.MAX_DISTANCE_HANDLES)
			{
				return i;
			}
		}
		
		return -1;
	}

	private boolean deleteHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		short handle = searchHandle(handles, vertices, triangles, pixelX, pixelY, screenWidth, screenHeight);
		if (handle != -1)
		{
			handles.removeHandle(handle);
			
			// Eliminar Handle
			deformator.deleteHandles(handles);
			
			return true;
		}

		return false;
	}

	private boolean selectHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		short handle = searchHandle(handles, vertices, triangles, pixelX, pixelY, screenWidth, screenHeight);
		if (handle != -1)
		{
			if (!handles.isSelectedHandle(handle))
			{
				handles.setSelectedHandle(handle, true);
				pointers[pointer] = handle;
				numPointers++;
	
				if (recordActive)
				{
					animationHandles.add(handles.clone());
				}
				
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStateDeform.Moving)
		{
			// Handle sin Pulsar
			if (pointers[pointer] == -1 || !handles.isSelectedHandle(pointers[pointer]))
			{
				return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
			else
			{
				return moveHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
		}

		return false;
	}

	private boolean moveHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		float lastFrameX = handles.getXCoordHandle((short) pointer);
		float lastFrameY = handles.getYCoordHandle((short) pointer);

		float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
		float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

		if (Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3 * GamePreferences.MAX_DISTANCE_PIXELS)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			if (!isPointOutsideFrame(frameX, frameY))
			{
				handles.setCoordsHandle(pointers[pointer], frameX, frameY);
				return true;
			}
		}

		return false;
	}
	
	@Override
	protected boolean onTouchPointerUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStateDeform.Moving && pointers[pointer] != -1)
		{
			handles.setSelectedHandle(pointers[pointer], true);
			
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TStateDeform.Moving && pointers[pointer] != -1)
		{
			handles.setSelectedHandle(pointers[pointer], true);
			
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			handles.setSelectedHandle(pointers[pointer], false);
			
			// Reiniciar punteros
			for (short i = 0; i < GamePreferences.NUM_HANDLES; i++)
			{
				pointers[i] = -1;
			}

			if (recordActive && animationHandles.size() > 0)
			{
				recordActive = false;
				
				final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_character_title), mContext.getString(R.string.text_processing_character_description), true);

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run()
					{
						buildAnimation();
						mState = TStateDeform.Nothing;
						alert.dismiss();
					}
				});
				
				thread.start();
				return true;
			}
		}

		return false;
	}
	
	protected boolean onMultiTouchPreAction(int countPointer)
	{
		if (mState == TStateDeform.Moving)
		{
			if (numPointers > countPointer)
			{
				numPointers = 0;
				
				for (short i = 0; i < handles.getNumHandles(); i++)
				{
					handles.setSelectedHandle(i, false);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	protected boolean onMultiTouchPostAction()
	{
		if (mState == TStateDeform.Moving)
		{
			if (recordActive)
			{
				animationHandles.add(handles.clone());
			}
			
			// Cambiar Posicion de los Handles
			deformator.moveHandles(handles, verticesModified);

			BufferManager.updateBufferTriangleFillList(bufferTriangles, triangles, verticesModified);
			BufferManager.updateBufferVertexIndexList(bufferHull, hull, verticesModified);

			return true;
		}

		return false;
	}

	private void buildAnimation()
	{
		int numFramesDescartar = 1;
		int numFramesRepetir = 1;

		if (animationHandles.size() >= GamePreferences.NUM_FRAMES_ANIMATION)
		{
			numFramesDescartar = Math.round((float) animationHandles.size() / (float) GamePreferences.NUM_FRAMES_ANIMATION);
		}
		else
		{
			numFramesRepetir = Math.round((float) GamePreferences.NUM_FRAMES_ANIMATION / (float) animationHandles.size());
		}

		VertexArray frame = vertices.clone();
		HandleArray lastHandles = null;
		
		int i = 0;
		while (i < animationHandles.size())
		{
			if (lastHandles != null)
			{
				for(int j = 0; j < numFramesRepetir - 1; j++)
				{
					HandleArray handleInterpolado = lastHandles.interpolar(animationHandles.get(i), j / numFramesRepetir);
					
					deformator.moveHandles(handleInterpolado, frame);
					animationListVertices.add(frame.clone());
				}
			}
			
			deformator.moveHandles(animationHandles.get(i), frame);
			animationListVertices.add(frame.clone());
			lastHandles = animationHandles.get(i);
			
			i = i + numFramesDescartar;
		}
		
		mListener.onAnimationFinished();
	}
	
	private void resetHandles()
	{
		for (short i = 0; i < handles.getNumHandles(); i++)
		{
			handles.setSelectedHandle(i, false);
			handles.setCoordsHandle(i, verticesModified, triangles);
		}
		
		for (short i = 0; i < GamePreferences.NUM_HANDLES; i++)
		{
			pointers[i] = -1;
		}
	}

	/* Métodos de Selección de Estado */

	public void selectRecording()
	{
		recordActive = true;
		mState = TStateDeform.Moving;

		verticesModified = vertices.clone();
		
		animationHandles.clear();
		animationListVertices.clear();

		resetHandles();

		BufferManager.updateBufferTriangleFillList(bufferTriangles, triangles, verticesModified);
		BufferManager.updateBufferVertexIndexList(bufferHull, hull, verticesModified);
	}

	public void selectPlaying()
	{
		mState = TStateDeform.Playing;

		startAnimation();
	}

	public void startAnimation()
	{
		animationPosition = 0;
		animationVertices = animationListVertices.get(animationPosition);
		animationTriangles = BufferManager.buildBufferTriangleFillList(triangles, animationVertices);
		animationHull = BufferManager.buildBufferVertexIndexList(hull, animationVertices);
	}

	public boolean playAnimation()
	{
		animationVertices = animationListVertices.get(animationPosition);
		BufferManager.updateBufferTriangleFillList(animationTriangles, triangles, animationVertices);
		BufferManager.updateBufferVertexIndexList(animationHull, hull, animationVertices);
		animationPosition++;

		return animationPosition == animationListVertices.size();
	}

	public void stopAnimation()
	{
		mState = TStateDeform.Nothing;
	}

	/* Métodos de Obtención de Información */

	public boolean isHandlesEmpty()
	{
		return handles.getNumHandles() == 0;
	}

	public boolean isStateAdding()
	{
		return mState == TStateDeform.Adding;
	}

	public boolean isStateDeleting()
	{
		return mState == TStateDeform.Deleting;
	}

	public boolean isStateMoving()
	{
		return mState == TStateDeform.Moving;
	}

	public boolean isStateRecording()
	{
		return mState == TStateDeform.Moving && recordActive;
	}

	public boolean isStatePlaying()
	{
		return mState == TStateDeform.Playing;
	}

	public List<VertexArray> getAnimation()
	{
		return animationListVertices;
	}

	public boolean isAnimationReady()
	{
		return animationListVertices.size() > 0;
	}

	/* Métodos de Guardado de Información */

	public DeformDataSaved saveData()
	{
		// Textura
		texture.deleteTexture(this, TTypeEntity.Character, 0);
		
		// Pegatinas
		stickers.deleteTexture(this, TTypeEntity.Character, 0);

		return new DeformDataSaved(handles, verticesModified, mState, animationListVertices);
	}

	public void restoreData(DeformDataSaved data)
	{
		recordActive = false;
		mState = data.getState();
		handles = data.getHandles();
		verticesModified = data.getVertices();
		animationListVertices = data.getAnimationVertices();
		
		if (deformator == null)
		{
			deformator = new Deformator(verticesModified, triangles, handles);
		}
		else if (handles.getNumHandles() > 0)
		{
			deformator.addHandles(handles);
		}
		
		BufferManager.updateBufferTriangleFillList(bufferTriangles, triangles, verticesModified);
		BufferManager.updateBufferVertexIndexList(bufferHull, hull, verticesModified);
	}
}
