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
	private Deformator deformator;

	// Modo Grabado
	private TEstadoDeform estado;
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

	/* Constructora */

	public DeformOpenGLRenderer(Context context, Esqueleto esqueleto, Textura textura)
	{
		super(context);

		estado = TEstadoDeform.Nada;
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

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// Textura
		cargarTexturaMalla(gl, bitmap);

		// Pegatinas
		for (int i = 0; i < GamePreferences.MAX_TEXTURE_STICKER; i++)
		{
			TTipoSticker tipoPegatinas = TTipoSticker.values()[i];
			
			if (pegatinas.isCargada(tipoPegatinas))
			{
				cargarTexturaRectangulo(gl, pegatinas.getIndice(tipoPegatinas, mContext), tipoPegatinas);
			}
		}
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (estado == TEstadoDeform.Reproducir)
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

			if (estado != TEstadoDeform.Deformar)
			{
				dibujarListaHandle(gl, Color.RED, objetoVertice.getBuffer(), verticesModificados);
			}

			// Handles
			if (handles.size > 0)
			{
				dibujarListaHandle(gl, Color.BLACK, objetoHandle.getBuffer(), handles);
			}

			// Seleccionado
			if (estado == TEstadoDeform.Deformar)
			{
				dibujarListaIndiceHandle(gl, Color.RED, objetoHandleSeleccionado.getBuffer(), handleSeleccionado);
			}

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);

			// Marcos
			dibujarMarcoIncompletoSuave(gl);
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
			TTipoSticker tipoPegatinas = TTipoSticker.values()[i];
			
			if (pegatinas.isCargada(tipoPegatinas))
			{
				int indice = pegatinas.getVertice(tipoPegatinas);
				dibujarTexturaRectangulo(gl, vertices.get(2 * indice), vertices.get(2 * indice + 1), tipoPegatinas);
			}
		}
	}

	/* Métodos de Selección de Estado */

	public void seleccionarAnyadir()
	{
		estado = TEstadoDeform.Anyadir;
	}

	public void seleccionarEliminar()
	{
		estado = TEstadoDeform.Eliminar;
	}

	public void seleccionarMover()
	{
		estado = TEstadoDeform.Deformar;
	}

	/* Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		estado = TEstadoDeform.Nada;
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
		if (estado == TEstadoDeform.Anyadir)
		{
			return anyadirHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TEstadoDeform.Eliminar)
		{
			return eliminarHandle(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if (estado == TEstadoDeform.Deformar)
		{
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
		short j = buscarPixel(handles, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1)
		{
			// Seleccionar Handle
			handleSeleccionado.set(4 * pointer, j);
			handleSeleccionado.set(4 * pointer + 1, 1);
			handleSeleccionado.set(4 * pointer + 2, handles.get(2 * j));
			handleSeleccionado.set(4 * pointer + 3, handles.get(2 * j + 1));

			if (modoGrabar)
			{
				listaHandlesAnimacion.add(handles.clone());
			}
			
			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDeform.Deformar)
		{
			// Handle sin Pulsar
			if (handleSeleccionado.get(4 * pointer + 1) == 0)
			{
				return onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
			}
			else
			{
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
			
			if (modoGrabar)
			{
				listaHandlesAnimacion.add(handles.clone());
			}

			return true;
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDeform.Deformar)
		{
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);

			handleSeleccionado.set(4 * pointer, -1);
			handleSeleccionado.set(4 * pointer + 1, 0);

			if (modoGrabar && listaHandlesAnimacion.size() > 0)
			{
				modoGrabar = false;
				estado = TEstadoDeform.Nada;
				construirListadeMovimientos();

				return true;
			}
		}

		return false;
	}

	private void construirListadeMovimientos()
	{
		int i = 0;

		int numFramesDescartar = 1;
		int numFramesRepetir = 1;

		if (listaHandlesAnimacion.size() >= GamePreferences.NUM_FRAMES_ANIMATION)
		{
			numFramesDescartar = Math.round((float) listaHandlesAnimacion.size() / (float) GamePreferences.NUM_FRAMES_ANIMATION);
		}
		else
		{
			numFramesRepetir = Math.round((float) GamePreferences.NUM_FRAMES_ANIMATION / (float) listaHandlesAnimacion.size());
		}

		FloatArray frame = vertices.clone();

		while (i < listaHandlesAnimacion.size())
		{
			for(int j = 0; j < numFramesRepetir; j++)
			{
				deformator.moverHandles(listaHandlesAnimacion.get(i), frame);
				listaVerticesAnimacion.add(frame.clone());
			}
			
			i = i + numFramesDescartar;
		}

		deformator.moverHandles(listaHandlesAnimacion.get(listaHandlesAnimacion.size() - 1), frame);
		listaVerticesAnimacion.add(frame.clone());
	}

	@Override
	protected boolean onMultiTouchEvent()
	{
		if (estado == TEstadoDeform.Deformar)
		{
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);

			BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);

			return true;
		}

		return false;
	}

	/* Métodos de Selección de Estado */

	public void seleccionarGrabado()
	{
		modoGrabar = true;
		estado = TEstadoDeform.Deformar;

		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = new ArrayList<FloatArray>();

		reiniciarHandles();

		verticesModificados = vertices.clone();
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}

	public void selecionarPlay()
	{
		estado = TEstadoDeform.Reproducir;

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
		estado = TEstadoDeform.Audio;
	}

	public void seleccionarReposo()
	{
		estado = TEstadoDeform.Nada;
	}

	/* Métodos de Obtención de Información */

	public boolean isHandlesVacio()
	{
		return indiceHandles.size == 0;
	}

	public boolean isEstadoAnyadir()
	{
		return estado == TEstadoDeform.Anyadir;
	}

	public boolean isEstadoEliminar()
	{
		return estado == TEstadoDeform.Eliminar;
	}

	public boolean isEstadoDeformar()
	{
		return estado == TEstadoDeform.Deformar;
	}

	public boolean isEstadoGrabacion()
	{
		return estado == TEstadoDeform.Deformar && modoGrabar;
	}

	public boolean isEstadoAudio()
	{
		return estado == TEstadoDeform.Audio;
	}

	public boolean isEstadoReproduccion()
	{
		return estado == TEstadoDeform.Reproducir;
	}

	public List<FloatArray> getMovimientos()
	{
		return listaVerticesAnimacion;
	}

	public boolean isGrabacionReady()
	{
		return listaVerticesAnimacion != null && listaVerticesAnimacion.size() > 0;
	}

	/* Métodos de Guardado de Información */

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
