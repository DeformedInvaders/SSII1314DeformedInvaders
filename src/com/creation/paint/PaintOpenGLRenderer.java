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
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.character.display.TEstadoCaptura;
import com.creation.data.MapaBits;
import com.creation.data.Pegatinas;
import com.creation.data.Polilinea;
import com.creation.data.TTipoSticker;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.game.data.TTipoEntidad;
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
	private TEstadoPaint estado;

	private int colorPaleta;
	private TTipoSize sizeLinea;
	private int pegatinaActual;
	private TTipoSticker tipoPegatinaActual;

	// Detalles
	private List<Polilinea> listaLineas;
	private VertexArray lineaActual;
	private FloatBuffer bufferLineaActual;

	// Pegatinas
	private Pegatinas pegatinas;
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
	private TEstadoCaptura estadoCaptura;

	private MapaBits textura;
	private VertexArray coordsTextura;

	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;

	/* Constructora */
	
	public PaintOpenGLRenderer(Context context, int color, Personaje personaje)
	{
		super(context, TTipoFondoRenderer.Nada, TTipoTexturasRenderer.Personaje, color);

		estado = TEstadoPaint.Nada;

		contorno = personaje.getEsqueleto().getContorno();
		vertices = personaje.getEsqueleto().getVertices();
		triangulos = personaje.getEsqueleto().getTriangulos();

		bufferVertices = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		
		if (personaje.getTextura() != null)
		{
			pegatinas = personaje.getTextura().getPegatinas();
		}
		else
		{
			pegatinas = new Pegatinas();
		}
		
		pegatinaActual = 0;
		pegatinaAnyadida = false;
		
		listaLineas = new ArrayList<Polilinea>();
		lineaActual = null;

		colorPintura = Color.WHITE;

		colorPaleta = Color.RED;
		sizeLinea = TTipoSize.Small;

		anteriores = new Stack<Accion>();
		siguientes = new Stack<Accion>();

		estadoCaptura = TEstadoCaptura.Nada;
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		if (estado == TEstadoPaint.Captura && estadoCaptura == TEstadoCaptura.Capturando)
		{
			// Guardar posición actual de la Cámara
			salvarCamara();

			// Restaurar Cámara posición inicial
			camaraRestore();

			dibujarEsqueleto(gl);

			// Capturar Pantalla
			textura = capturaPantalla(gl);

			// Construir Textura
			coordsTextura = construirTextura(vertices, textura.getWidth(), textura.getHeight());

			// Restaurar posición anterior de la Cámara
			recuperarCamara();
		}

		// Cargar Pegatinas
		pegatinas.cargarTexturas(gl, this, mContext, TTipoEntidad.Personaje, 0);

		dibujarEsqueleto(gl);
		
		if (estado == TEstadoPaint.Captura && estadoCaptura == TEstadoCaptura.Capturando)
		{
			// Desactivar Modo Captura
			estadoCaptura = TEstadoCaptura.Terminado;
		}
	}

	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

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
				Iterator<Polilinea> it = listaLineas.iterator();
				while (it.hasNext())
				{
					Polilinea polilinea = it.next();
					OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize().getSize(), polilinea.getColor(), polilinea.getBuffer());
				}
			}

		gl.glPopMatrix();
			
		if (estado != TEstadoPaint.Captura)
		{
			// Contorno
			OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferContorno);

			// Dibujar Pegatinas
			pegatinas.dibujar(gl, this, vertices, triangulos, TTipoEntidad.Personaje, 0);
		}

		// Centrado de Marco
		centrarPersonajeEnMarcoFinal(gl);
	}
	
	/* Métodos Abstráctos OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		lineaActual = null;
		listaLineas.clear();

		pegatinas.descargarTextura(this, TTipoEntidad.Personaje, 0);
		pegatinas.eliminarPegatinas();
		pegatinaActual = 0;
		pegatinaAnyadida = false;

		anteriores.clear();
		siguientes.clear();

		estado = TEstadoPaint.Nada;
		colorPintura = Color.WHITE;
		sizeLinea = TTipoSize.Small;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoPaint.Pincel)
		{
			return anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TEstadoPaint.Cubo)
		{
			return pintarEsqueleto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TEstadoPaint.AnyadirPegatinas)
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

				anteriores.push(new AccionColor(colorPaleta));
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
			pegatinas.anyadirPegatina(tipoPegatinaActual, pegatinaActual, frameX, frameY, triangle, vertices, triangulos);

			descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, tipoPegatinaActual);
			pegatinaAnyadida = true;

			anteriores.push(new AccionPegatina(tipoPegatinaActual, pegatinaActual, frameX, frameY, triangle, pegatinas.getFactor(tipoPegatinaActual), pegatinas.getTheta(tipoPegatinaActual)));
			siguientes.clear();

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoPaint.Pincel)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoPaint.Pincel)
		{
			return guardarPolilinea();
		}

		return false;
	}
	
	@Override
	public void pointsZoom(float factor, float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoPaint.EditarPegatinas)
		{
			pegatinas.ampliarPegatina(tipoPegatinaActual, factor);
		}
	}

	@Override
	public void pointsDrag(float pixelX, float pixelY, float lastPixelX, float lastPixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoPaint.EditarPegatinas)
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
					pegatinas.moverPegatina(tipoPegatinaActual, newStickerFrameX, newtStickerFrameY, triangle, vertices, triangulos);
				}
			}
		}
	}

	@Override
	public void pointsRotate(float angRad, float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		if (estado == TEstadoPaint.EditarPegatinas)
		{
			pegatinas.rotarPegatina(tipoPegatinaActual, (float) Math.toDegrees(angRad));
		}
	}
	
	@Override
	public void pointsRestore()
	{
		if (estado == TEstadoPaint.EditarPegatinas)
		{
			pegatinas.recuperarPegatina(tipoPegatinaActual);
		}
	}

	private boolean guardarPolilinea()
	{
		if (lineaActual != null)
		{
			synchronized (listaLineas)
			{
				Polilinea polilinea = new Polilinea(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);
	
				listaLineas.add(polilinea);
				anteriores.push(new AccionPolilinea(polilinea));
				siguientes.clear();
				lineaActual = null;
			}

			return true;
		}

		return false;
	}
	
	private boolean guardarPegatina()
	{
		if (estado == TEstadoPaint.EditarPegatinas)
		{
			int id = pegatinas.getId(tipoPegatinaActual);
			float x = pegatinas.getXCoords(tipoPegatinaActual, vertices, triangulos);
			float y = pegatinas.getYCoords(tipoPegatinaActual, vertices, triangulos);
			short indice = pegatinas.getIndice(tipoPegatinaActual);
			float factor = pegatinas.getFactor(tipoPegatinaActual);
			float angulo = pegatinas.getTheta(tipoPegatinaActual);

			anteriores.push(new AccionPegatina(tipoPegatinaActual, id, x, y, indice, factor, angulo));
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
		estado = TEstadoPaint.Nada;
	}
	
	public void seleccionarMano()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TEstadoPaint.Mano;
	}

	public void seleccionarPincel()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TEstadoPaint.Pincel;
	}

	public void seleccionarCubo()
	{
		guardarPolilinea();
		guardarPegatina();
		estado = TEstadoPaint.Cubo;
	}

	public void seleccionarColor(int color)
	{
		colorPaleta = color;
	}

	public void seleccionarSize(TTipoSize size)
	{
		sizeLinea = size;
	}

	public void seleccionarPegatina(int pegatina, TTipoSticker tipo)
	{
		guardarPolilinea();

		pegatinaActual = pegatina;
		tipoPegatinaActual = tipo;
		estado = TEstadoPaint.AnyadirPegatinas;
		
		if (buscador == null)
		{
			buscador = new TriangleQuadTreeSearcher(triangulos, vertices, 0.0f, 0.0f, marcoAnchuraInterior, marcoAnchuraInterior);
		}
	}
	
	public void eliminarPegatina(TTipoSticker tipo)
	{
		guardarPolilinea();
		
		if (pegatinas.isCargada(tipo))
		{
			descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, tipo);
			pegatinas.eliminarPegatina(tipo);
			
			anteriores.push(new AccionPegatina(tipo));
			siguientes.clear();
		}
		
		estado = TEstadoPaint.Nada;
	}
	
	public void editarPegatina(TTipoSticker tipo)
	{
		guardarPolilinea();
		
		if (pegatinas.isCargada(tipo))
		{
			tipoPegatinaActual = tipo;
			estado = TEstadoPaint.EditarPegatinas;
		}
		else
		{
			estado = TEstadoPaint.Nada;
		}
	}

	public void seleccionarCaptura() 
{
		guardarPolilinea();

		estado = TEstadoPaint.Captura;
		estadoCaptura = TEstadoCaptura.Capturando;
	}

	/* Métodos de modificación de Buffers de estado */

	public void anteriorAccion()
	{
		guardarPolilinea();

		if (!anteriores.isEmpty())
		{
			Accion accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public void siguienteAccion()
	{
		guardarPolilinea();

		if (!siguientes.isEmpty())
		{
			Accion accion = siguientes.lastElement();
			siguientes.remove(siguientes.size() - 1);
			anteriores.push(accion);
			actualizarEstado(anteriores);
		}
	}

	private void actualizarEstado(Stack<Accion> pila)
	{
		colorPintura = Color.WHITE;
		listaLineas = new ArrayList<Polilinea>();
		
		pegatinas.descargarTextura(this, TTipoEntidad.Personaje, 0);
		pegatinas.eliminarPegatinas();

		Iterator<Accion> it = pila.iterator();
		while (it.hasNext())
		{
			Accion accion = it.next();
			if (accion.isTipoColor())
			{
				actualizarEstado((AccionColor) accion);
			}
			else if (accion.isTipoPolilinea())
			{
				actualizarEstado((AccionPolilinea) accion);
			}
			else if (accion.isTipoPegatina())
			{
				actualizarEstado((AccionPegatina) accion);
			}
		}
	}
	
	private void actualizarEstado(AccionColor accion)
	{
		colorPintura = accion.getColorFondo();
	}
	
	private void actualizarEstado(AccionPolilinea accion)
	{
		listaLineas.add(accion.getPolilinea());
	}
	
	private void actualizarEstado(AccionPegatina accion)
	{
		pegatinas.anyadirPegatina(accion.getTipoPegatina(), accion.getIdPegatina(), accion.getPosXPegatina(), accion.getPosYPegatina(), accion.getIndiceTriangulo(), accion.getFactorEscala(), accion.getAnguloRotacion(), vertices, triangulos);
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
			estado = TEstadoPaint.Nada;

			return true;
		}

		return false;
	}

	public boolean isEstadoPincel()
	{
		return estado == TEstadoPaint.Pincel;
	}

	public boolean isEstadoCubo()
	{
		return estado == TEstadoPaint.Cubo;
	}

	public boolean isEstadoMover()
	{
		return estado == TEstadoPaint.Mano;
	}

	public boolean isEstadoPegatinas()
	{
		return estado == TEstadoPaint.AnyadirPegatinas || estado == TEstadoPaint.EditarPegatinas;
	}
	
	public Textura getTextura()
	{
		if (estado == TEstadoPaint.Captura && estadoCaptura == TEstadoCaptura.Capturando)
		{
			while (estadoCaptura != TEstadoCaptura.Terminado);

			estado = TEstadoPaint.Nada;
			estadoCaptura = TEstadoCaptura.Nada;
			
			return new Textura(textura, coordsTextura, pegatinas);
		}
		
		return null;
	}

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
		// Pegatinas
		pegatinas.descargarTextura(this, TTipoEntidad.Personaje, 0);
				
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
