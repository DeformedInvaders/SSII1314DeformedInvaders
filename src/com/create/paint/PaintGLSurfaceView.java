package com.create.paint;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.android.touch.DoubleTouchGestureListener;
import com.android.touch.DragGestureDetector;
import com.android.touch.ScaleGestureListener;
import com.project.data.Esqueleto;
import com.project.data.Textura;

public class PaintGLSurfaceView extends GLSurfaceView
{
	// Renderer
    private final PaintOpenGLRenderer renderer;
    
    // Detectores de Gestos
    private ScaleGestureDetector scaleDectector;
    private GestureDetector doubleTouchDetector;
    private DragGestureDetector dragDetector;

    public PaintGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Crear Contexto OpenGL ES 1.0
        setEGLContextClientVersion(1);

        // Asignar Renderer al GLSurfaceView
        renderer = new PaintOpenGLRenderer(context);
        setRenderer(renderer);

        // Activar Modo Pintura en demanda
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        scaleDectector = new ScaleGestureDetector(context, new ScaleGestureListener(renderer));
        doubleTouchDetector = new GestureDetector(context, new DoubleTouchGestureListener(renderer));
        dragDetector = new DragGestureDetector(renderer);
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
		
			if(renderer.getEstado() != TPaintEstado.Mano)
			{
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
			}
			else
			{
				if(event.getPointerCount() == 1)
				{
					dragDetector.onTouchEvent(event, x, y, width, height);
					doubleTouchDetector.onTouchEvent(event);
				}
				else
				{
					scaleDectector.onTouchEvent(event);
				}
			}
			
			requestRender();
		}
		return true;
	}
	
	public void seleccionarMano()
	{
		renderer.seleccionarMano();
	}
	
	public void seleccionarPincel()
	{
		renderer.seleccionarPincel();
	}
	
	public void seleccionarCubo()
	{
		renderer.seleccionarCubo();
	}
	
	public int getColorPaleta()
	{
		return renderer.getColorPaleta();
	}
	
	public void seleccionarColor(int color)
	{
		renderer.seleccionarColor(color);
	}
	
	public void seleccionarSize(int pos)
	{
		renderer.seleccionarSize(pos);
	}
	
	public void anteriorAccion()
	{
		renderer.anteriorAccion();
		requestRender();
	}
	
	public void siguienteAccion()
	{
		renderer.siguienteAccion();
		requestRender();
	}
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	public void setParameters(Esqueleto esqueleto)
	{
		renderer.setParameters(esqueleto);
	}
	
	public Textura getTextura()
	{
		return renderer.getTextura();
	}
	
	public void capturaPantalla()
	{
		renderer.capturaPantalla(getHeight(), getWidth());
		requestRender();
	}
	
	public boolean bufferSiguienteVacio()
	{
		return renderer.bufferSiguienteVacio();
	}
	
	public boolean bufferAnteriorVacio()
	{
		return renderer.bufferAnteriorVacio();
	}
}
