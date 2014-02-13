package com.view.display;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;
import com.project.data.Esqueleto;
import com.project.data.Pegatinas;
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private boolean personajeCargado;
	
	// Pegatinas
	private Pegatinas pegatinas;
	private FloatBuffer coordPegatina;
	private FloatBuffer puntosOjos, puntosBoca, puntosArma;
	private boolean pegatinaOjosCargada, pegatinaBocaCargada, pegatinaArmaCargada;
		
	// Esqueleto	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	// Textura
	private Bitmap bitmap;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	public DisplayOpenGLRenderer(Context context)
	{
		super(context);
		
		personajeCargado = false;
	}
	
	public DisplayOpenGLRenderer(Context context, Esqueleto esqueleto, Textura textura)
	{
        super(context);
        
        personajeCargado = true;
        
        // Esqueleto
        contorno = esqueleto.getContorno();
		vertices = esqueleto.getVertices();
		
		triangulos = esqueleto.getTriangulos();
		
		bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
		
		// Textura
		bitmap = textura.getTextura().getBitmap();
		coords = textura.getCoordTextura();

		bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
		
		// Pegatinas
		float texture[] = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f };
        
        pegatinas = textura.getPegatinas();
        coordPegatina = construirBufferListaPuntos(texture);
        
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		if(personajeCargado)
		{
			// Textura
			cargarTextura(gl, bitmap, nombreTexturas, 0);

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
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
			
		if(personajeCargado)
		{
			// Textura
			dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTexturas, 0);
				
			// Contorno
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
			
			// Pegatinas
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
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onMultiTouchEvent() { }
	
	public void saveData()
	{
        pegatinaOjosCargada = false;
        pegatinaBocaCargada = false;
        pegatinaArmaCargada = false;
	}
}
