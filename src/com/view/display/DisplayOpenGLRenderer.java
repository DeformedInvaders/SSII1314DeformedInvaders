package com.view.display;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.data.Esqueleto;
import com.example.data.Textura;
import com.example.main.OpenGLRenderer;
import com.lib.utils.FloatArray;
import com.lib.utils.ShortArray;

public class DisplayOpenGLRenderer extends OpenGLRenderer
{
	private ShortArray contorno;
	private FloatBuffer bufferContorno;
	
	private FloatArray vertices;
	
	private ShortArray triangulos;
	private FloatBuffer bufferTriangulos;
	
	private static final int numeroTexturas = 1;
	private int[] nombreTextura;
	private int posTextura;
	
	private Bitmap textura;
	private FloatArray coords;
	private FloatBuffer bufferCoords;
	
	public DisplayOpenGLRenderer(Context context)
	{
        super(context);
        
        nombreTextura = new int[numeroTexturas];
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
			
		if(textura != null)
		{	
			// Textura
			cargarTextura(gl, textura, nombreTextura, 0);
			dibujarTextura(gl, bufferTriangulos, bufferCoords, nombreTextura, posTextura);
			
			// Contorno
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		}
	}
	
	public void setEsqueleto(Esqueleto esqueleto, Textura textura)
	{
		this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		
		this.triangulos = esqueleto.getTriangulos();
		
		this.textura = textura.getTextura().getBitmap();
		this.coords = textura.getCoordTextura();

		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
		this.bufferTriangulos = construirBufferListaTriangulosRellenos(triangulos, vertices);
		this.bufferCoords = construirBufferListaTriangulosRellenos(triangulos, coords);
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float x, float y, float width, float height) { }
	
	public void onTouchMove(float x, float y, float width, float height) { }
	
	public void onTouchUp(float x, float y, float width, float height) { }

}
