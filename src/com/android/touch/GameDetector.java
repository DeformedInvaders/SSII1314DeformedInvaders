package com.android.touch;

import android.view.MotionEvent;

import com.game.game.GameOpenGLSurfaceView;
import com.game.game.TEstadoGame;
import com.main.model.GamePreferences;

public class GameDetector
{
	private float lastPixelY;
	private long lastTap;
	
	private TEstadoGame estado;
	private boolean bloqueado;

	public void set(TEstadoGame e)
	{
		estado = e;
	}
	
	public boolean onTouchEvent(MotionEvent event, GameOpenGLSurfaceView renderer)
	{
		int action = event.getActionMasked();

		long time = System.currentTimeMillis();
			
		if(estado == TEstadoGame.FaseEnemies)
		{
			float pixelY = event.getY();
	
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					onGameEnemiesDown(time, pixelY, renderer);
				break;
				case MotionEvent.ACTION_UP:
					onGameEnemiesUp(time, pixelY, renderer);
				break;
			}
	
			return true;
		}
		else
		{			
			if (event.getPointerCount() == 1)
			{
				float pixelX = event.getX(0);
				float pixelY = event.getY(0);
				
				switch (action)
				{
					case MotionEvent.ACTION_DOWN:
						onGameBossDown(time, renderer);
					break;
					case MotionEvent.ACTION_UP:
						onGameBossUp(time, renderer);
					break;
					case MotionEvent.ACTION_MOVE:
						onGameBossMove(time, pixelX, pixelY, renderer);
					break;
				}
			}
			else if(event.getPointerCount() > 1)
			{
				float pixelX1 = event.getX(0);
				float pixelY1 = event.getY(0);
				
				switch (action)
				{
					case MotionEvent.ACTION_POINTER_DOWN:
						onGameBossDown(time, renderer);
					break;
					case MotionEvent.ACTION_POINTER_UP:
						onGameBossUp(time, renderer);
					break;
					case MotionEvent.ACTION_MOVE:
						onGameBossMove(time, pixelX1, pixelY1, renderer);
					break;
				}
			}
			
			return true;
		}
	}

	private void onGameEnemiesDown(long time, float pixelY, GameOpenGLSurfaceView renderer)
	{
		lastPixelY = pixelY;
		lastTap = time;
	}

	private void onGameEnemiesUp(long time, float pixelY, GameOpenGLSurfaceView surface)
	{
		if (pixelY - lastPixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			surface.seleccionarCrouch();
		}
		else if (lastPixelY - pixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			surface.seleccionarJump();
		}
		else if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			surface.seleccionarAttack();
		}
	}
	
	//Eventos boss
	private void onGameBossDown(long time, GameOpenGLSurfaceView surface)
	{
		lastTap = time;
		bloqueado = true;
	}
	
	private void onGameBossUp(long time, GameOpenGLSurfaceView surface)
	{
		if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			surface.seleccionarAttack();
			bloqueado = false;
		}
	}
	
	private void onGameBossMove(long time, float pixelX, float pixelY, GameOpenGLSurfaceView surface)
	{
		if(bloqueado)
		{
			if (Math.abs(lastTap - time) >= GamePreferences.MAX_DURATION_TAP)
			{
				bloqueado = false;
			}
		}
		if(!bloqueado)
		{
			surface.onTouchMove(pixelX, pixelY, 0, 0, 0);
		}
	}
	
	

}
