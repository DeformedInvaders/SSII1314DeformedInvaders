package com.example.animation;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;

import com.example.main.Esqueleto;
import com.example.main.GLESUtils;
import com.example.main.OpenGLRenderer;
import com.example.utils.FloatArray;

public class JumpOpenGLRenderer extends OpenGLRenderer{
	
	/* Esqueleto */	
	private FloatArray hull;
	private FloatBuffer bufferHull;
	
	public JumpOpenGLRenderer(Context context)
	{        
		super(context);
		
	}

	public void setEsqueleto(Esqueleto esqueleto) {
		this.hull = esqueleto.getHull();
		this.bufferHull = GLESUtils.construirBuffer(hull);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		super.onDrawFrame(gl);
		
		if(hull != null) 
		{
			GLESUtils.dibujarBuffer(gl, GL10.GL_LINE_LOOP, 3.0f, Color.BLACK, bufferHull);
		}
	}
}
