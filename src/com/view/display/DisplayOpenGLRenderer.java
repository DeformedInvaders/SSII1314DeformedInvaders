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
import com.project.data.Textura;
import com.project.main.OpenGLRenderer;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private boolean personajeCargado;
	
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	private int posTextura;
	
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
		nombreTextura = new int[numeroTexturas];
		bitmap = textura.getTextura().getBitmap();
		coords = textura.getCoordTextura();

		bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		super.onSurfaceCreated(gl, config);
		
		if(personajeCargado)
		{
			// Textura
			cargarTextura(gl, bitmap, nombreTextura, 0);
		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
			
		if(personajeCargado)
		{
			// Textura
			dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTextura, posTextura);
				
			// Contorno
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		}
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	public void onMultiTouchEvent() { }

}
