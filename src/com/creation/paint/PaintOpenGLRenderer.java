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
	private TStatePaint estado;

	private int colorPaleta;
	private TTypeSize sizeLinea;
	private int pegatinaActual;
	private TTypeSticker tipoPegatinaActual;

	// Detalles
	private List<Polyline> listaLineas;
	private VertexArray lineaActual;
	private FloatBuffer bufferLineaActual;

	// Pegatinas
	private Stickers pegatinas;
	private boolean pegatinaAnyadida;

	// Esqueleto
	private HullArray contorno;
	private FloatBuffer bufferContorno;

	private VertexArray vertices;
	private FloatBuffer bufferVertices;

	private TriangleArray triangulos;

	private int colorPintura;
	
	// Buscador de Triángulos
	private TriangleQuadTreeSearcher buscador;

	// Texturas
	private TStateScreenshot estadoCaptura;

	private BitmapImage textura;
	private VertexArray coordsTextura;

	// Anterior Siguiente Buffers
	private Stack<Action> anteriores;
	private Stack<Action> siguientes;

	/* Constructora */
	
	public PaintOpenGLRenderer(Context context, int color, Character personaje)
	{
		super(context, TTypeBackgroundRenderer.Blank, TTypeTexturesRenderer.Character, color);

		estado = TStatePaint.Nothing;

		contorno = personaje.getSkeleton().getHull();
		vertices = personaje.getSkeleton().getVertices();
		triangulos = personaje.getSkeleton().getTriangles();

		bufferVertices = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		
		if (personaje.isTextureReady())
		{
			pegatinas = personaje.getTexture().getStickers();
		}
		else
		{
			pegatinas = new Stickers();
		}
		
		pegatinaActual = 0;
		pegatinaAnyadida = false;
		
		listaLineas = new ArrayList<Polyline>();
		lineaActual = null;

		colorPintura = Color.WHITE;

		colorPaleta = Color.RED;
		sizeLinea = TTypeSize.Small;

		anteriores = new Stack<Action>();
		siguientes = new Stack<Action>();

		estadoCaptura = TStateScreenshot.Nothing;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		if (estado == TStatePaint.Screenshot && estadoCaptura == TStateScreenshot.Capturing)
		{
			// Guardar posición actual de la Cámara
			saveCamera();

			// Restaurar Cámara posición inicial
			camaraRestore();

			dibujarEsqueleto(gl);

			// Capturar Pantalla
			textura = getScreenshot(gl);

			// Construir Textura
			coordsTextura = buildTexture(vertices, textura.getWidth(), textura.getHeight());

			// Restaurar posición anterior de la Cámara
			restoreCamera();
		}

		// Cargar Pegatinas
		pegatinas.loadTexture(gl, this, mContext, TTypeEntity.Character, 0);

		dibujarEsqueleto(gl);
		
		if (estado == TStatePaint.Screenshot && estadoCaptura == TStateScreenshot.Capturing)
		{
			// Desactivar Modo Captura
			estadoCaptura = TStateScreenshot.Finished;
		}
	}

	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		drawInsideFrameBegin(gl);

		// Esqueleto
		OpenGLManager.dibujarBuffer(gl, GL10.GL_TRIANGLES, GamePreferences.SIZE_LINE, colorPintura, bufferVertices);

		gl.glPushMatrix();
		
			gl.glTranslatef(0.0f, 0.0f, GamePreferences.DEEP_POLYLINES);
			
			// Detalles
			if (lineaActual != null)
			{
				synchronized (lineaActual)
				{
					OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, sizeLinea.getSize(), colorPaleta, bufferLineaActual);
				}
			}
			
			synchronized (listaLineas)
			{
				Iterator<Polyline> it = listaLineas.iterator();
				while (it.hasNext())
				{
					Polyline polilinea = it.next();
					OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize().getSize(), polilinea.getColor(), polilinea.getBuffer());
				}
			}

		gl.glPopMatrix();
			
		if (estado != TStatePaint.Screenshot)
		{
			// Contorno
			OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferContorno);

			// Dibujar Pegatinas
			pegatinas.drawTexture(gl, this, vertices, triangulos, TTypeEntity.Character, 0);
		}

		// Centrado de Marco
		drawInsideFrameEnd(gl);
	}
	
	/* Métodos Abstráctos OpenGLRenderer */

	@Override
	protected boolean onReset()
	{
		lineaActual = null;
		listaLineas.clear();

		pegatinas.deleteTexture(this, TTypeEntity.Character, 0);
		pegatinas.resetSticker();
		
		pegatinaActual = 0;
		pegatinaAnyadida = false;

		anteriores.clear();
		siguientes.clear();

		estado = TStatePaint.Nothing;
		colorPintura = Color.WHITE;
		sizeLinea = TTypeSize.Small;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TStatePaint.Pencil)
		{
			return anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TStatePaint.Bucket)
		{
			return pintarEsqueleto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TStatePaint.AddSticker)
		{
			return anyadirPegatina(pixelX, pixelY, screenWidth, screenHeight);
		}

		return false;
	}

	private boolean anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (lineaActual == null)
		{
			lineaActual = new VertexArray();
		}

		boolean anyadir = true;

		if (lineaActual.getNumVertices() > 0)
		{
			float lastFrameX = lineaActual.getLastXVertex();
			float lastFrameY = lineaActual.getLastYVertex();

			float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
			float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > GamePreferences.MAX_DISTANCE_PIXELS;
		}

		if (anyadir)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			synchronized (lineaActual)
			{
				lineaActual.addVertex(frameX, frameY);
	
				bufferLineaActual = BufferManager.construirBufferListaPuntos(lineaActual);
			}
			
			return true;
		}

		return false;
	}

	private boolean pintarEsqueleto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

		if (GeometryUtils.isPointInsideMesh(contorno, vertices, frameX, frameY))
		{
			if (colorPaleta != colorPintura)
			{
				colorPintura = colorPaleta;

				anteriores.push(new ActionColor(colorPaleta));
				siguientes.clear();

				return true;
			}
		}

		return false;
	}

	private boolean anyadirPegatina(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
		
		short triangle = buscador.searchTriangle(frameX, frameY);
		if (triangle != -1)
		{			
			pegatinas.addSticker(tipoPegatinaActual, pegatinaActual, frameX, frameY, triangle, vertices, triangulos);

			deleteTextureRectangle(TTypeEntity.Character, 0, tipoPegatinaActual);
			pegatinaAnyadida = true;

			anteriores.push(new ActionSticker(tipoPegatinaActual, pegatinaActual, frameX, frameY, triangle, pegatinas.getFactor(tipoPegatinaActual), pegatinas.getTheta(tipoPegatinaActual)));
			siguientes.clear();
			
			estado = TStatePaint.Nothing;
			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TStatePaint.Pencil)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TStatePaint.Pencil)
		{
			return guardarPolilinea();
		}

		return false;
	}
	
	@Override
	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TStatePaint.EditSticker)
		{
			pegatinas.zoomSticker(tipoPegatinaActual, factor);
		}
	}

	@Override
	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TStatePaint.EditSticker)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

			float lastFrameX = convertPixelXToFrameXCoordinate(lastPixelX, screenWidth);
			float lastFrameY = convertPixelYToFrameYCoordinate(lastPixelY, screenHeight);
			
			float dWorldX = frameX - lastFrameX;
			float dWorldY = frameY - lastFrameY;

			if (Math.abs(Intersector.distancePoints(0.0f, 0.0f, dWorldX, dWorldY)) > GamePreferences.MAX_DISTANCE_PIXELS)
			{
				float newStickerFrameX = pegatinas.getXCoords(tipoPegatinaActual, vertices, triangulos) + dWorldX;
				float newtStickerFrameY = pegatinas.getYCoords(tipoPegatinaActual, vertices, triangulos) + dWorldY;
				
				short triangle = buscador.searchTriangle(newStickerFrameX, newtStickerFrameY);
				if (triangle != -1)
				{			
					pegatinas.moveSticker(tipoPegatinaActual, newStickerFrameX, newtStickerFrameY, triangle, vertices, triangulos);
				}
			}
		}
	}

	@Override
	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (estado == TStatePaint.EditSticker)
		{
			pegatinas.rotateSticker(tipoPegatinaActual, (float) Math.toDegrees(angRad));
		}
	}
	
	@Override
	public void pointsRestore()
	{
		if (estado == TStatePaint.EditSticker)
		{
			pegatinas.restoreSticker(tipoPegatinaActual);
		}
	}

	private boolean guardarPolilinea()
	{
		if (lineaActual != null)
		{
			synchronized (listaLineas)
			{
				Polyline polilinea = new Polyline(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);
	
				listaLineas.add(polilinea);
				anteriores.push(new ActionPolyline(polilinea));
				siguientes.clear();
				lineaActual = null;
			}

			return true;
		}

		return false;
	}
	
	private boolean guardarPegatina()
	{
		if (estado == TStatePaint.EditSticker)
		{
			int id = pegatinas.getId(tipoPegatinaActual);
			float x = pegatinas.getXCoords(tipoPegatinaActual, vertices, triangulos);
			float y = pegatinas.getYCoords(tipoPegatinaActual, vertices, triangulos);
			short indice = pegatinas.getIndex(tipoPegatinaActual);
			float factor = pegatinas.getFactor(tipoPegatinaActual);
			float angulo = pegatinas.getTheta(tipoPegatinaActual);

			anteriores.push(new ActionSticker(tipoPegatinaActual, id, x, y, indice, factor, angulo));
			siguientes.clear();
			
			return true;
		}
		
		return false;
	}
	
	/* Métodos de Selección de Estado */

	public void seleccionarNada()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TStatePaint.Nothing;
	}
	
	public void seleccionarMano()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TStatePaint.Hand;
	}

	public void seleccionarPincel()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TStatePaint.Pencil;
	}

	public void seleccionarCubo()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TStatePaint.Bucket;
	}

	public void seleccionarColor(int color)
	{
		colorPaleta = color;
	}

	public void seleccionarSize(TTypeSize size)
	{
		sizeLinea = size;
	}

	public void seleccionarPegatina(int pegatina, TTypeSticker tipo)
	{
		guardarPolilinea();

		pegatinaActual = pegatina;
		tipoPegatinaActual = tipo;
		estado = TStatePaint.AddSticker;
		
		if (buscador == null)
		{
			buscador = new TriangleQuadTreeSearcher(triangulos, vertices, 0.0f, 0.0f, frameWidthMiddle, frameWidthMiddle);
		}
	}
	
	public void eliminarPegatina(TTypeSticker tipo)
	{
		guardarPolilinea();
		
		if (pegatinas.isStickerLoaded(tipo))
		{
			deleteTextureRectangle(TTypeEntity.Character, 0, tipo);
			pegatinas.deleteSticker(tipo);
			
			anteriores.push(new ActionSticker(tipo));
			siguientes.clear();
		}
		
		estado = TStatePaint.Nothing;
	}
	
	public void editarPegatina(TTypeSticker tipo)
	{
		guardarPolilinea();
		
		if (buscador == null)
		{
			buscador = new TriangleQuadTreeSearcher(triangulos, vertices, 0.0f, 0.0f, frameWidthMiddle, frameWidthMiddle);
		}
		
		if (pegatinas.isStickerLoaded(tipo))
		{
			tipoPegatinaActual = tipo;
			estado = TStatePaint.EditSticker;
		}
		else
		{
			estado = TStatePaint.Nothing;
		}
	}

	public void seleccionarCaptura() 
	{
		guardarPolilinea();

		estado = TStatePaint.Screenshot;
		estadoCaptura = TStateScreenshot.Capturing;
	}

	/* Métodos de modificación de Buffers de estado */

	public void anteriorAccion()
	{
		guardarPolilinea();

		if (!anteriores.isEmpty())
		{
			Action accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public void siguienteAccion()
	{
		guardarPolilinea();

		if (!siguientes.isEmpty())
		{
			Action accion = siguientes.lastElement();
			siguientes.remove(siguientes.size() - 1);
			anteriores.push(accion);
			actualizarEstado(anteriores);
		}
	}

	private void actualizarEstado(Stack<Action> pila)
	{
		colorPintura = Color.WHITE;
		listaLineas = new ArrayList<Polyline>();
		
		pegatinas.deleteTexture(this, TTypeEntity.Character, 0);
		pegatinas.resetSticker();

		Iterator<Action> it = pila.iterator();
		while (it.hasNext())
		{
			Action accion = it.next();
			if (accion.isTipoColor())
			{
				actualizarEstado((ActionColor) accion);
			}
			else if (accion.isTipoPolilinea())
			{
				actualizarEstado((ActionPolyline) accion);
			}
			else if (accion.isTipoPegatina())
			{
				actualizarEstado((ActionSticker) accion);
			}
		}
	}
	
	private void actualizarEstado(ActionColor accion)
	{
		colorPintura = accion.getColorFondo();
	}
	
	private void actualizarEstado(ActionPolyline accion)
	{
		listaLineas.add(accion.getPolilinea());
	}
	
	private void actualizarEstado(ActionSticker accion)
	{
		pegatinas.addSticker(accion.getTipoPegatina(), accion.getIdPegatina(), accion.getPosXPegatina(), accion.getPosYPegatina(), accion.getIndiceTriangulo(), accion.getFactorEscala(), accion.getAnguloRotacion(), vertices, triangulos);
	}

	/* Métodos de Obtención de Información */

	public boolean isBufferSiguienteVacio()
	{
		return siguientes.isEmpty();
	}

	public boolean isBufferAnteriorVacio()
	{
		return anteriores.isEmpty();
	}

	public boolean isPegatinaAnyadida()
	{
		if (pegatinaAnyadida)
		{
			pegatinaAnyadida = false;
			estado = TStatePaint.Nothing;

			return true;
		}

		return false;
	}

	public boolean isEstadoPincel()
	{
		return estado == TStatePaint.Pencil;
	}

	public boolean isEstadoCubo()
	{
		return estado == TStatePaint.Bucket;
	}

	public boolean isEstadoMover()
	{
		return estado == TStatePaint.Hand;
	}

	public boolean isEstadoPegatinas()
	{
		return estado == TStatePaint.AddSticker || estado == TStatePaint.EditSticker;
	}
	
	public Texture getTextura()
	{
		if (estado == TStatePaint.Screenshot && estadoCaptura == TStateScreenshot.Capturing)
		{
			while (estadoCaptura != TStateScreenshot.Finished);

			estado = TStatePaint.Nothing;
			estadoCaptura = TStateScreenshot.Nothing;
			
			return new Texture(textura, coordsTextura, pegatinas);
		}
		
		return null;
	}

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
		// Pegatinas
		pegatinas.deleteTexture(this, TTypeEntity.Character, 0);
				
		return new PaintDataSaved(anteriores, siguientes, estado);
	}

	public void restoreData(PaintDataSaved data)
	{
		estado = data.getEstado();
		anteriores = data.getAnteriores();
		siguientes = data.getSiguientes();

		actualizarEstado(anteriores);
	}
}
