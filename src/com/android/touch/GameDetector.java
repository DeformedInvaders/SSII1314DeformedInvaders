package com.android.touch;

import android.view.MotionEvent;

import com.creation.data.TTipoMovimiento;
import com.game.game.GameOpenGLSurfaceView;
import com.game.game.TEstadoGame;
import com.main.model.GamePreferences;

public class GameDetector
{
	private float lastPixelY;
	private long lastTap;
	
	private boolean bloqueado;
	
	public boolean onTouchEvent(MotionEvent event, TEstadoGame estado, GameOpenGLSurfaceView renderer)
	{
		int action = event.getActionMasked();

		long time = System.currentTimeMillis();
		float pixelX = event.getX();
		float pixelY = event.getY();
		
		if (estado == TEstadoGame.FaseEnemies)
		{
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
					onGameEnemiesDown(time, pixelY);
				break;
				case MotionEvent.ACTION_UP:
					onGameEnemiesUp(time, pixelY, renderer);
				break;
			}
	
			return true;
		}
		else if (estado == TEstadoGame.FaseBoss)
		{
			switch (action)
			{
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					onGameBossDown(time);
				break;
				case MotionEvent.ACTION_MOVE:
					onGameBossMove(time, pixelX, pixelY, renderer);
				break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					onGameBossUp(time, renderer);
				break;
			}
			
			return true;
		}
		
		return false; 
	}

	private void onGameEnemiesDown(long time, float pixelY)
	{
		lastPixelY = pixelY;
		lastTap = time;
	}

	private void onGameEnemiesUp(long time, float pixelY, GameOpenGLSurfaceView surface)
	{
		if (pixelY - lastPixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			surface.seleccionarAnimacion(TTipoMovimiento.Crouch);
		}
		else if (lastPixelY - pixelY > GamePreferences.MAX_DISTANCE_DRAG)
		{
			surface.seleccionarAnimacion(TTipoMovimiento.Jump);
		}
		else if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			surface.seleccionarAnimacion(TTipoMovimiento.Attack);
		}
	}
	
	private void onGameBossDown(long time)
	{
		lastTap = time;
		bloqueado = true;
	}
	
	private void onGameBossUp(long time, GameOpenGLSurfaceView surface)
	{
		if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			surface.seleccionarAnimacion(TTipoMovimiento.Attack);
			bloqueado = false;
		}
	}
	
	private void onGameBossMove(long time, float pixelX, float pixelY, GameOpenGLSurfaceView surface)
	{
		if (bloqueado && Math.abs(lastTap - time) >= GamePreferences.MAX_DURATION_TAP)
		{
			bloqueado = false;
		}
		
		if (!bloqueado)
		{
			//surface.seleccionarPosicion(pixelX, pixelY);
		}
	}
}
