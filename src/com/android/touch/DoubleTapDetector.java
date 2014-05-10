package com.android.touch;

import android.view.MotionEvent;

import com.android.opengl.OpenGLRenderer;
import com.main.model.GamePreferences;

public class DoubleTapDetector
{
	private OpenGLRenderer renderer;

	private boolean modoCamara;
	private boolean bloqueado;
	private long lastTap;

	/* Constructora */

	public DoubleTapDetector(OpenGLRenderer renderer)
	{
		this.renderer = renderer;
		this.bloqueado = false;
		this.lastTap = System.currentTimeMillis();
		this.modoCamara = true;
	}

	/* Métodos de Modificación de Estado */

	public void setEstado(boolean camara)
	{
		this.modoCamara = camara;
	}

	/* Métodos Listener onTouch */

	public boolean onTouchEvent(MotionEvent event, float screenWidth, float screenHeight)
	{
		int action = event.getActionMasked();
		
		if (!bloqueado)
		{
			if (action == MotionEvent.ACTION_UP)
			{
				return tapOnTouchUp();
			}
		}
		else
		{
			if (action == MotionEvent.ACTION_UP)
			{
				bloqueado = false;
			}
		}

		return false;
	}

	public boolean onStopEvent()
	{
		bloqueado = true;
		return true;
	}

	private boolean tapOnTouchUp()
	{
		long time = System.currentTimeMillis();

		if (Math.abs(lastTap - time) < GamePreferences.MAX_DURATION_TAP)
		{
			// Double Touch
			if (modoCamara)
			{
				renderer.camaraRestore();
			}
			else
			{
				renderer.pointsRestore();
			}
			
			return true;
		}

		lastTap = time;
		
		return false;
	}
}
