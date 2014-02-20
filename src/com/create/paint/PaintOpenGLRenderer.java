package com.create.paint;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.lib.math.GeometryUtils;
import com.lib.math.Intersector;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Accion;
import com.project.data.Esqueleto;
import com.project.data.Handle;
import com.project.data.MapaBits;
import com.project.data.Pegatinas;
import com.project.data.Polilinea;
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class PaintOpenGLRenderer extends OpenGLRenderer
{		
	// Estructura de Datos
	private TPaintEstado estado;
	
	private int colorPaleta;
	private int sizeLinea;
	private int pegatinaActual;
	private int tipoPegatinaActual;

	// Detalles
	private List<Polilinea> listaLineas;
	private FloatArray lineaActual;
	private FloatBuffer bufferLineaActual;
	
	// Pegatinas
	private Pegatinas pegatinas;
	private FloatBuffer coordPegatina;
	private FloatBuffer puntosOjos, puntosBoca, puntosArma;
	
	private boolean pegatinaAnyadida;
	private boolean pegatinaOjosCargada, pegatinaBocaCargada, pegatinaArmaCargada;
	
	// Esqueleto
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	private FloatBuffer bufferVertices;
	
	private ShortArray triangulos;
	
	private int color;
	
	// Texturas
	private int canvasHeight, canvasWidth;
	
	private boolean modoCaptura;
	private boolean capturaTerminada;
	
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
		
		bufferVertices = construirBufferListaTriangulosRellenos(triangulos, vertices);
		bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
        
        listaLineas = new ArrayList<Polilinea>();
        lineaActual = null;
        
		float texture[] = { 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
        
        pegatinas = new Pegatinas();
        coordPegatina = construirBufferListaPuntos(texture);
        
        pegatinaActual = 0;
        pegatinaAnyadida = false;
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
        
        color = Color.WHITE;
        
        colorPaleta = Color.RED;
        sizeLinea = 6;
        
        anteriores = new Stack<Accion>();
        siguientes = new Stack<Accion>();
        
        modoCaptura = false;
        capturaTerminada = false;
        
        objetoVertice = new Handle(20, POINTWIDTH);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		if (modoCaptura)
		{	
			// Guardar posición actual de la Cámara
			salvarCamara();
			
			// Restaurar Cámara posición inicial
			restore();
			
			dibujarEsqueleto(gl);
			
			// Capturar Pantalla
		    textura = capturaPantalla(gl, canvasWidth, canvasHeight);
		    
		    // Construir Textura
			coordsTextura = construirTextura(vertices, textura.getWidth(), textura.getHeight());
			
			// Desactivar Modo Captura
			modoCaptura = false;
			capturaTerminada = true;
			
			// Restaurar posición anterior de la Cámara
			recuperarCamara();
		}
		
		dibujarEsqueleto(gl);
		
		// Contorno
		dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		
		if(estado == TPaintEstado.Pegatinas)
		{
			dibujarListaHandle(gl, Color.BLACK, objetoVertice.getBuffer(), vertices);
		}
		
		// Cargar Pegatinas
		if(!pegatinaOjosCargada && pegatinas.getIndiceOjos() != -1)
		{
			FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceOjos(), nombreTexturas, 1);
			puntosOjos = construirBufferListaPuntos(puntos);
			pegatinaOjosCargada = true;
		}
		
		if(!pegatinaBocaCargada && pegatinas.getIndiceBoca() != -1)
		{
			FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceBoca(), nombreTexturas, 2);
			puntosBoca = construirBufferListaPuntos(puntos);
			pegatinaBocaCargada = true;
		}
		
		if(!pegatinaArmaCargada && pegatinas.getIndiceArma() != -1)
		{
			FloatArray puntos = cargarTextura(gl, pegatinas.getIndiceArma(), nombreTexturas, 3);
			puntosArma = construirBufferListaPuntos(puntos);
			pegatinaArmaCargada = true;
		}
		
		// Dibujar Pegatinas
		if(pegatinaOjosCargada && pegatinas.getIndiceOjos() != -1)
		{
			int indice = pegatinas.getVerticeOjos();
			dibujarPegatina(gl, puntosOjos, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 1);
		}
		
		if(pegatinaBocaCargada && pegatinas.getIndiceBoca() != -1)
		{
			int indice = pegatinas.getVerticeBoca();
			dibujarPegatina(gl, puntosBoca, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 2);
		}
		
		if(pegatinaArmaCargada && pegatinas.getIndiceArma() != -1)
		{
			int indice = pegatinas.getVerticeArma();
			dibujarPegatina(gl, puntosArma, coordPegatina, vertices.get(2*indice), vertices.get(2*indice+1), nombreTexturas, 3);
		}
	}
	
	private void dibujarEsqueleto(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		// Esqueleto
		dibujarBuffer(gl, GL10.GL_TRIANGLES, SIZELINE, color, bufferVertices);
		
		// Detalles
		if(lineaActual != null)
		{
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, sizeLinea, colorPaleta, bufferLineaActual);
		}
		
		Iterator<Polilinea> it = listaLineas.iterator();
		while(it.hasNext())
		{
			Polilinea polilinea = it.next();
			dibujarBuffer(gl, GL10.GL_LINE_STRIP, polilinea.getSize(), polilinea.getColor(), polilinea.getBuffer());
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar()
	{
		lineaActual = null;
		listaLineas.clear();
		
		pegatinas = new Pegatinas();
        pegatinaActual = 0;
        pegatinaAnyadida = false;
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
		
		anteriores.clear();
		siguientes.clear();
		
		estado = TPaintEstado.Nada;
		color = Color.WHITE;
		sizeLinea = 6;
	}
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(estado == TPaintEstado.Pincel)
		{
			anyadirPunto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if(estado == TPaintEstado.Cubo)
		{			
			pintarEsqueleto(pixelX, pixelY, screenWidth, screenHeight);
		}
		else if(estado == TPaintEstado.Pegatinas)
		{
			anyadirPegatina(pixelX, pixelY, screenWidth, screenHeight);
		}
	}
	
	private void anyadirPunto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		if(lineaActual == null)
		{
			lineaActual = new FloatArray();
		}
		
		boolean anyadir = true;
		
		if(lineaActual.size > 0)
		{
			float lastWorldX = lineaActual.get(lineaActual.size-2);
			float lastWorldY = lineaActual.get(lineaActual.size-1);
			
			float lastPixelX = convertToPixelXCoordinate(lastWorldX, screenWidth);
			float lastPixelY = convertToPixelYCoordinate(lastWorldY, screenHeight);
			
			anyadir = Math.abs(Intersector.distancePoints(pixelX, pixelY, lastPixelX, lastPixelY)) > MAX_DISTANCE_PIXELS;
		}
		
		if(anyadir)
		{
			lineaActual.add(worldX);
			lineaActual.add(worldY);
			
			bufferLineaActual = construirBufferListaPuntos(lineaActual);
		}
	}
	
	private void pintarEsqueleto(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Conversión Pixel - Punto	
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
				
		if(GeometryUtils.isPointInsideMesh(contorno, vertices, worldX, worldY))
		{
			if(colorPaleta != color)
			{
				color = colorPaleta;
				
				anteriores.push(new Accion(colorPaleta));
				siguientes.clear();
			}
		}
	}
	
	private void anyadirPegatina(float pixelX, float pixelY, float screenWidth, float screenHeight)
	{
		// Pixel pertenece a los Vértices
		short j = buscarPixel(contorno, vertices, pixelX, pixelY, screenWidth, screenHeight);
		if(j != -1)
		{
			pegatinas.setPegatina(pegatinaActual, j, tipoPegatinaActual);
			
			switch(tipoPegatinaActual)
			{
				case 0:
					pegatinaOjosCargada = false;
				break;
				case 1:
					pegatinaBocaCargada = false;
				break;
				case 2:
					pegatinaArmaCargada = false;
				break;
			}
			
			pegatinaAnyadida = true;
			
			anteriores.push(new Accion(pegatinaActual, j, tipoPegatinaActual));
			siguientes.clear();
		}
	}
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TPaintEstado.Pincel)
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if(estado == TPaintEstado.Pincel)
		{
			guardarPolilinea();
		}		
	}
	
	public void onMultiTouchEvent() {}
	
	private void guardarPolilinea()
	{
		if(lineaActual != null)
		{
			Polilinea polilinea = new Polilinea(colorPaleta, sizeLinea, lineaActual, bufferLineaActual);
			
			listaLineas.add(polilinea);
			anteriores.push(new Accion(polilinea));
			siguientes.clear();
			lineaActual = null;
		}
	}
	
	/* Selección de Estado */
	
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
		if(pos == 0)
		{
			sizeLinea = 6;
		}
		else if(pos == 1)
		{
			sizeLinea = 11;
		}
		else if(pos == 2)
		{
			sizeLinea = 16;
		}
	}
	
	public void seleccionarPegatina(int pegatina, int tipo)
	{
		guardarPolilinea();
		
		pegatinaActual = pegatina;
		tipoPegatinaActual = tipo;
		estado = TPaintEstado.Pegatinas;
	}
	
	public void seleccionarCaptura(int height, int width)
	{
		guardarPolilinea();
		
		canvasHeight = height;
		canvasWidth = width;
		modoCaptura = true;
	}
	
	/* Métodos de modificación de Buffers de estado */

	public void anteriorAccion()
	{
		guardarPolilinea();
		
		if(!anteriores.isEmpty())
		{
			Accion accion = anteriores.pop();
			siguientes.add(accion);
			actualizarEstado(anteriores);
		}
	}

	public void siguienteAccion()
	{
		guardarPolilinea();
		
		if(!siguientes.isEmpty())
		{
			Accion accion = siguientes.lastElement();
			siguientes.remove(siguientes.size()-1);
			anteriores.push(accion);
			actualizarEstado(anteriores);
		}
	}
	
	private void actualizarEstado(Stack<Accion> pila)
	{
		color = Color.WHITE;
		listaLineas = new ArrayList<Polilinea>();
		pegatinas = new Pegatinas();
		pegatinaOjosCargada = false;
		pegatinaBocaCargada = false;
		pegatinaArmaCargada = false;
		
		Iterator<Accion> it = pila.iterator();
		while(it.hasNext())
		{
			Accion accion = it.next();
			if(accion.isTipoColor())
			{
				color = accion.getColor();
			}
			else if(accion.isTipoPolilinea())
			{
				listaLineas.add(accion.getLinea());
			}
			else if(accion.isTipoPegatina())
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
		if(pegatinaAnyadida)
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
		while(!capturaTerminada);
		
		return new Textura(textura, coordsTextura, pegatinas);
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
