package com.creation.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.character.display.TEstadoCaptura;
import com.creation.data.Accion;
import com.creation.data.MapaBits;
import com.creation.data.Pegatinas;
import com.creation.data.Polilinea;
import com.creation.data.TTipoSticker;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.game.data.TTipoEntidad;
import com.lib.buffer.BufferManager;
import com.lib.buffer.HullArray;
import com.lib.buffer.TriangleArray;
import com.lib.buffer.VertexArray;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.project.main.R;
import com.project.model.GamePreferences;

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
		super(context, color);

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

			// Desactivar Modo Captura
			estadoCaptura = TEstadoCaptura.Terminado;

			// Restaurar posición anterior de la Cámara
			recuperarCamara();
		}

		// Cargar Pegatinas
		pegatinas.cargarTexturas(gl, this, mContext, TTipoEntidad.Personaje, 0);

		dibujarEsqueleto(gl);
	}

	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

		// Esqueleto
		BufferManager.dibujarBuffer(gl, GL10.GL_TRIANGLES, GamePreferences.SIZE_LINE, colorPintura, bufferVertices);

		gl.glPushMatrix();
		
			gl.glTranslatef(0.0f, 0.0f, GamePreferences.DEEP_POLYLINES);
			
			// Detalles
			if (lineaActual != null)
			{
				synchronized (lineaActual)
				{
					BufferManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, sizeLinea.getSize(), colorPaleta, bufferLineaActual);
				}
			}
			
			synchronized (listaLineas)
			{
				Iterator<Polilinea> it = listaLineas.iterator();
				while (it.hasNext())
				{
					Polilinea polilinea = it.next();
					BufferManager.dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize().getSize(), polilinea.getColor(), polilinea.getBuffer());
				}
			}

		gl.glPopMatrix();
			
		if (estado != TEstadoPaint.Captura)
		{
			// Contorno
			BufferManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, bufferContorno);

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
		else if (estado == TEstadoPaint.Pegatinas)
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

				anteriores.push(new Accion(colorPaleta));
				siguientes.clear();

				return true;
			}
		}

		return false;
	}

	private boolean anyadirPegatina(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		short triangle = buscarTriangulo(contorno, vertices, triangulos, pixelX, pixelY, screenWidth, screenHeight);
		if (triangle != -1)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			pegatinas.setPegatina(tipoPegatinaActual, pegatinaActual, frameX, frameY, triangle, vertices, triangulos);

			descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, tipoPegatinaActual);
			pegatinaAnyadida = true;

			anteriores.push(new Accion(tipoPegatinaActual));
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

	private boolean guardarPolilinea()
	{
		if (lineaActual != null)
		{
			synchronized (listaLineas)
			{
				Polilinea polilinea = new Polilinea(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);
	
				listaLineas.add(polilinea);
				anteriores.push(new Accion(polilinea));
				siguientes.clear();
				lineaActual = null;
			}

			return true;
		}

		return false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarMano()
	{
		guardarPolilinea();
		estado = TEstadoPaint.Mano;
	}

	public void seleccionarPincel()
	{
		guardarPolilinea();
		estado = TEstadoPaint.Pincel;
	}

	public void seleccionarCubo()
	{
		guardarPolilinea();
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
		estado = TEstadoPaint.Pegatinas;
	}
	
	public void eliminarPegatina(TTipoSticker tipo)
	{
		guardarPolilinea();
		
		descargarTexturaRectangulo(TTipoEntidad.Personaje, 0, tipo);
		pegatinas.eliminarPegatinas(tipo);
		estado = TEstadoPaint.Nada;
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
		pegatinas.ocultarPegatinas();

		Iterator<Accion> it = pila.iterator();
		while (it.hasNext())
		{
			Accion accion = it.next();
			if (accion.isTipoColor())
			{
				colorPintura = accion.getColor();
			}
			else if (accion.isTipoPolilinea())
			{
				listaLineas.add(accion.getLinea());
			}
			else if (accion.isTipoPegatina())
			{
				pegatinas.mostrarPegatina(accion.getTipoPegatina());
			}
		}
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
		return estado == TEstadoPaint.Pegatinas;
	}

	public Textura getTextura()
	{
		if (estadoCaptura == TEstadoCaptura.Capturando)
		{
			final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_character_title), mContext.getString(R.string.text_processing_character_description), true);

			Thread thread = new Thread(new Runnable() {
				@Override
				public void run()
				{
					while (estadoCaptura != TEstadoCaptura.Terminado);

					estado = TEstadoPaint.Nada;
					estadoCaptura = TEstadoCaptura.Nada;
					
					alert.dismiss();
				}
			});
			
			thread.start();

			// Esperar por la finalización del thread.
			
			try
			{
				thread.join();
				
				return new Textura(textura, coordsTextura, pegatinas);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
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
