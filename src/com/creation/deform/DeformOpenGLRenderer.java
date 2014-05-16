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
import com.android.opengl.TTipoFondoRenderer;
import com.android.opengl.TTipoTexturasRenderer;
import com.android.storage.ExternalStorageManager;
import com.creation.data.Handle;
import com.creation.data.Pegatinas;
import com.creation.data.Textura;
import com.game.data.Personaje;
import com.game.data.TTipoEntidad;
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
	private TEstadoDeform estado;
	private boolean modoGrabar;

	/* Movimientos */

	// Información de Movimiento
	private List<HandleArray> listaHandlesAnimacion;
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

	// Indice de Vertices que forman Triángulos
	private TriangleArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	// Buscador de Triángulos
	private TriangleQuadTreeSearcher buscador;

	/* Handles */

	// Coordenadas de Handles
	private HandleArray handles;
	private short[] punteros;
	private int numPunteros;
	
	private Handle objetoHandle, objetoHandleSeleccionado;

	/* Textura */
	private Textura textura;
	private FloatBuffer coordsTextura;
	
	// Pegatinas
	private Pegatinas pegatinas;

	/* Constructora */

	public DeformOpenGLRenderer(Context context, int color, OnDeformListener listener, Personaje personaje)
	{
		super(context, TTipoFondoRenderer.Nada, TTipoTexturasRenderer.Personaje, color);
		
		mListener = listener;
		estado = TEstadoDeform.Nada;
		modoGrabar = false;
		
		listaHandlesAnimacion = new ArrayList<HandleArray>();
		listaVerticesAnimacion = new ArrayList<VertexArray>();

		// Esqueleto
		contorno = personaje.getEsqueleto().getContorno();
		vertices = personaje.getEsqueleto().getVertices();
		verticesModificados = vertices.clone();
		triangulos = personaje.getEsqueleto().getTriangulos();

		bufferContorno = BufferManager.construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = BufferManager.construirBufferListaTriangulosRellenos(triangulos, vertices);

		// Handles
		handles = new HandleArray();
		punteros = new short[GamePreferences.NUM_HANDLES];
		numPunteros = 0;
		
		reiniciarHandles();
		
		// Textura
		textura = personaje.getTextura();
		coordsTextura = BufferManager.construirBufferListaTriangulosRellenos(triangulos, textura.getCoordTextura());
		pegatinas = textura.getPegatinas();

		objetoHandle = new Handle(20, GamePreferences.POINT_WIDTH, Color.BLACK);
		objetoHandleSeleccionado = new Handle(20, 2 * GamePreferences.POINT_WIDTH, Color.RED);

		// Deformador
		//deformator = new Deformator(verticesModificados, triangulos, handles);
	}

	/* Métodos Renderer */

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);

		// Textura
		textura.cargarTextura(gl, this, mContext, TTipoEntidad.Personaje, 0);

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
			dibujarMarcoInterior(gl, Color.LTGRAY, GamePreferences.DEEP_INSIDE_FRAMES);
			
			// Centrado de Marco
			centrarPersonajeEnMarcoInicio(gl);

			dibujarPersonaje(gl, bufferTriangulos, bufferContorno, verticesModificados);

			// Handles
			OpenGLManager.dibujarListaHandle(gl, objetoHandle, objetoHandleSeleccionado, handles);

			// Centrado de Marco
			centrarPersonajeEnMarcoFinal(gl);
		}
	}

	public void dibujarPersonaje(GL10 gl, FloatBuffer malla, FloatBuffer contorno, VertexArray vertices)
	{
		// Textura
		textura.dibujar(gl, this, malla, coordsTextura, TTipoEntidad.Personaje, 0);

		// Contorno
		OpenGLManager.dibujarBuffer(gl, GL10.GL_LINE_LOOP, GamePreferences.SIZE_LINE, Color.BLACK, contorno);

		// Pegatinas
		pegatinas.dibujar(gl, this, vertices, triangulos, TTipoEntidad.Personaje, 0);
	}

	/* Métodos de Selección de Estado */

	public void seleccionarAnyadir()
	{		
		estado = TEstadoDeform.Anyadir;
		
		if (deformator == null)
		{
			deformator = new Deformator(verticesModificados, triangulos, handles);
		}
		
		if (buscador == null)
		{
			buscador = new TriangleQuadTreeSearcher(triangulos, verticesModificados, 0.0f, 0.0f, marcoAnchuraInterior, marcoAnchuraInterior);
		}
	}

	public void seleccionarEliminar()
	{
		estado = TEstadoDeform.Eliminar;
	}

	public void seleccionarMover()
	{
		estado = TEstadoDeform.Deformar;
		
		buscador = null;
	}

	/* Métodos Abstractos de OpenGLRenderer */

	@Override
	protected boolean reiniciar()
	{
		estado = TEstadoDeform.Nada;
		modoGrabar = false;
		
		buscador = null;

		handles.clear();

		verticesModificados = vertices.clone();
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);

		listaHandlesAnimacion.clear();
		listaVerticesAnimacion.clear();

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
		float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
		float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
		
		short triangle = buscador.searchTriangle(frameX, frameY);
		if (triangle != -1)
		{			
			handles.addHandle(frameX, frameY, triangle, verticesModificados, triangulos);
			
			// Añadir Handle Nuevo
			deformator.anyadirHandles(handles);
			
			return true;
		}

		return false;
	}
	
	private short buscarHandle(HandleArray handles, VertexArray vertices, TriangleArray triangulos, float pixelX, float pixelY, float screenWidth, float screenHeight)
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

	private boolean eliminarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		short handle = buscarHandle(handles, vertices, triangulos, pixelX, pixelY, screenWidth, screenHeight);
		if (handle != -1)
		{
			handles.removeHandle(handle);
			
			// Eliminar Handle
			deformator.eliminarHandles(handles);
			
			return true;
		}

		return false;
	}

	private boolean seleccionarHandle(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		short handle = buscarHandle(handles, vertices, triangulos, pixelX, pixelY, screenWidth, screenHeight);
		if (handle != -1)
		{
			if (!handles.isSelectedHandle(handle))
			{
				handles.setSelectedHandle(handle, true);
				punteros[pointer] = handle;
				numPunteros++;
	
				if (modoGrabar)
				{
					listaHandlesAnimacion.add(handles.clone());
				}
				
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDeform.Deformar)
		{
			// Handle sin Pulsar
			if (punteros[pointer] == -1 || !handles.isSelectedHandle(punteros[pointer]))
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
		float lastFrameX = handles.getXCoordHandle((short) pointer);
		float lastFrameY = handles.getYCoordHandle((short) pointer);

		float lastPixelX = convertFrameXToPixelXCoordinate(lastFrameX, screenWidth);
		float lastPixelY = convertFrameYToPixelYCoordinate(lastFrameY, screenHeight);

		if (Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > 3 * GamePreferences.MAX_DISTANCE_PIXELS)
		{
			float frameX = convertPixelXToFrameXCoordinate(pixelX, screenWidth);
			float frameY = convertPixelYToFrameYCoordinate(pixelY, screenHeight);
			
			if (!isPuntoFueraMarco(frameX, frameY))
			{
				handles.setCoordsHandle(punteros[pointer], frameX, frameY);
				return true;
			}
		}

		return false;
	}
	
	@Override
	protected boolean onTouchPointerUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDeform.Deformar)
		{
			handles.setSelectedHandle(punteros[pointer], true);
			
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}

		return false;
	}

	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (estado == TEstadoDeform.Deformar && punteros[pointer] != -1)
		{
			handles.setSelectedHandle(punteros[pointer], true);
			
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			handles.setSelectedHandle(punteros[pointer], false);
			
			// Reiniciar punteros
			for (short i = 0; i < GamePreferences.NUM_HANDLES; i++)
			{
				punteros[i] = -1;
			}

			if (modoGrabar && listaHandlesAnimacion.size() > 0)
			{
				modoGrabar = false;
				
				final ProgressDialog alert = ProgressDialog.show(mContext, mContext.getString(R.string.text_processing_character_title), mContext.getString(R.string.text_processing_character_description), true);

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run()
					{
						construirListadeMovimientos();
						estado = TEstadoDeform.Nada;
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
		if (estado == TEstadoDeform.Deformar)
		{
			if (numPunteros > countPointer)
			{
				numPunteros = 0;
				
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
		if (estado == TEstadoDeform.Deformar)
		{
			if (modoGrabar)
			{
				listaHandlesAnimacion.add(handles.clone());
			}
			
			// Cambiar Posicion de los Handles
			deformator.moverHandles(handles, verticesModificados);

			BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
			BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);

			return true;
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
		HandleArray lastHandles = null;
		
		int i = 0;
		while (i < listaHandlesAnimacion.size())
		{
			if (lastHandles != null)
			{
				for(int j = 0; j < numFramesRepetir - 1; j++)
				{
					HandleArray handleInterpolado = lastHandles.interpolar(listaHandlesAnimacion.get(i), j / numFramesRepetir);
					
					deformator.moverHandles(handleInterpolado, frame);
					listaVerticesAnimacion.add(frame.clone());
				}
			}
			
			deformator.moverHandles(listaHandlesAnimacion.get(i), frame);
			listaVerticesAnimacion.add(frame.clone());
			lastHandles = listaHandlesAnimacion.get(i);
			
			i = i + numFramesDescartar;
		}
		
		//TODO Comprobar comportamiento del algoritmo.
		if (GamePreferences.IS_DEBUG_ENABLED())
		{
			ExternalStorageManager.writeLogcat("TEST", "NUM FRAMES INICIAL " + listaHandlesAnimacion.size());
			ExternalStorageManager.writeLogcat("TEST", "NUM FRAMES A DESCARTAR " + numFramesDescartar);
			ExternalStorageManager.writeLogcat("TEST", "NUM FRAMES A REPETIR " + numFramesRepetir);
			ExternalStorageManager.writeLogcat("TEST", "NUM FRAMES FINAL " + listaVerticesAnimacion.size());
		}
		
		mListener.onAnimationFinished();
	}
	
	private void reiniciarHandles()
	{
		for (short i = 0; i < handles.getNumHandles(); i++)
		{
			handles.setSelectedHandle(i, false);
			handles.setCoordsHandle(i, verticesModificados, triangulos);
		}
		
		for (short i = 0; i < GamePreferences.NUM_HANDLES; i++)
		{
			punteros[i] = -1;
		}
	}

	/* Métodos de Selección de Estado */

	public void seleccionarGrabado()
	{
		modoGrabar = true;
		estado = TEstadoDeform.Deformar;

		verticesModificados = vertices.clone();
		
		listaHandlesAnimacion.clear();
		listaVerticesAnimacion.clear();

		reiniciarHandles();

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
		return handles.getNumHandles() == 0;
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
		return listaVerticesAnimacion.size() > 0;
	}

	/* Métodos de Guardado de Información */

	public DeformDataSaved saveData()
	{
		// Textura
		textura.descargarTextura(this, TTipoEntidad.Personaje, 0);
		
		// Pegatinas
		pegatinas.descargarTextura(this, TTipoEntidad.Personaje, 0);

		return new DeformDataSaved(handles, verticesModificados, estado, listaVerticesAnimacion);
	}

	public void restoreData(DeformDataSaved data)
	{
		modoGrabar = false;
		estado = data.getEstado();
		handles = data.getHandles();
		verticesModificados = data.getVerticesModificados();
		listaVerticesAnimacion = data.getListaVertices();
		
		if (deformator == null)
		{
			deformator = new Deformator(verticesModificados, triangulos, handles);
		}
		else if (handles.getNumHandles() > 0)
		{
			deformator.anyadirHandles(handles);
		}
		
		BufferManager.actualizarBufferListaTriangulosRellenos(bufferTriangulos, triangulos, verticesModificados);
		BufferManager.actualizarBufferListaIndicePuntos(bufferContorno, contorno, verticesModificados);
	}
}
