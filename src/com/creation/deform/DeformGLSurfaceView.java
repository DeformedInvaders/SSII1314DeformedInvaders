package com.creation.deform;

import java.util.List;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.Esqueleto;
import com.creation.data.Textura;
import com.lib.utils.FloatArray;

public class DeformGLSurfaceView extends OpenGLSurfaceView
{
    private DeformOpenGLRenderer renderer;
    
    private CountDownTimer timer;
    
    /* SECTION Constructora */

    public DeformGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.MultiTouch); 
    }
	
	public void setParameters(Esqueleto esqueleto, Textura textura, TDeformTipo tipo, final DeformFragment fragmento, int num_frames)
	{
		renderer = new DeformOpenGLRenderer(getContext(), NUM_HANDLES, esqueleto, textura, tipo, num_frames);
		setRenderer(renderer);
		
		timer = new CountDownTimer(TIME_DURATION_ANIMATION, TIME_INTERVAL_ANIMATION) 
		{

			@Override
			public void onFinish() 
			{ 
				renderer.seleccionarReposo();
				fragmento.reiniciarInterfaz();
				fragmento.actualizarInterfaz();
			}

			@Override
			public void onTick(long arg0) 
			{
				renderer.reproducirAnimacion();
				requestRender();
			}
        };
	}
	
    /* SECTION Métodos Abstráctos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchDown(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchDown(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchMove(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchMove(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchUp(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return renderer.onMultiTouchEvent();
	}
	
	/* SECTION Métodos de modifiación del Renderer */

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
	
	public void reiniciar()
	{
		renderer.reiniciar();
		requestRender();
	}
	
	public void seleccionarGrabado() 
	{
		renderer.seleccionarGrabado();
		requestRender();
	}

	public void seleccionarPlay() 
	{
		renderer.selecionarPlay();
		requestRender();
		
		timer.start();
	}
	
	public void seleccionarAudio()
	{
		renderer.seleccionarAudio();
	}
	
	public void seleccionarReposo() 
	{
		renderer.seleccionarReposo();
	}

	/* SECTION Métodos de Obtención de Información */
	
	public boolean isHandlesVacio()
	{
		return renderer.isHandlesVacio();
	}
	
	public boolean isEstadoAnyadir()
	{
		return renderer.isEstadoAnyadir();
	}
	
	public boolean isEstadoEliminar()
	{
		return renderer.isEstadoEliminar();
	}
	
	public boolean isEstadoDeformar()
	{
		return renderer.isEstadoDeformar();
	}
	
	public boolean isEstadoGrabacion()
	{
		return renderer.isEstadoGrabacion();
	}
	
	public boolean isGrabacionReady() 
	{
		return renderer.isGrabacionReady();
	}

	public boolean isEstadoAudio()
	{
		return renderer.isEstadoAudio();
	}
	
	public boolean isEstadoReproduccion()
	{
		return renderer.isEstadoReproduccion();
	}
	
	public List<FloatArray> getMovimientos()
	{
		return renderer.getMovimientos();
	}
	
	/* SECTION Métodos de Guardado de Información */
	
	public DeformDataSaved saveData()
	{
		return renderer.saveData();
	}
	
	public void restoreData(DeformDataSaved data)
	{
		renderer.restoreData(data);
	}

}
