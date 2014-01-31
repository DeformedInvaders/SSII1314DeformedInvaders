package com.create.deform;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.data.Esqueleto;
import com.example.data.Textura;

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
			int action = event.getAction();
			
			float x = event.getX();
			float y = event.getY();
			
			float width = getWidth();
			float height = getHeight();
			
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
	
			// Recolector de Basura
			// TODO
			System.gc();
			
			requestRender();
		}
		return true;
	}
	
	public void setEsqueleto(Esqueleto esqueleto, Textura textura)
	{
		renderer.setEsqueleto(esqueleto, textura);
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
}
