package com.creation.design;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.opengl.OpenGLRenderer;
import com.android.opengl.TTypeBackgroundRenderer;
import com.android.opengl.TTypeTexturesRenderer;
import com.creation.data.Skeleton;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.opengl.OpenGLManager;
import com.main.model.GamePreferences;

public class DesignOpenGLRenderer extends OpenGLRenderer
{
	// Estructura de Datos de la Escena
	private TSatateDesign mState;
	private Triangulator triangulator;

	private VertexArray points;
	private FloatBuffer bufferPoints;
	
	private VertexArray vertices;
	private TriangleArray triangles;
	private FloatBuffer bufferTriangles;
	private HullArray hull;
	private FloatBuffer bufferHull;

	private boolean simplex, singular, triangulate;

	/* Constructora */

	public DesignOpenGLRenderer(Context context, int color)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character, color);

		mState = TSatateDesign.Drawing;

		points = new VertexArray();
		
		simplex = false;
		singular = false;
		triangulate = false;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
				
			if (mState == TSatateDesign.Drawing)
			{
				if (points.getNumVertices() > 0)
				{
					// Centrado de Marco
					drawInsideFrameBegin(gl);
	
					if (points.getNumVertices() > 1)
					{
						if (simplex)
						{
							if (triangulate)
							{
								OpenGLManager.drawBuffer(gl, GL10.GL_LINES, GamePreferences.SIZE_LINE, Color.BLACK, bufferTriangles);
							}
							else
							{
								OpenGLManager.drawBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferHull);
							}
						}
						else
						{
							OpenGLManager.drawBuffer(gl, GL10.GL_POINTS, GamePreferences.POINT_WIDTH, Color.RED, bufferPoints);
							OpenGLManager.drawBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLUE, bufferPoints);
						}
					}
					
					// Centrado de Marco
					drawInsideFrameEnd(gl);
				}
			}
			else
			{
				drawFrameInside(gl, Color.LTGRAY, GamePreferences.DEEP_INSIDE_FRAMES);
				
				// Centrado de Marco
				drawInsideFrameBegin(gl);
				
				if (triangulate)
				{
					OpenGLManager.drawBuffer(gl, GL10.GL_LINES, GamePreferences.SIZE_LINE, Color.BLACK, bufferTriangles);
				}
				else
				{
					OpenGLManager.drawBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferHull);
				}
				// Centrado de Marco
				drawInsideFrameEnd(gl);
			}
	}

	/* Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean onReset()
	{
		mState = TSatateDesign.Drawing;
		points.clear();

		vertices = null;
		triangles = null;
		hull = null;
		
		simplex = false;
		singular = false;
		triangulate = false;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TSatateDesign.Drawing)
		{
			return addPoint(pixelX, pixelY, screenWidth, screenHeight);
		}

		return false;
	}

	private boolean addPoint(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		boolean addPoint = true;

		if (points.getNumVertices() > 0)
		{
			float lastFrameX = points.getLastXVertex();
			float lastFrameY = points.getLastYVertex();

			float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
			float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

			addPoint = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > GamePreferences.MAX_DISTANCE_PIXELS;
		}

		if (addPoint)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			points.addVertex(frameX, frameY);

			bufferPoints = BufferManager.buildBufferVertexList(points);

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TSatateDesign.Drawing)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (mState == TSatateDesign.Drawing)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);

			triangulator = new Triangulator(points);

			simplex = triangulator.getSimplex();
			vertices = triangulator.getVertices();
			triangles = triangulator.getTriangles();
			hull = triangulator.getHull();
			singular = triangulator.getSingular();
			
			if (simplex)
			{
				bufferTriangles = BufferManager.buildBufferTriangleList(triangles, vertices);
				bufferHull = BufferManager.buildBufferVertexIndexList(hull, vertices);
				
				mState = TSatateDesign.Preparing;
			}

			return true;
		}

		return false;
	}
	
	@Override
	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (mState == TSatateDesign.Preparing)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);

			float cFrameX = (frameX + lastFrameX) / 2.0f;
			float cFrameY = (frameY + lastFrameY) / 2.0f;

			BufferManager.scaleVertices(factor, factor, cFrameX, cFrameY, vertices);
			BufferManager.updateBufferTriangleList(bufferTriangles, triangles, vertices);
			BufferManager.updateBufferVertexIndexList(bufferHull, hull, vertices);
		}
	}

	@Override
	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (mState == TSatateDesign.Preparing)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);

			float dWorldX = frameX - lastFrameX;
			float dWorldY = frameY - lastFrameY;

			BufferManager.dragVertices(dWorldX, dWorldY, vertices);
			BufferManager.updateBufferTriangleList(bufferTriangles, triangles, vertices);
			BufferManager.updateBufferVertexIndexList(bufferHull, hull, vertices);
		}
	}

	@Override
	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (mState == TSatateDesign.Preparing)
		{
			float cFrameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float cFrameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			BufferManager.rotateVertices(angRad, cFrameX, cFrameY, vertices);
			BufferManager.updateBufferTriangleList(bufferTriangles, triangles, vertices);
			BufferManager.updateBufferVertexIndexList(bufferHull, hull, vertices);
		}
	}

	/* Métodos de Selección de Estado */

	public void selectTriangulate()
	{
		triangulate = !triangulate;
	}

	public void selectPreparing()
	{
		mState = TSatateDesign.Preparing;
	}

	/* Métodos de Obtención de Información */

	public Skeleton getSkeleton()
	{
		if (mState == TSatateDesign.Finished)
		{
			mState = TSatateDesign.Preparing;
			return new Skeleton(hull, vertices, triangles);
		}

		return null;
	}

	public boolean isStateDrawing()
	{
		return mState == TSatateDesign.Drawing;
	}

	public boolean isStateTriangulate()
	{
		return triangulate;
	}

	public boolean isStatePreparing()
	{
		return mState == TSatateDesign.Preparing;
	}

	public boolean isPolygonComplete()
	{
		return points.getNumVertices() >= 3;
	}
	
	public boolean isPolygonSimplex()
	{
		return simplex;
	}
	
	public boolean isPolygonSingular()
	{
		return singular;
	}

	public boolean isPolygonReady()
	{
		for (short i = 0; i < hull.getNumVertices(); i++)
		{
			short a = hull.get(i);
			
			float frameX = vertices.getXVertex(a);
			float frameY = vertices.getYVertex(a);
			
			if (isPointOutsideFrame(frameX, frameY))
			{
				return false;
			}
		}
		
		mState = TSatateDesign.Finished;
		return true;
	}

	/* Métodos de Guardado de Información */

	public DesignDataSaved saveData()
	{
		return new DesignDataSaved(points, vertices, triangles, hull, mState, simplex, singular);
	}

	public void restoreData(DesignDataSaved data)
	{
		mState = data.getState();
		points = data.getPoints();
		vertices = data.getVertices();
		triangles = data.getTriangles();
		hull = data.getHull();
		simplex = data.getSimplex();
		singular = data.getSingular();

		if (simplex)
		{
			bufferPoints = BufferManager.buildBufferVertexList(points);
			bufferTriangles = BufferManager.buildBufferTriangleList(triangles, vertices);
			bufferHull = BufferManager.buildBufferVertexIndexList(hull, vertices);
		}
	}
}
