package com.create.deform;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.project.data.Esqueleto;
import com.project.data.Textura;

public class DeformGLSurfaceView extends GLSurfaceView
{
	// Renderer
    private final DeformOpenGLRenderer renderer;

    public DeformGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);

        // Asignar Renderer al GLSurfaceView
        renderer = new DeformOpenGLRenderer(context);
        setRenderer(renderer);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

	public boolean onTouch(MotionEvent event)
	{	
		if(event != null)
		{
			//int pointCount = event.getPointerCount();
			int action = event.getAction();
			
			float width = getWidth();
			float height = getHeight();
			
			//if(pointCount == 1)
			//{
				float x = event.getX();
				float y = event.getY();
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
						renderer.onTouchDown(x, y, width, height);
					break;
					case MotionEvent.ACTION_MOVE:
						renderer.onTouchMove(x, y, width, height);	
					break;
					case MotionEvent.ACTION_UP:
						renderer.onTouchUp(x, y, width, height);
					break;
					default:
						return false;
				}
			//}
			//else
			//{
				/*
				float x1 = event.getX(0);
				float y1 = event.getY(0);
				float x2 = event.getX(1);
				float y2 = event.getY(1);
				
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						renderer.onTouchDown(x1, y1, x2, y2, width, height);
					break;
					case MotionEvent.ACTION_MOVE:
						renderer.onTouchMove(x1, y1, x2, y2, width, height);	
					break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
						renderer.onTouchUp(x1, y1, x2, y2, width, height);
					break;
					default:
						return false;
				}
				*/
			//}
			
			requestRender();
		}
		
		invalidate();
		return true;
	}
	
	public void setParameters(Esqueleto esqueleto, Textura textura)
	{
		renderer.setParameters(esqueleto, textura);
	}

	public void seleccionarAnyadir()
	{
		renderer.seleccionarAnyadir();
	}

	public void seleccionarEliminar()
	{
		renderer.seleccionarEliminar();
	}

	public void seleccionarMover()
	{
		renderer.seleccionarMover();
	}

	public boolean handlesVacio()
	{
		return renderer.handlesVacio();
	}

	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	public DeformDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(DeformDataSaved data)
	{
		renderer.restoreData(data);
	}
}
