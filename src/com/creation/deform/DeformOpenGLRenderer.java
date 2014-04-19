package com.creation.deform;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import com.android.view.OpenGLRenderer;
import com.creation.data.Handle;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.game.data.TTipoEntidad;
import com.lib.math.Intersector;
import com.lib.opengl.BufferManager;
import com.lib.opengl.HullArray;
import com.lib.opengl.TriangleArray;
import com.lib.opengl.VertexArray;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.main.R;
import com.project.model.GamePreferences;

public class DeformOpenGLRenderer extends OpenGLRenderer
{
	private Deformator deformator;

	// Modo Grabado
	private TEstadoDeform estado;
	private boolean modoGrabar;

	/* Movimientos */

	// Informaci�n de Movimiento
	private List<FloatArray> listaHandlesAnimacion;
	private List<VertexArray> listaVerticesAnimacion;

	// Informacion para la reproduccion de la animacion
	private VertexArray verticesAnimacion;
	private FloatBuffer triangulosAnimacion;
	private FloatBuffer contornoAnimacion;
	private int posicionAnimacion;

	/* Esqueleto */

	// Indice de Vertices que forman en ConvexHull
	private HullArray contorno;
	private FloatBuffer bufferContorno;

	// Coordenadas de Vertices
	private VertexArray vertices;
	private VertexArray verticesModificados;

	// Indice de Vertices que forman Tri�ngulos
	private TriangleArray triangulos;
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
	private Textura textura;
	private FloatBuffer coordsTextura;
	
	// Pegatinas
	private Pegatinas pegatinas;

	/* Constructora */

	public DeformOpenGLRenderer(Context context, int color, Personaje personaje)
	{
		super(context, color);

		estado = TEstadoDeform.Nada;
		modoGrabar = false;
		listaHandlesAnimacion = new ArrayList<FloatArray>();

		// Esqueleto
		contorno = personaje.getEsqueleto().getContorno();
		vertices = personaje.getEsqueleto().getVertices();
		verticesModificados = vertices.clone();
		triangulos = personaje.getEsqueleto().getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);

		// Handles
		handles = new FloatArray();
		indiceHandles = new ShortArray();

		handleSeleccionado = new FloatArray();
		reinciarHandlesSeleccionados();

		// Textura
		textura = personaje.getTextura();
		coordsTextura = BufferManager.construirBufferListaTriangulosRellenos(triangulos, textura.getCoordTextura());
		pegatinas = textura.getPegatinas();

		objetoHandle = new Handle(20, GamePreferences.POINT_WIDTH);
		objetoVertice = new Handle(20, GamePreferences.POINT_WIDTH / 2);
		objetoHandleSeleccionado = new Handle(20, 2 * GamePreferences.POINT_WIDTH);

		// Deformador
		deformator = new Deformator(verticesModificados, triangulos, handles, indiceHandles);
	}

	/* M�todos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// Textura
		textura.cargarTextura(gl, this, mContext, TTipoEntidad.Personaje);

		// Pegatinas
		pegatinas.cargarTexturas(gl, this, mContext, TTipoEntidad.Personaje, 0);
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);

		if (estado == TEstadoDeform.Reproducir)
		{
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			dibujarPersonaje(gl, triangulosAnimacion, contornoAnimacion, verticesAnimacion);

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);
		}
		else
		{
			// Marcos
			dibujarMarcoInterior(gl, Color.LTGRAY);
			
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			dibujarPersonaje(gl, bufferTriangulos, bufferContorno, verticesModificados);

			if (estado != TEstadoDeform.Deformar)
			{
				BufferManager.dibujarListaHandle(gl, Color.RED, objetoVertice, verticesModificados);
			}

			// Handles
			if (handles.size > 0)
			{
				BufferManager.dibujarListaHandle(gl, Color.BLACK, objetoHandle, handles);
			}

			// Seleccionado
			if (estado == TEstadoDeform.Deformar)
			{
				BufferManager.dibujarListaIndiceHandle(gl, Color.RED, objetoHandleSeleccionado, handleSeleccionado);
			}

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);
		}
	}

	public void dibujarPersonaje(GL10 gl, FloatBuffer triangulos, FloatBuffer contorno, VertexArray vertices)
	{
		// Textura
		textura.dibujar(gl, this, triangulos, coordsTextura, TTipoEntidad.Personaje);

		// Contorno
		BufferManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, contorno);

		// Pegatinas
		pegatinas.dibujar(gl, this, vertices, TTipoEntidad.Personaje, 0);
	}

	/* M�todos de Selecci�n de Estado */

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

	/* M�todos Abstractos de OpenGLRenderer */

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
		// Pixel pertenece a los V�rtices
		short j = buscarPixel(verticesModificados, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1)
		{
			// V�rtice no pertenece a los Handles
			if (!indiceHandles.contains(j))
			{
				indiceHandles.add(j);
				handles.add(verticesModificados.getXVertex(j));
				handles.add(verticesModificados.getYVertex(j));

				// A�adir Handle Nuevo
				deformator.anyadirHandles(handles, indiceHandles);
			}

			return true;
		}

		return false;
	}

	private boolean eliminarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los V�rtices
		short j = buscarHandle(handles, pixelX, pixelY, screenWidth, screenHeight);
		if (j != -1)
		{
			indiceHandles.removeIndex(j);
			
			handles.removeIndex(2 * j + 1);
			handles.removeIndex(2 * j);
			
			// Eliminar Handle
			deformator.anyadirHandles(handles, indiceHandles);
			
			return true;
		}

		return false;
	}

	private boolean seleccionarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		// Pixel pertenece a los V�rtices
		short j = buscarHandle(handles, pixelX, pixelY, screenWidth, screenHeight);
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
		// Conversi�n Pixel - Punto
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);

		int indiceHandleSeleccionado = (int) handleSeleccionado.get(4 * pointer);
		float lastFrameX = handles.get(2 * indiceHandleSeleccionado);
		float lastFrameY = handles.get(2 * indiceHandleSeleccionado);

		float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
		float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

		if (Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3 * GamePreferences.MAX_DISTANCE_PIXELS)
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

			reinciarHandlesSeleccionados();

			if (modoGrabar && listaHandlesAnimacion.size() > 0)
			{
				modoGrabar = false;
				estado = TEstadoDeform.Nada;
				
				final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_character_title), mContext.getString(R.string.text_processing_character_description), true);

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run()
					{
						construirListadeMovimientos();
						alert.dismiss();
					}
				});
				
				thread.start();
				return true;
			}
		}

		return false;
	}

	private void construirListadeMovimientos()
	{		
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

		VertexArray frame = vertices.clone();
		
		int i = 0;
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
		
		//FIXME Comprobar comportamiento de algoritmo.
		android.util.Log.d("TEST", "NUM FRAMES INICIAL " + listaHandlesAnimacion.size());
		android.util.Log.d("TEST", "NUM FRAMES A DESCARTAR " + numFramesDescartar);
		android.util.Log.d("TEST", "NUM FRAMES A REPETIR " + numFramesRepetir);
		android.util.Log.d("TEST", "NUM FRAMES FINAL " + listaVerticesAnimacion.size());
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

	/* M�todos de Selecci�n de Estado */

	public void seleccionarGrabado()
	{
		modoGrabar = true;
		estado = TEstadoDeform.Deformar;

		listaHandlesAnimacion.clear();
		listaVerticesAnimacion = new ArrayList<VertexArray>();

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

			float x = vertices.getXVertex(pos);
			float y = vertices.getYVertex(pos);

			handles.set(2 * i, x);
			handles.set(2 * i + 1, y);
		}
	}

	private void reinciarHandlesSeleccionados()
	{
		handleSeleccionado.clear();
		
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

	/* M�todos de Obtenci�n de Informaci�n */

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

	public List<VertexArray> getMovimientos()
	{
		return listaVerticesAnimacion;
	}

	public boolean isGrabacionReady()
	{
		return listaVerticesAnimacion != null && listaVerticesAnimacion.size() > 0;
	}

	/* M�todos de Guardado de Informaci�n */

	public DeformDataSaved saveData()
	{
		// Textura
		textura.descargarTextura(this, TTipoEntidad.Personaje);
		
		// Pegatinas
		pegatinas.descargarTextura(this, TTipoEntidad.Personaje, 0);

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
