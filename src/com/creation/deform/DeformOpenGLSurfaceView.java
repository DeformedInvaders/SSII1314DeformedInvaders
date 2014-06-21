package com.creation.deform;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.creation.data.TTypeMovement;
import com.game.data.Character;
import com.lib.buffer.VertexArray;
import com.main.model.GamePreferences;

public class DeformOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnDeformListener mListener;
	private DeformOpenGLRenderer mRenderer;

	private Handler handler;
	private Runnable task;

	private boolean threadActive;

	/* Constructora */

	public DeformOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.MultiTouch, false);
	}

	public void setParameters(OnDeformListener listener, Character personaje, TTypeMovement movimiento)
	{
		mListener = listener;
		
		mRenderer = new DeformOpenGLRenderer(getContext(), Color.WHITE, mListener, personaje, movimiento);
		setRenderer(mRenderer);

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
					mListener.onAnimationFinished();
					threadActive = false;
				}
			}
		};

		threadActive = false;
	}

	/* Métodos Abstractos OpenGLSurfaceView */

	@Override
	protected boolean onTouchDown(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchDown(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchPointerDown(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchDown(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchMove(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchMove(x, y, width, height, pos);
	}

	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchUp(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onTouchPointerUp(float x, float y, float width, float height, int pos)
	{
		return mRenderer.onTouchPointerUp(x, y, width, height, pos);
	}
	
	@Override
	protected boolean onMultiTouchPre(int action, int countPounter)
	{
		if (action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_MOVE)
		{
			return mRenderer.onMultiTouchPreAction(countPounter);
		}
		
		return false;
	}

	@Override
	protected boolean onMultiTouchPost(int action)
	{
		if (action == MotionEvent.ACTION_MOVE)
		{
			return mRenderer.onMultiTouchPostAction();
		}
		
		return false;
	}

	/* Métodos de modifiación del Renderer */

	public void selectAdding()
	{
		mRenderer.selectAdding();
	}

	public void selectDeleting()
	{
		mRenderer.selectDeleting();
	}

	public void selectMoving()
	{
		mRenderer.selectMoving();
	}

	public void selectReset()
	{
		mRenderer.onReset();
		requestRender();
	}

	public void selectRecording()
	{
		mRenderer.selectRecording();
		requestRender();
	}

	public void selectPlaying()
	{
		if (!threadActive)
		{
			mRenderer.selectPlaying();
			requestRender();

			mListener.onPlaySoundEffect();
			task.run();
			threadActive = true;
		}
	}

	/* Métodos de Obtención de Información */

	public boolean isHandlesEmpty()
	{
		return mRenderer.isHandlesEmpty();
	}

	public boolean isStateAdding()
	{
		return mRenderer.isStateAdding();
	}

	public boolean isStateDeleting()
	{
		return mRenderer.isStateDeleting();
	}

	public boolean isStateMoving()
	{
		return mRenderer.isStateMoving();
	}

	public boolean isStateRecording()
	{
		return mRenderer.isStateRecording();
	}

	public boolean isAnimationReady()
	{
		return mRenderer.isAnimationReady();
	}

	public boolean isStatePlaying()
	{
		return mRenderer.isStatePlaying();
	}

	public List<VertexArray> getMovement()
	{
		return mRenderer.getAnimation();
	}

	/* Métodos de Guardado de Información */

	public DeformDataSaved saveData()
	{
		return mRenderer.saveData();
	}

	public void restoreData(DeformDataSaved data)
	{
		mRenderer.restoreData(data);
	}
}
