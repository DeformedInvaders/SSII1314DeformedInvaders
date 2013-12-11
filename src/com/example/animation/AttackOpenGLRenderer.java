package com.example.animation;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.OpenGLRenderer;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class AttackOpenGLRenderer extends OpenGLRenderer
{
	/* Esqueleto */	
	private ShortArray contorno;
	private FloatArray vertices;
	private FloatBuffer bufferContorno;
	
	public AttackOpenGLRenderer(Context context)
	{        
		super(context);
		
	}

	public void setEsqueleto(Esqueleto esqueleto)
	{
		this.contorno = esqueleto.getContorno();
		this.vertices = esqueleto.getVertices();
		this.bufferContorno = construirBufferListaIndicePuntos(contorno, vertices);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		if(bufferContorno != null) 
		{
			dibujarBuffer(gl, GL10.GL_LINE_LOOP, SIZELINE, Color.BLACK, bufferContorno);
		}
	}

	@Override
	public void onTouchDown(float x, float y, float width, float height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchMove(float x, float y, float width, float height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTouchUp(float x, float y, float width, float height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reiniciar() {
		// TODO Auto-generated method stub
		
	}

}
