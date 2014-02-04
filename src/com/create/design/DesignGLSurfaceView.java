package com.create.design;

import android.content.Context;
import android.util.AttributeSet;

import com.project.data.Esqueleto;
import com.project.main.OpenGLSurfaceView;
import com.project.main.TTouchEstado;

public class DesignGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private final DesignOpenGLRenderer renderer;
 
    public DesignGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);

        // Asignar Renderer al GLSurfaceView
        renderer = new DesignOpenGLRenderer(context);
        setRenderer(renderer);
    }
	
	/*public void calcularBSpline()
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
	}*/
	
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
	
	/*public void restore()
	{		
		renderer.restore();
		requestRender();
	}*/

	public boolean poligonoCompleto()
	{
		return renderer.poligonoCompleto();
	}
}
