package com.view.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.create.design.TPadre;
import com.project.data.Personaje;
import com.project.main.OpenGLSurfaceView;
import com.project.main.R;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private DisplayOpenGLRenderer renderer;
    private Context mContext;
    
    private String nombre;
    
    private TPadre padre;
    
    private ExternalStorageManager manager;
	private CountDownTimer timer;
	private AudioPlayerManager player;
	
	private boolean animacionFinalizada;
    
	/* SECTION Constructora */
	
    public DisplayGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);
       
        mContext = context;
        
        animacionFinalizada = true;
        
        timer = new CountDownTimer(TIME_DURATION, TIME_INTERVAL) {

			@Override
			public void onFinish() 
			{ 
				animacionFinalizada = true;
			}

			@Override
			public void onTick(long arg0) 
			{
				renderer.reproducirAnimacion();
				requestRender();
			}
        };
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m, TPadre e)
	{
		renderer = new DisplayOpenGLRenderer(getContext(), p);
		nombre = p.getNombre();
		manager = m;
		padre = e;
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
	}
	
	public void setParameters()
	{
		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}
	
	/* SECTION Métodos abstractos OpenGLSurfaceView */
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) 
	{ 
		if(padre == TPadre.Main)
		{
			if(nombre != null)
			{
				int n = (int) Math.floor(Math.random()*4);
				
				if(animacionFinalizada)
				{
					switch (n)
					{
						case 0:
							seleccionarRun();
						break;
						case 1:
							seleccionarJump();
						break;
						case 2:
							seleccionarCrouch();
						break;
						case 3:
							seleccionarAttack();
						break;
					}
					animacionFinalizada = false;
				}
			}
		}
	}
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION Métodos de Selección de Estado */
	
	public void seleccionarRun() 
	{
		renderer.seleccionarRun();
		requestRender();
		
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_run));
	}

	public void seleccionarJump() 
	{
		renderer.seleccionarJump();
		requestRender();
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_jump));
	}

	public void seleccionarCrouch()
	{
		renderer.seleccionarCrouch();
		requestRender();
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_crouch));
	}

	public void seleccionarAttack() 
	{
		renderer.seleccionarAttack();
		requestRender();
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_attack));
	}
	
	public void seleccionarRetoque()
	{
		renderer.seleccionarRetoque(getHeight(), getWidth());
		setEstado(TTouchEstado.CamaraDetectors);
		
		requestRender();
	}
	
	public void seleccionarTerminado()
	{
		renderer.seleccionarTerminado();
		requestRender();
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	public boolean isEstadoReposo()
	{
		return renderer.isEstadoReposo();
	}
	
	public boolean isEstadoRetoque()
	{
		return renderer.isEstadoRetoque();
	}
	
	public boolean isEstadoTerminado()
	{
		return renderer.isEstadoTerminado();
	}
	
	public boolean isEstadoAnimacion()
	{
		return renderer.isEstadoAnimacion();
	}
	
	public Bitmap getCapturaPantalla()
	{
		renderer.seleccionarCaptura();
		setEstado(TTouchEstado.SimpleTouch);
		
		requestRender();
		return renderer.getCapturaPantalla();
	}
	
	/* SECTION Métodos de Guardado de Información */
	
	public void saveData()
	{
		renderer.saveData();
	}

	public boolean getAnimacionFinalizada() 
	{
		return animacionFinalizada;
	}
	
	public void setAnimacionFinalizada(boolean a) 
	{
		animacionFinalizada = a;
	}
}
