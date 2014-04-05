package com.creation.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Esqueleto;
import com.creation.data.Handle;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.game.data.TTipoSticker;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.main.GamePreferences;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private TDeformTipo tipoDeformacion;
	private Deformator deformator;

	private final int NUM_FRAMES;

	// Modo Grabado
	private TDeformEstado estado;
	private boolean modoGrabar;

	/* Movimientos */

	// Información de Movimiento
	private List<FloatArray> listaHandlesAnimacion;
	private List<FloatArray> listaVerticesAnimacion;

	// Informacion para la reproduccion de la animacion
	private FloatArray verticesAnimacion;
	private FloatBuffer triangulosAnimacion;
	private FloatBuffer contornoAnimacion;
	private int posicionAnimacion;

	/* Esqueleto */

	// Indice de Vertices que forman en ConvexHull
	private ShortArray contorno;
	private FloatBuffer bufferContorno;

	// Coordenadas de Vertices
	private FloatArray vertices;
	private FloatArray verticesModificados;

	// Indice de Vertices que forman Triángulos
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;

	/* Handles */

	// Coordenadas de Handles
	private FloatArray handles;

	// Indice Vertice asociado a Handles
	private ShortArray indiceHandles;

	// Coordenadas de Handles Seleccionados
	private FloatArray handleSeleccionado;

	private Handle objetoVertice, objetoHandle, objetoHandleSeleccionado;

	/* Textura */

	private Bitmap bitmap;
	private FloatArray coords;
	private FloatBuffer bufferCoords;

	// Pegatinas
	private Pegatinas pegatinas;

	/* SECTION Constructora */

	public DeformOpenGLRenderer(Context context, Esqueleto esqueleto, Textura textura, TDeformTipo tipo, int numero_frames)
	{
		super(context);

		NUM_FRAMES = numero_frames;

		tipoDeformacion = tipo;
		estado = TDeformEstado.Nada;
		modoGrabar = false;
		listaHandlesAnimacion = new ArrayList<FloatArray>();

		// Esqueleto
		contorno = esqueleto.getContorno();
		vertices = esqueleto.getVertices();
		verticesModificados = vertices.clone();
		triangulos = esqueleto.getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);

		// Handles
		handles = new FloatArray();
		indiceHandles = new ShortArray();

		handleSeleccionado = new FloatArray();
		reinciarHandlesSeleccionados();

		// Textura
		pegatinas = textura.getPegatinas();

		bitmap = textura.getMapaBits().getBitmap();
		coords = textura.getCoordTextura();

		bufferCoords = BufferManager.construirBufferListaTriangulosRellenos(triangulos, coords);

		objetoHandle = new Handle(20, POINTWIDTH);
		objetoVertice = new Handle(20, POINTWIDTH / 2);
		objetoHandleSeleccionado = new Handle(20, 2 * POINTWIDTH);

		// Deformador
		deformator = new Deformator(verticesModificados, triangulos, handles, indiceHandles);
	}

	/* SECTION Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// Textura
		cargarTexturaMalla(gl, bitmap);

		// Pegatinas
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			if (pegatinas.isCargada(i))
			{
				TTipoSticker[] tipoPegatinas = TTipoSticker.values();
				cargarTexturaRectangulo(gl, pegatinas.getIndice(i), tipoPegatinas[i]);
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (estado == TDeformEstado.Reproducir)
		{
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			dibujarPersonaje(gl, triangulosAnimacion, contornoAnimacion, bufferCoords, pegatinas, verticesAnimacion);

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);
		}
		else
		{
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			dibujarPersonaje(gl, bufferTriangulos, bufferContorno, bufferCoords, pegatinas, verticesModificados);

			if (estado != TDeformEstado.Deformar)
			{
				dibujarListaHandle(gl, Color.RED, objetoVertice.getBuffer(), verticesModificados);
			}

			// Handles
			if (handles.size > 0)
			{
				dibujarListaHandle(gl, Color.BLACK, objetoHandle.getBuffer(), handles);
			}

			// Seleccionado
			if (estado == TDeformEstado.Deformar)
			{
				dibujarListaIndiceHandle(gl, Color.RED, objetoHandleSeleccionado.getBuffer(), handleSeleccionado);
			}

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);

			// Marcos
			if (tipoDeformacion == TDeformTipo.Run || tipoDeformacion == TDeformTipo.Attack)
			{
				dibujarMarcoCentral(gl);
			}
			else if (tipoDeformacion == TDeformTipo.Jump)
			{
				dibujarMarcoInferior(gl);
			}
			else if (tipoDeformacion == TDeformTipo.Crouch)
			{
				dibujarMarcoSuperior(gl);
			}

			dibujarMarcoLateral(gl);
		}
	}

	public void dibujarPersonaje(GL10 gl, FloatBuffer triangulos, FloatBuffer contorno, FloatBuffer coordTriangulos, Pegatinas pegatinas, FloatArray vertices)
	{
		// Textura
		dibujarTexturaMalla(gl, triangulos, coordTriangulos);

		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, contorno);

		// Pegatinas
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			if (pegatinas.isCargada(i))
			{
				int indice = pegatinas.getVertice(i);
				TTipoSticker[] tipoPegatinas = TTipoSticker.values();
				dibujarTexturaRectangulo(gl, vertices.get(2 * indice), vertices.get(2 * indice + 1), tipoPegatinas[i]);
			}
		}
	}

	/* SECTION Métodos de Selección de Estado */

	public void seleccionarAnyadir()
	{
		estado = TDeformEstado.Anyadir;
	}

	public void seleccionarEliminar()
	{
		estado = TDeformEstado.Eliminar;
	}

	public void seleccionarMover()
	{
		estado = TDeformEstado.Deformar;
	}

	/* SECTION Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		estado = TDeformEstado.Nada;
		modoGrabar = false;

		handles.clear();
		indiceHandles.clear();
		reinciarHandlesSeleccionados();

		verticesModificados = vertices.clone();
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);

		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = null;

		return true;
	}

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TDeformEstado.Anyadir)
		{
			return anyadirHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TDeformEstado.Eliminar)
		{
			return eliminarHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TDeformEstado.Deformar)
		{
			if (modoGrabar)
			{
				listaHandlesAnimacion.add(handles.clone());
			}

			return seleccionarHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	private boolean anyadirHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1) {
			// Vértice no pertenece a los Handles
			if (!indiceHandles.contains(j))
			{
				indiceHandles.add(j);
				handles.add(verticesModificados.get(2 * j));
				handles.add(verticesModificados.get(2 * j + 1));

				// Añadir Handle Nuevo
				deformator.anyadirHandles(handles, indiceHandles);
			}

			return true;
		}

		return false;
	}

	private boolean eliminarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1) {
			// Vértice no pertenece a los Handles
			if (indiceHandles.contains(j))
			{
				int pos = indiceHandles.indexOf(j);
				indiceHandles.removeIndex(pos);

				handles.removeIndex(2 * pos + 1);
				handles.removeIndex(2 * pos);

				// Eliminar Handle
				deformator.anyadirHandles(handles, indiceHandles);
			}

			return true;
		}

		return false;
	}

	private boolean seleccionarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(handles, pixelX, pixelY, screenWidth,
				screenHeight);
		if (j != -1)
		{
			// Seleccionar Handle
			handleSeleccionado.set(4 * pointer, j);
			handleSeleccionado.set(4 * pointer + 1, 1);
			handleSeleccionado.set(4 * pointer + 2, handles.get(2 * j));
			handleSeleccionado.set(4 * pointer + 3, handles.get(2 * j + 1));

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TDeformEstado.Deformar)
		{
			// Handle sin Pulsar
			if (handleSeleccionado.get(4 * pointer + 1) == 0)
			{
				return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
			else
			{
				if (modoGrabar)
				{
					listaHandlesAnimacion.add(handles.clone());
				}

				return moverHandle(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
		}

		return false;
	}

	private boolean moverHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Conversión Pixel - Punto
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);

		float frameX = convertToFrameXCoordinate(worldX);
		float frameY = convertToFrameYCoordinate(worldY);

		int indiceHandleSeleccionado = (int) handleSeleccionado.get(4 * pointer);
		float lastFrameX = handles.get(2 * indiceHandleSeleccionado);
		float lastFrameY = handles.get(2 * indiceHandleSeleccionado);

		float lastWorldX = convertFromFrameXCoordinate(lastFrameX);
		float lastWorldY = convertFromFrameYCoordinate(lastFrameY);

		float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
		float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);

		if (Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3 * MAX_DISTANCE_PIXELS)
		{
			handles.set(2 * indiceHandleSeleccionado, frameX);
			handles.set(2 * indiceHandleSeleccionado + 1, frameY);

			handleSeleccionado.set(4 * pointer + 2, frameX);
			handleSeleccionado.set(4 * pointer + 3, frameY);

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TDeformEstado.Deformar)
		{
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);

			handleSeleccionado.set(4 * pointer, -1);
			handleSeleccionado.set(4 * pointer + 1, 0);

			if (modoGrabar && listaHandlesAnimacion.size() > 0)
			{
				modoGrabar = false;
				estado = TDeformEstado.Nada;
				construirListadeMovimientos();

				return true;
			}
		}

		return false;
	}

	private void construirListadeMovimientos()
	{
		int i = 0;
		// Entero para controlar cada cuantos movimientos se guarda uno.
		int r = 1;

		if (listaHandlesAnimacion.size() >= NUM_FRAMES)
		{
			r = listaHandlesAnimacion.size() / NUM_FRAMES;
		}

		FloatArray v = vertices.clone();

		while (i < listaHandlesAnimacion.size())
		{
			deformator.moverHandles(listaHandlesAnimacion.get(i), v);

			listaVerticesAnimacion.add(v.clone());
			i = i + r;
		}

		deformator.moverHandles(listaHandlesAnimacion.get(listaHandlesAnimacion.size() - 1), v);
		listaVerticesAnimacion.add(v.clone());

	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		if (estado == TDeformEstado.Deformar)
		{
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);

			BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);

			return true;
		}

		return false;
	}

	/* SECTION Métodos de Selección de Estado */

	public void seleccionarGrabado()
	{
		modoGrabar = true;
		estado = TDeformEstado.Deformar;

		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = new ArrayList<FloatArray>();

		reiniciarHandles();

		verticesModificados = vertices.clone();
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}

	public void selecionarPlay()
	{
		estado = TDeformEstado.Reproducir;

		iniciarAnimacion();
	}

	public void iniciarAnimacion()
	{
		posicionAnimacion = 0;
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		triangulosAnimacion = BufferManager.construirBufferListaTriangulosRellenos(triangulos, verticesAnimacion);
		contornoAnimacion = BufferManager.construirBufferListaIndicePuntos(contorno, verticesAnimacion);
	}

	public boolean reproducirAnimacion()
	{
		verticesAnimacion = listaVerticesAnimacion.get(posicionAnimacion);
		BufferManager.actualizarBufferListaTriangulosRellenos(triangulosAnimacion, triangulos, verticesAnimacion);
		BufferManager.actualizarBufferListaIndicePuntos(contornoAnimacion, contorno, verticesAnimacion);
		posicionAnimacion++;

		return posicionAnimacion == listaVerticesAnimacion.size();
	}

	private void reiniciarHandles()
	{
		// Handles
		for (int i = 0; i < indiceHandles.size; i++)
		{
			short pos = indiceHandles.get(i);

			float x = vertices.get(2 * pos);
			float y = vertices.get(2 * pos + 1);

			handles.set(2 * i, x);
			handles.set(2 * i + 1, y);
		}
	}

	private void reinciarHandlesSeleccionados()
	{
		for (int i = 0; i < GamePreferences.NUM_HANDLES; i++)
		{
			// Indice Handle
			handleSeleccionado.add(-1);
			// Estado Handle
			handleSeleccionado.add(0);
			// Posicion Handle
			handleSeleccionado.add(0);
			handleSeleccionado.add(0);
		}
	}

	public void seleccionarAudio()
	{
		estado = TDeformEstado.Audio;
	}

	public void seleccionarReposo()
	{
		estado = TDeformEstado.Nada;
	}

	/* SECTION Métodos de Obtención de Información */

	public boolean isHandlesVacio()
	{
		return indiceHandles.size == 0;
	}

	public boolean isEstadoAnyadir()
	{
		return estado == TDeformEstado.Anyadir;
	}

	public boolean isEstadoEliminar()
	{
		return estado == TDeformEstado.Eliminar;
	}

	public boolean isEstadoDeformar()
	{
		return estado == TDeformEstado.Deformar;
	}

	public boolean isEstadoGrabacion()
	{
		return estado == TDeformEstado.Deformar && modoGrabar;
	}

	public boolean isEstadoAudio()
	{
		return estado == TDeformEstado.Audio;
	}

	public boolean isEstadoReproduccion()
	{
		return estado == TDeformEstado.Reproducir;
	}

	public List<FloatArray> getMovimientos()
	{
		return listaVerticesAnimacion;
	}

	public boolean isGrabacionReady()
	{
		return listaVerticesAnimacion != null && listaVerticesAnimacion.size() > 0;
	}

	/* SECTION Métodos de Guardado de Información */

	public DeformDataSaved saveData()
	{
		// Textura
		descargarTexturaMalla();

		// Pegatinas
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			TTipoSticker[] tipoPegatinas = TTipoSticker.values();
			descargarTexturaRectangulo(tipoPegatinas[i]);
		}

		return new DeformDataSaved(handles, indiceHandles, verticesModificados, estado, listaVerticesAnimacion);
	}

	public void restoreData(DeformDataSaved data)
	{
		modoGrabar = false;
		estado = data.getEstado();
		handles = data.getHandles();
		indiceHandles = data.getIndiceHandles();
		verticesModificados = data.getVerticesModificados();
		listaVerticesAnimacion = data.getListaVertices();

		deformator.anyadirHandles(handles, indiceHandles);
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
}
