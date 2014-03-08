package com.game.game;

import android.content.Context;
import android.os.Handler;
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
    private OnGameListener listener;
    private Context mContext;
    
    private String nombrePersonaje;
    
    private boolean animacionFinalizada;
    private int contadorFrames;
    
    private ExternalStorageManager manager;
	private AudioPlayerManager player;
	
	private Handler handler;
	private Thread thread;
	
	/* SECTION Constructora */
	
    public GameGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.GameDetectors);
        
        mContext = context;
        
        animacionFinalizada = true;
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m, int l, OnGameListener gl)
	{        
        manager = m;
        nombrePersonaje = p.getNombre();
        listener = gl;
        
    	renderer = new GameOpenGLRenderer(getContext(), p, l);
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
        
        handler = new Handler();
        
        thread = new Thread(new Runnable() {
        	@Override
            public void run()
        	{        		                
				renderer.reproducirAnimacion();
				requestRender();
				
				contadorFrames++;
				
				if(contadorFrames == NUM_FRAMES_ANIMATION)
				{
					renderer.seleccionarRun();
					animacionFinalizada = true;
				}
				
				handler.postDelayed(this, TIME_INTERVAL_ANIMATION);

                if(renderer.isJuegoFinalizado())
                {
                    renderer.pararAnimacion();
    				requestRender();
    				
                	handler.removeCallbacks(this);
                	
                	listener.onGameFinished();
                }
        	}
        });
        
        renderer.seleccionarRun();
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
        thread.run();
	}
	
	public void seleccionarJump() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarJump();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_jump));
			
			animacionFinalizada = false;	
			contadorFrames = 0;
		}
	}

	public void seleccionarCrouch()
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarCrouch();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_crouch));
			
			animacionFinalizada = false;
			contadorFrames = 0;
		}
	}

	public void seleccionarAttack() 
	{
		if(animacionFinalizada)
		{
			renderer.seleccionarAttack();
			requestRender();
			
			player.startPlaying(nombrePersonaje, mContext.getString(R.string.title_animation_section_attack));
			
			animacionFinalizada = false;
			contadorFrames = 0;
		}
	}
	
	/* SECTION Métodos de Obtención de Información */
	
	/* SECTION Métodos de Guardado de Información */
	
	public void saveData()
	{
		renderer.saveData();
	}
}
