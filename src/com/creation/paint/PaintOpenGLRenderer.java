package com.creation.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.character.display.TCapturaEstado;
import com.creation.data.Accion;
import com.creation.data.Esqueleto;
import com.creation.data.Handle;
import com.creation.data.MapaBits;
import com.creation.data.Pegatinas;
import com.creation.data.Polilinea;
import com.creation.data.Textura;
import com.game.data.TTipoSticker;
import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.main.GamePreferences;

public class PaintOpenGLRenderer extends OpenGLRenderer
{
	// Estructura de Datos
	private TPaintEstado estado;

	private int colorPaleta;
	private int sizeLinea;
	private int pegatinaActual;
	private TTipoSticker tipoPegatinaActual;

	// Detalles
	private List<Polilinea> listaLineas;
	private FloatArray lineaActual;
	private FloatBuffer bufferLineaActual;

	// Pegatinas
	private Pegatinas pegatinas;
	private boolean pegatinaAnyadida;

	// Esqueleto
	private ShortArray contorno;
	private FloatBuffer bufferContorno;

	private FloatArray vertices;
	private FloatBuffer bufferVertices;

	private ShortArray triangulos;

	private int color;

	// Texturas
	private TCapturaEstado estadoCaptura;

	private MapaBits textura;
	private FloatArray coordsTextura;

	private Handle objetoVertice;

	// Anterior Siguiente Buffers
	private Stack<Accion> anteriores;
	private Stack<Accion> siguientes;

	/* Constructora */

	public PaintOpenGLRenderer(Context context, Esqueleto esqueleto)
	{
		super(context);

		estado = TPaintEstado.Nada;

		contorno = esqueleto.getContorno();
		vertices = esqueleto.getVertices();
		triangulos = esqueleto.getTriangulos();

		bufferVertices = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);
		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);

		listaLineas = new ArrayList<Polilinea>();
		lineaActual = null;

		pegatinas = new Pegatinas();
		pegatinaActual = 0;
		pegatinaAnyadida = false;

		color = Color.WHITE;

		colorPaleta = Color.RED;
		sizeLinea = 6;

		anteriores = new Stack<Accion>();
		siguientes = new Stack<Accion>();

		estadoCaptura = TCapturaEstado.Nada;

		objetoVertice = new Handle(20, POINTWIDTH);
	}

	/* Métodos Renderer */

	@Override
	public void onDrawFrame(GL10 gl)
	{
		if (estado == TPaintEstado.Captura && estadoCaptura == TCapturaEstado.Capturando)
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
			estadoCaptura = TCapturaEstado.Terminado;

			// Restaurar posición anterior de la Cámara
			recuperarCamara();
		}

		// Cargar Pegatinas
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			TTipoSticker tipoPegatinas = TTipoSticker.values()[i];
			
			if (pegatinas.isCargada(tipoPegatinas))
			{
				cargarTexturaRectangulo(gl, pegatinas.getIndice(tipoPegatinas, mContext), tipoPegatinas);
			}
		}

		dibujarEsqueleto(gl);
	}

	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);

		// Centrado de Marco
		centrarPersonajeEnMarcoInicio(gl);

		// Esqueleto
		dibujarBuffer(gl, GL10.GL_TRIANGLES, SIZELINE, color, bufferVertices);

		// Detalles
		if (lineaActual != null)
		{
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, sizeLinea, colorPaleta, bufferLineaActual);
		}

		Iterator<Polilinea> it = listaLineas.iterator();
		while (it.hasNext())
		{
			Polilinea polilinea = it.next();
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize(), polilinea.getColor(), polilinea.getBuffer());
		}

		if (estado != TPaintEstado.Captura)
		{
			// Contorno
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);

			// Dibujar Pegatinas
			for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
			{
				TTipoSticker tipoPegatinas = TTipoSticker.values()[i];
				
				if (pegatinas.isCargada(tipoPegatinas))
				{
					int indice = pegatinas.getVertice(tipoPegatinas);
					dibujarTexturaRectangulo(gl, vertices.get(2 * indice), vertices.get(2 * indice + 1), tipoPegatinas);
				}
			}

			if (estado == TPaintEstado.Pegatinas)
			{
				dibujarListaHandle(gl, Color.BLACK, objetoVertice.getBuffer(), vertices);
			}
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

		pegatinas = new Pegatinas();
		pegatinaActual = 0;
		pegatinaAnyadida = false;

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			descargarTexturaRectangulo(tipoPegatinas[i]);
		}

		anteriores.clear();
		siguientes.clear();

		estado = TPaintEstado.Nada;
		color = Color.WHITE;
		sizeLinea = 6;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TPaintEstado.Pincel)
		{
			return anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TPaintEstado.Cubo)
		{
			return pintarEsqueleto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TPaintEstado.Pegatinas)
		{
			return anyadirPegatina(pixelX, pixelY, screenWidth, screenHeight);
		}

		return false;
	}

	private boolean anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

		if (lineaActual == null)
		{
			lineaActual = new FloatArray();
		}

		boolean anyadir = true;

		if (lineaActual.size > 0)
		{
			float lastWorldX = lineaActual.get(lineaActual.size - 2);
			float lastWorldY = lineaActual.get(lineaActual.size - 1);

			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);

			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > MAX_DISTANCE_PIXELS;
		}

		if (anyadir)
		{
			float frameX = convertToFrameXCoordinate(worldX);
			float frameY = convertToFrameYCoordinate(worldY);

			lineaActual.add(frameX);
			lineaActual.add(frameY);

			bufferLineaActual = BufferManager.construirBufferListaPuntos(lineaActual);

			return true;
		}

		return false;
	}

	private boolean pintarEsqueleto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

		float frameX = convertToFrameXCoordinate(worldX);
		float frameY = convertToFrameYCoordinate(worldY);

		if (GeometryUtils.isPointInsideMesh(contorno, vertices, frameX, frameY))
		{
			if (colorPaleta != color)
			{
				color = colorPaleta;

				anteriores.push(new Accion(colorPaleta));
				siguientes.clear();

				return true;
			}
		}

		return false;
	}

	private boolean anyadirPegatina(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(vertices, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1)
		{
			pegatinas.setPegatina(pegatinaActual, j, tipoPegatinaActual);

			descargarTexturaRectangulo(tipoPegatinaActual);
			pegatinaAnyadida = true;

			anteriores.push(new Accion(pegatinaActual, j, tipoPegatinaActual));
			siguientes.clear();

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TPaintEstado.Pincel)
		{
			return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TPaintEstado.Pincel)
		{
			return guardarPolilinea();
		}

		return false;
	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}

	private boolean guardarPolilinea()
	{
		if (lineaActual != null)
		{
			Polilinea polilinea = new Polilinea(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);

			listaLineas.add(polilinea);
			anteriores.push(new Accion(polilinea));
			siguientes.clear();
			lineaActual = null;

			return true;
		}

		return false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarMano()
	{
		guardarPolilinea();
		estado = TPaintEstado.Mano;
	}

	public void seleccionarPincel()
	{
		guardarPolilinea();
		estado = TPaintEstado.Pincel;
	}

	public void seleccionarCubo()
	{
		guardarPolilinea();
		estado = TPaintEstado.Cubo;
	}

	public void seleccionarColor(int color)
	{
		colorPaleta = color;
	}

	public int getColorPaleta()
	{
		return colorPaleta;
	}

	public void setColorPaleta(int colorPaleta)
	{
		this.colorPaleta = colorPaleta;
	}

	public void seleccionarSize(int pos)
	{
		if (pos == 0)
		{
			sizeLinea = 6;
		}
		else if (pos == 1)
		{
			sizeLinea = 11;
		}
		else if (pos == 2)
		{
			sizeLinea = 16;
		}
	}

	public void seleccionarPegatina(int pegatina, TTipoSticker tipo)
	{
		guardarPolilinea();

		pegatinaActual = pegatina;
		tipoPegatinaActual = tipo;
		estado = TPaintEstado.Pegatinas;
	}

	public void seleccionarCaptura() 
{
		guardarPolilinea();

		estado = TPaintEstado.Captura;
		estadoCaptura = TCapturaEstado.Capturando;
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
		color = Color.WHITE;
		listaLineas = new ArrayList<Polilinea>();
		pegatinas = new Pegatinas();

		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			descargarTexturaRectangulo(tipoPegatinas[i]);
		}

		Iterator<Accion> it = pila.iterator();
		while (it.hasNext())
		{
			Accion accion = it.next();
			if (accion.isTipoColor())
			{
				color = accion.getColor();
			}
			else if (accion.isTipoPolilinea())
			{
				listaLineas.add(accion.getLinea());
			}
			else if (accion.isTipoPegatina())
			{
				pegatinas.setPegatina(accion.getIndicePegatina(), accion.getVerticePegatina(), accion.getTipoPegatina());
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
			estado = TPaintEstado.Nada;

			return true;
		}

		return false;
	}

	public boolean isEstadoPincel()
	{
		return estado == TPaintEstado.Pincel;
	}

	public boolean isEstadoCubo()
	{
		return estado == TPaintEstado.Cubo;
	}

	public boolean isEstadoMover()
	{
		return estado == TPaintEstado.Mano;
	}

	public boolean isEstadoPegatinas()
	{
		return estado == TPaintEstado.Pegatinas;
	}

	public Textura getTextura()
	{
		if (estadoCaptura == TCapturaEstado.Capturando)
		{
			while (estadoCaptura != TCapturaEstado.Terminado);

			estado = TPaintEstado.Nada;
			estadoCaptura = TCapturaEstado.Nada;

			return new Textura(textura, coordsTextura, pegatinas);
		}

		return null;
	}

	/* Métodos de Guardado de Información */

	public PaintDataSaved saveData()
	{
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
