package com.character.display;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.main.model.GamePreferences;

public class DisplayOpenGLSurfaceView extends OpenGLSurfaceView
{
	// Renderer
	private OnDisplayListener mListener;
	private DisplayOpenGLRenderer mRenderer;

	private boolean characterLoaded, randomAnimation;

	private Handler handler;
	private Runnable task;

	private boolean threadActive;

	/* Constructora */

	public DisplayOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, true);

		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (!mRenderer.playAnimation())
				{
					requestRender();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION());
				}
				else
				{
					mRenderer.stopAnimation();
					threadActive = false;
				}
			}
		};

		threadActive = false;
	}

	public void setParameters(OnDisplayListener listener, Character personaje, boolean random)
	{
		mListener = listener;
		randomAnimation = random;
		characterLoaded = true;

		mRenderer = new DisplayOpenGLRenderer(getContext(), personaje);
		setRenderer(mRenderer);
	}

	public void setParameters()
	{
		randomAnimation = false;
		characterLoaded = false;

		mRenderer = new DisplayOpenGLRenderer(getContext());
		setRenderer(mRenderer);
	}

	/* Métodos abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float pixelX, float pixelY, float screenWidth, float screenHeight, int pointer)
	{
		if (randomAnimation && characterLoaded)
		{
			int animacion = (int) Math.floor(Math.random() * GamePreferences.NUM_TYPE_MOVEMENTS);
			startAnimation(TTypeMovement.values()[animacion]);
			return true;
		}

		return false;
	}

	/* Métodos de Selección de Estado */

	public void startAnimation(TTypeMovement movement)
	{
		if (!threadActive)
		{
			mRenderer.startAnimation(movement);
			requestRender();
			
			int sound = movement.getSound();
			if (sound != -1)
			{
				mListener.onDisplayPlaySoundEffect(sound);
			}
			
			task.run();
			threadActive = true;
		}
	}

	public void selectPreparing()
	{
		mRenderer.selectPreparing(getHeight(), getWidth());
		setDetectorState(TStateDetector.CamaraDetectors);

		requestRender();
	}

	public void selectFinished()
	{
		mRenderer.selectFinished();
		requestRender();
	}

	/* Métodos de Obtención de Información */

	public boolean isStateNothing()
	{
		return mRenderer.isStateNothing();
	}

	public boolean isStatePreparing()
	{
		return mRenderer.isStatePreparing();
	}

	public boolean isStateFinished()
	{
		return mRenderer.isStateFinished();
	}

	public boolean isStateAnimation()
	{
		return mRenderer.isStateAnimation();
	}

	public Bitmap getScreenshot()
	{
		mRenderer.selectCapturing();
		setDetectorState(TStateDetector.SimpleTouch);

		requestRender();
		return mRenderer.getScreenshot();
	}

	/* Métodos de Guardado de Información */

	public void saveData()
	{
		mRenderer.saveData();
	}
}
