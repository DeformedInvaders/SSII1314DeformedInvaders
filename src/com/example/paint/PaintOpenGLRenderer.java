package com.example.paint;

import javax.microedition.khronos.opengles.GL10;

import com.example.data.Polilinea;
import com.example.data.Punto;
import com.example.main.OpenGLRenderer;

public class PaintOpenGLRenderer extends OpenGLRenderer {
	
	private Polilinea polilinea;
	
	public PaintOpenGLRenderer() {
        super();
        
        polilinea = new Polilinea();
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{			
		super.onDrawFrame(gl);
		
		polilinea.dibujar(gl);
	}

	
	public void anyadirPunto(float x, float y, float width, float height)
	{	
		// Conversión Pixel - Punto	
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		this.polilinea.anyadirPunto(new Punto(nx, ny));
	}

}
