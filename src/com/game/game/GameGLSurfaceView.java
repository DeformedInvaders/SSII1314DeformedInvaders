package com.game.game;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.game.data.Personaje;
import com.project.main.R;

public class GameGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
    private GameOpenGLRenderer renderer;
    private Context mContext;
    
    private String nombrePersonaje;
    private boolean animacionFinalizada;
    
    private ExternalStorageManager manager;
	private CountDownTimer timer;
	private AudioPlayerManager player;
    
	/* SECTION Constructora */
	
    public GameGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.SimpleTouch);
        
        mContext = context;
        
        animacionFinalizada = true;
        
        timer = new CountDownTimer(TIME_DURATION, TIME_INTERVAL) {

			@Override
			public void onFinish() 
			{ 
				renderer.pararAnimacion();
				animacionFinalizada = true;
				requestRender();
			}

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
        manager = m;
        nombrePersonaje = p.getNombre();
        
    	renderer = new GameOpenGLRenderer(getContext(), p);
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
	}
	
	/* SECTION Métodos abstractos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchMove(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onTouchUp(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		return false;
	}
	
	@Override
	protected boolean onMultiTouchEvent()
	{
		return false;
	}
	
	/* SECTION Métodos de Selección de Estado */
	
	public void seleccionarRun() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarRun();
			requestRender();
			
			timer.start();
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_run));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarJump() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarJump();
			requestRender();
			
			timer.start();
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_jump));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarCrouch()
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarCrouch();
			requestRender();
			
			timer.start();
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_crouch));
			
			animacionFinalizada = false;
		}
	}

	public void seleccionarAttack() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarAttack();
			requestRender();
			
			timer.start();
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_attack));
			
			animacionFinalizada = false;
		}
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	/* SECTION Métodos de Guardado de Información */
	
}
