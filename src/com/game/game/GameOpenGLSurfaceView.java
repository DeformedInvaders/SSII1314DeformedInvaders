package com.game.game;

import java.util.List;
import java.util.Queue;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.audio.AudioPlayerManager;
import com.android.storage.ExternalStorageManager;
import com.android.touch.TTouchEstado;
import com.android.view.OpenGLSurfaceView;
import com.game.data.Entidad;
import com.game.data.Personaje;
import com.project.main.R;

public class GameOpenGLSurfaceView extends OpenGLSurfaceView
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
	private Runnable task;
	
	private boolean threadActivo;
	
	/* SECTION Constructora */
	
    public GameOpenGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs, TTouchEstado.GameDetectors);
        
        mContext = context;
        
        animacionFinalizada = true;
    }
	
	public void setParameters(Personaje p, ExternalStorageManager m, OnGameListener gl, List<Entidad> listaEnemigos, Queue<InstanciaEntidad> colaEnemigos, Background b)
	{        
        manager = m;
        nombrePersonaje = p.getNombre();
        listener = gl;
        
    	renderer = new GameOpenGLRenderer(getContext(), listaEnemigos, colaEnemigos, b, p);
        setRenderer(renderer);
        
        player = new AudioPlayerManager(manager) {

			@Override
			public void onPlayerCompletion() { }
        };
        
        handler = new Handler();
        
        task = new Runnable() {
        	@Override
            public void run()
        	{        		 
        		boolean primerosCiclos = contadorFrames < NUM_FRAMES_ANIMATION / 2;
        		
				renderer.reproducirAnimacion(primerosCiclos);
				requestRender();
				
				contadorFrames++;
				
				if(contadorFrames == NUM_FRAMES_ANIMATION)
				{
					renderer.seleccionarRun();
					animacionFinalizada = true;
				}

				int valor = renderer.isJuegoFinalizado();
				
				switch(valor)
				{
					case 0:
						handler.postDelayed(this, TIME_INTERVAL_ANIMATION);
					break;
					case 1:
						renderer.pararAnimacion();
	    				requestRender();
	                	
	                	listener.onGameFinished();
					break;
					case 2:
						renderer.pararAnimacion();
	    				requestRender();
	                	
	                	listener.onGameFailed();
					break;
				}
        	}
        };
        
        threadActivo = false;
        
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
		if(!threadActivo)
		{
			task.run();
			
			threadActivo = true;
		}
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
