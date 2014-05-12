package com.video.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.view.OpenGLFragment;
import com.project.main.R;
import com.video.data.Video;

public class VideoFragment extends OpenGLFragment implements OnVideoListener
{
	private VideoFragmentListener mCallback;
	
	private Video video;
	
	private VideoOpenGLSurfaceView canvas;
	
	/* Constructora */

	public static final VideoFragment newInstance(VideoFragmentListener c, Video v)
	{
		VideoFragment fragment = new VideoFragment();
		fragment.setParameters(c, v);
		return fragment;
	}

	private void setParameters(VideoFragmentListener c, Video v)
	{
		mCallback = c;
		video = v;
	}

	public interface VideoFragmentListener
	{
		public void onVideoFinished();
		public void onVideoPlayMusic(int music);
		public void onVideoPlaySoundEffect(int sound);
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_video_layout, container, false);

		canvas = (VideoOpenGLSurfaceView) rootView.findViewById(R.id.videoGLSurfaceViewVideo1);
		canvas.setParameters(this, video);
		
		setCanvasListener(canvas);
	
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		canvas = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
	}

	@Override
	public void onResume()
	{
		super.onResume();		
		canvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
		canvas.saveData();
		canvas.onPause();
	}

	/* Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz() { }

	@Override
	protected void actualizarInterfaz() { }

	/* Métodos interfaz OnVideoListener */
	
	@Override
	public void onVideoFinished()
	{
		mCallback.onVideoFinished();
	}

	@Override
	public void onPlayMusic(int music)
	{
		mCallback.onVideoPlayMusic(music);
	}

	@Override
	public void onPlaySoundEffect(int sound)
	{
		mCallback.onVideoPlaySoundEffect(sound);
	}
	
	@Override
	public void onChangeDialog(int text)
	{
		
	}
}
