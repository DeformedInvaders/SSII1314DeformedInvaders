package com.video.video;

import android.content.Context;
import android.util.AttributeSet;

import com.android.touch.TEstadoDetector;
import com.android.view.OpenGLSurfaceView;
import com.video.data.Video;

public class VideoOpenGLSurfaceView extends OpenGLSurfaceView
{
	private OnVideoListener mListener;
	
	// Renderer
	private VideoOpenGLRenderer renderer;

	/* Constructora */

	public VideoOpenGLSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs, TEstadoDetector.SimpleTouch, false);
	}

	public void setParameters(OnVideoListener listener, Video video)
	{		
		mListener = listener;
		
		// Asignar Renderer al GLSurfaceView
		renderer = new VideoOpenGLRenderer(getContext(), video);
		setRenderer(renderer);
	}
	
	@Override
	protected boolean onTouchUp(float x, float y, float width, float height, int pos)
	{
		return renderer.onTouchUp(x, y, width, height, pos);
	}
}
