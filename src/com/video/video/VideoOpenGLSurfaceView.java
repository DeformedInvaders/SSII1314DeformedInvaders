package com.video.video;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.android.touch.TStateDetector;
import com.android.view.OpenGLSurfaceView;
import com.main.model.GamePreferences;
import com.video.data.Video;

public class VideoOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnVideoListener mListener;
	
	private TStateVideo mState;
	private Video mVideo;
	
	private VideoOpenGLRenderer mRenderer;
	
	private boolean threadActive;
	private Handler handler;
	private Runnable task;
	
	private int[] mQuoteList;
	private int quotePosition;
	
	private long timeStart, timeDuration;

	/* Constructora */

	public VideoOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TStateDetector.SimpleTouch, false);
	}

	public void setParameters(OnVideoListener listener, Video video)
	{		
		mListener = listener;
		
		mVideo = video;
		mState = TStateVideo.Nothing;
		threadActive = false;
		
		quotePosition = 0;
		
		timeStart = System.currentTimeMillis();
		timeDuration = 0;
		
		// Asignar Renderer al GLSurfaceView
		mRenderer = new VideoOpenGLRenderer(getContext(), mVideo);
		setRenderer(mRenderer);

		handler = new Handler();

		task = new Runnable() {
			@Override
			public void run()
			{
				if (threadActive)
				{
					long tiempoActual = System.currentTimeMillis();
					
					// Fin de Ciclo de Escena
					if (tiempoActual - timeStart >= timeDuration)
					{
						// Fin de Video
						if (mState == TStateVideo.Space)
						{
							threadActive = false;
							
							mListener.onDismissDialog();
							mListener.onVideoFinished();
						}
						
						// Fin de Escena
						if (mQuoteList == null || quotePosition >= mQuoteList.length)
						{
							mState = mState.getNext();
							
							mListener.onDismissDialog();
							mQuoteList = mVideo.getQuote(mState);
							quotePosition = 0;
							
							mRenderer.selectScene(mState);
							
							if (mState != TStateVideo.Outside)
							{
								mRenderer.nextScene();
								requestRender();
							}
							
							long duration = mState.getDuration();
							int music = mState.getMusic();
							int sound = mState.getSound();
							
							if (sound != -1)
							{
								mListener.onPlayVoice(sound);
							}
							
							if (music != -1)
							{
								mListener.onPlayMusic(music);
							}
							
							if (duration != -1)
							{
								timeStart = tiempoActual;
								timeDuration = duration;
							}
						}
						
						if (mQuoteList != null)
						{
							mListener.onChangeDialog(mQuoteList[quotePosition], mState);
							timeStart = tiempoActual;
							quotePosition++;
							
							int sound = mState.getSound();
							
							if (sound != -1)
							{
								mListener.onPlayVoice(sound);
							}
						}		
					}
					
					// Cambio de Ciclo de Escena
					if (mState == TStateVideo.Door)
					{
						mRenderer.zoomScene(0.999f);
					}
					
					mRenderer.playAnimation();
					requestRender();
					handler.postDelayed(this, GamePreferences.TIME_INTERVAL_ANIMATION_VIDEO());
				}
			}
		};
	}
	
	public void seleccionarResume()
	{
		if (!threadActive)
		{
			threadActive = true;
			task.run();
		}
	}

	public void seleccionarPause()
	{
		if (threadActive)
		{
			threadActive = false;
			handler.removeCallbacks(task);
		}
	}
	
	/* Métodos Abstráctos OpenGLSurfaceView */
	
	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		if (mRenderer.onTouchUp(x, y, width, height, pos))
		{
			int sonido = mRenderer.getSoundActive();
			if (sonido != -1)
			{
				mListener.onPlaySoundEffect(sonido, true);
				mRenderer.resetSoundActive();
				return true;
			}
		}
		
		return false;
	}
	
	/* Métodos de Guardado de Información */

	public void saveData()
	{
		mRenderer.saveData();
	}
}
