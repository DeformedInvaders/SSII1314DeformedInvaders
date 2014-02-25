package com.view.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.project.data.Personaje;
import com.project.main.OpenGLSurfaceView;
import com.project.main.R;

public class DisplayGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private DisplayOpenGLRenderer renderer;
    private Context mContext;
    
    private String nombre;
    
    private ExternalStorageManager manager;
	private CountDownTimer timer;
	private AudioPlayerManager player;
    
	/* SECTION Constructora */
	
    public DisplayGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);
       
        mContext = context;
        
        timer = new CountDownTimer(TIME_DURATION, TIME_INTERVAL) {

			@Override
			public void onFinish() { }

			@Override
			public void onTick(long arg0) 
			{
				renderer.reproducirAnimacion();
				requestRender();
			}
        };
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m)
	{
		renderer = new DisplayOpenGLRenderer(getContext(), p);
		nombre = p.getNombre();
		manager = m;
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
	}
	
	public void setParameters(Personaje p)
	{
		renderer = new DisplayOpenGLRenderer(getContext(), p);
        setRenderer(renderer);
	}
	
	public void setParameters()
	{
		renderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(renderer);
	}
	
	/* SECTION M�todos abstractos OpenGLSurfaceView */
	
	@Override
	protected void onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer) { }
	
	@Override
	protected void onMultiTouchEvent() { }
	
	/* SECTION M�todos de Selecci�n de Estado */
	
	public void selecionarRun() 
	{
		renderer.selecionarRun();
		requestRender();
		
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_run));
	}

	public void selecionarJump() 
	{
		renderer.selecionarJump();
		requestRender();
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_jump));
	}

	public void selecionarCrouch()
	{
		renderer.selecionarCrouch();
		requestRender();
		
		timer.start();
		player.startPlaying(nombre, mContext.getString(R.string.title_animation_section_crouch));
	}

	public void selecionarAttack() 
	{
		renderer.selecionarAttack();
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
	
	/* SECTION M�todos de Obtenci�n de Informaci�n */
	
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
	
	/* SECTION M�todos de Guardado de Informaci�n */
	
	public void saveData()
	{
		renderer.saveData();
	}
}
