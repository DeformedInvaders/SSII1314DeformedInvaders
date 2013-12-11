package com.example.design;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.example.main.Esqueleto;

public class DesignGLSurfaceView extends GLSurfaceView
{
	// Renderer
    private final DesignOpenGLRenderer renderer;
 
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);
        
        // Asignar Renderer al GLSurfaceView
        renderer = new DesignOpenGLRenderer(context);
        setRenderer(renderer);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
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
				//renderer.onTouchUp(x, y, width, height);
			break;
		}
		
		requestRender();		
		return true;
	}
	
	public void calcularBSpline()
	{
		renderer.bSpline();
		requestRender();
	}
	
	public void calcularConvexHull()
	{
		renderer.convexHull();
		requestRender();
	}
	
	public void calcularDelaunay()
	{
		renderer.delaunay();
		requestRender();
	}
	
	public void calcularEarClipping()
	{
		renderer.earClipping();
		requestRender();
	}
	
	public void calcularMeshTriangles()
	{
		renderer.meshGenerator();
		requestRender();
	}
	
	public boolean calcularTestSimple()
	{
		boolean b = renderer.testSimple();
		requestRender();
		return b;
	}
	
	public boolean pruebaCompleta()
	{
		Esqueleto e = renderer.getEsqueleto();
		requestRender();
		return e != null;
	}
	
	public Esqueleto getEsqueleto()
	{
		return renderer.getEsqueleto();
	}
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	public void restore()
	{		
		renderer.restore();
		requestRender();
	}
}
