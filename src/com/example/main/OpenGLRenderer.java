package com.example.main;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

public class OpenGLRenderer implements Renderer
{	
	// Parámetros de la Cámara
	public float xLeft, xRight, yTop, yBot, xCentro, yCentro;
	
	// Parámetros del Puerto de Vista
	public int height = 1280;
	public int width = 760;
	
	public OpenGLRenderer()
	{
        xRight = width;
        xLeft = 0.0f;
        yTop = height;
        yBot = 0.0f;
        xCentro = (xRight + xLeft)/2.0f;
        yCentro = (yTop + yBot)/2.0f;
	}
	
	public void zoom(float factor)
	{	
		float newAncho = (xRight-xLeft)*factor;
		float newAlto = (yTop-yBot)*factor;
		
		this.xRight = xCentro + newAncho/2.0f;
		this.xLeft = xCentro - newAncho/2.0f;
		this.yTop = yCentro + newAlto/2.0f;
		this.yBot = yCentro - newAlto/2.0f;
	}
	
	public void drag(float width, float height, float dx, float dy)
	{	
		/*
		this.xLeft += dx * width;
		this.xRight += dx * width;
		this.yBot += dy * height;
		this.yTop += dy * height;
		*/
		
		this.xLeft += dx;
		this.xRight += dx;
		this.yBot += dy;
		this.yTop += dy;
		
		this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}
	
	public void restore()
	{
        this.xRight = width; 
        this.xLeft = 0.0f;
        this.yTop = height;
        this.yBot = 0.0f;
        
        this.xCentro = (xRight + xLeft)/2.0f;
        this.yCentro = (yTop + yBot)/2.0f;
	}

	@Override
	public void onDrawFrame(GL10 gl)
	{	
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, xLeft, xRight, yBot, yTop);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		this.xRight = this.xLeft + width;
		this.yTop = this.yBot + height;
		this.width = width;
		this.height = height;
		
		gl.glViewport(0, 0, (int)this.width, (int)this.height); 	//Reset The Current Viewport
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, this.xLeft, this.xRight, this.yBot, this.yTop);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity(); 					//Reset The Modelview Matrix
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); 	//White Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
		GLU.gluOrtho2D(gl, this.xLeft, this.xRight, this.yBot, this.yTop);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
	}
}
