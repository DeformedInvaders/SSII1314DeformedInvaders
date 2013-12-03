package com.example.paint;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.example.main.Esqueleto;
import com.example.touch.DoubleTouchGestureListener;
import com.example.touch.DragGestureDetector;
import com.example.touch.ScaleGestureListener;

public class PaintGLSurfaceView extends GLSurfaceView
{
    private final PaintOpenGLRenderer renderer;
    
    private ScaleGestureDetector scaleDectector;
    private GestureDetector doubleTouchDetector;
    private DragGestureDetector dragDetector;

    public PaintGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Create an OpenGL 1.0 context.
        setEGLContextClientVersion(1);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new PaintOpenGLRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        scaleDectector = new ScaleGestureDetector(context, new ScaleGestureListener(renderer));
        doubleTouchDetector = new GestureDetector(context, new DoubleTouchGestureListener(renderer));
        dragDetector = new DragGestureDetector(renderer);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{	
		int action = event.getAction();
		
		float x = event.getX();
		float y = event.getY();
		
		float width = getWidth();
		float height = getHeight();
		
		TPaintEstado estado = renderer.getEstado();
		
		if(estado == TPaintEstado.Pincel)
		{
			switch(action)
			{
				case MotionEvent.ACTION_DOWN:
					renderer.crearPolilinea();
					renderer.anyadirPunto(x, y, width, height);
				break;
				case MotionEvent.ACTION_MOVE:
					renderer.anyadirPunto(x, y, width, height);	
				break;
				case MotionEvent.ACTION_UP:
					renderer.anyadirPunto(x, y, width, height);
					renderer.guardarPolilinea();
				break;
			}
		}
		else if(estado == TPaintEstado.Cubo)
		{
			renderer.anyadirPunto(x, y, width, height);	
		}
		else {
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
		return true;
	}
	
	public void seleccionarMano()
	{
		renderer.guardarPolilinea();
		renderer.seleccionarMano();
	}
	
	public void seleccionarPincel()
	{
		renderer.guardarPolilinea();
		renderer.seleccionarPincel();
	}
	
	public void seleccionarCubo()
	{
		renderer.guardarPolilinea();
		renderer.seleccionarCubo();
	}
	
	public void seleccionarColor()
	{
		renderer.seleccionarColor();
	}
	
	public void seleccionarSize()
	{
		renderer.seleccionarSize();
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
	
	public void setEsqueleto(Esqueleto esqueleto)
	{
		renderer.setEsqueleto(esqueleto);
	}
	
	public void testBitMap()
	{
		renderer.guardarPolilinea();
		renderer.testBitMap();
		// Tomar ScreenShot
		requestRender();
		// Pintar Textura
		requestRender();
	}
}
