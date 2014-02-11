package com.test.multitouch;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.create.deform.Handle;
import com.lib.utils.FloatArray;
import com.project.main.OpenGLRenderer;

public class MultitouchOpenGLRenderer extends OpenGLRenderer
{
	private FloatArray handles;
	private Handle objetoHandle;
	
	private final int NUM_HANDLES;
	
	public MultitouchOpenGLRenderer(Context context, int num_handles)
	{
        super(context); 
        
        NUM_HANDLES = num_handles;
        
        handles = new FloatArray();
        for(int i = 0; i < 3*NUM_HANDLES; i++) handles.add(0);

        objetoHandle = new Handle(20, 5*POINTWIDTH);
	}
	
	/* Métodos de la interfaz Renderer */
	
	@Override
	public void onDrawFrame(GL10 gl)
	{					
		super.onDrawFrame(gl);
		
		// Handles		
		dibujarListaHandleMultitouch(gl, objetoHandle.getBuffer(), handles);
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{		
		float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
		float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
		
		handles.set(3*pointer, 1);
		handles.set(3*pointer+1, worldX);
		handles.set(3*pointer+2, worldY);
	}
	
	public void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(handles.get(3*pointer) == 1)
		{
			float worldX = convertToWorldXCoordinate(pixelX, screenWidth);
			float worldY = convertToWorldYCoordinate(pixelY, screenHeight);
			
			handles.set(3*pointer+1, worldX);
			handles.set(3*pointer+2, worldY);
		}
		else 
		{
			onTouchDown(pixelX, pixelY, screenWidth, screenHeight, pointer);
		}
	}
	
	public void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{	
		if(handles.get(3*pointer) == 1)
		{
			onTouchMove(pixelX, pixelY, screenWidth, screenHeight, pointer);
			
			handles.set(3*pointer, 0);
		}	
	}
	
	public void onMultiTouchEvent() { }
}
