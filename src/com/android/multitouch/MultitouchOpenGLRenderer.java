package com.android.multitouch;

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
		this.dibujarListaHandleMultitouch(gl, objetoHandle.getBuffer(), handles);
	}
	
	/* Métodos abstractos de OpenGLRenderer */
	
	public void reiniciar() { }
	
	public void onTouchDown(float x, float y, float width, float height) { }
	
	public void onTouchMove(float x, float y, float width, float height) { }
	
	public void onTouchUp(float x, float y, float width, float height) { }
	
	public void onTouchDown(float x, float y, float width, float height, int pos)
	{		
		float nx = xLeft + (xRight-xLeft)*x/width;
		float ny = yBot + (yTop-yBot)*(height-y)/height;
		
		handles.set(3*pos, 1);
		handles.set(3*pos+1, nx);
		handles.set(3*pos+2, ny);
	}
	
	public void onTouchMove(float x, float y, float width, float height, int pos)
	{	
		if(handles.get(3*pos) == 1)
		{
			float nx = xLeft + (xRight-xLeft)*x/width;
			float ny = yBot + (yTop-yBot)*(height-y)/height;
			
			handles.set(3*pos+1, nx);
			handles.set(3*pos+2, ny);
		}
		else 
		{
			onTouchDown(x, y, width, height, pos);
		}
	}
	
	public void onTouchUp(float x, float y, float width, float height, int pos)
	{	
		if(handles.get(3*pos) == 1)
		{
			onTouchMove(x, y, width, height, pos);
			
			handles.set(3*pos, 0);
		}	
	}
}
