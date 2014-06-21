package com.video.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.dialog.TextDialog;
import com.android.view.OpenGLFragment;
import com.main.model.GamePreferences;
import com.project.main.R;
import com.video.data.Video;

public class VideoFragment extends OpenGLFragment implements OnVideoListener
{
	private VideoFragmentListener mCallback;
	
	private TStateDialog mState;
	private TextDialog sceneDialog, characterDialog, actorDialog;
	private ImageView imagePlay;
	
	private Video mVideo;
	
	private VideoOpenGLSurfaceView mCanvas;
	
	/* Constructora */

	public static final VideoFragment newInstance(VideoFragmentListener callback, Video video)
	{
		VideoFragment fragment = new VideoFragment();
		fragment.setParameters(callback, video);
		return fragment;
	}

	private void setParameters(VideoFragmentListener callback, Video video)
	{
		mCallback = callback;
		mVideo = video;
	}

	public interface VideoFragmentListener
	{
		public void onVideoFinished();
		public void onVideoPlayMusic(int music);
		public void onVideoPlaySoundEffect(int sound, boolean blockable);
		public void onVideoPlayVoice(int voice);
		public void onVideoResumeMusic();
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_video_layout, container, false);

		mCanvas = (VideoOpenGLSurfaceView) rootView.findViewById(R.id.videoGLSurfaceViewVideo1);
		mCanvas.setParameters(this, mVideo);		
		setCanvasListener(mCanvas);
		
		imagePlay = (ImageView) rootView.findViewById(R.id.imageViewVideo1);
		imagePlay.setOnClickListener(new OnPlayVideoClickListener());
	
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		mCanvas = null;
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
		mCanvas.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();		
		mCanvas.saveData();
		mCanvas.seleccionarPause();
		mCanvas.onPause();
		
		imagePlay.setVisibility(View.VISIBLE);
	}

	/* Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void resetInterface() { }

	@Override
	protected void updateInterface() { }

	/* Métodos Listener onClick */

	private class OnPlayVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCanvas.seleccionarResume();
			imagePlay.setVisibility(View.INVISIBLE);
			mCallback.onVideoResumeMusic();
		}
	}
	
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
	public void onPlaySoundEffect(int sound, boolean blockable)
	{
		mCallback.onVideoPlaySoundEffect(sound, blockable);
	}
	
	@Override
	public void onPlayVoice(int voice)
	{
		mCallback.onVideoPlayVoice(voice);
	}
	
	@Override
	public void onChangeDialog(final int text, TStateVideo estado)
	{
		if (estado == TStateVideo.Outside || estado == TStateVideo.Door)
		{
			mState = TStateDialog.Scene;
		}
		else if (estado == TStateVideo.Rock)
		{
			mState = TStateDialog.Character;
		}
		else if (estado == TStateVideo.Noise || estado == TStateVideo.Brief)
		{
			mState = TStateDialog.Actor;
		}
		else
		{
			mState = TStateDialog.Nothing;
		}
		
		getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run()
	        {
	        	if (mState == TStateDialog.Scene)
	        	{
	        		if (sceneDialog == null)
	        		{
	        			sceneDialog = new TextDialog(getActivity(), R.layout.dialog_text_scene_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	int posY = (int) (mCanvas.getHeight() - GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	sceneDialog.setText(text);
		        	sceneDialog.show(mCanvas, posX, posY);
	        	}
	        	else if (mState == TStateDialog.Character)
	        	{
	        		if (characterDialog == null)
	        		{
	        			characterDialog = new TextDialog(getActivity(), R.layout.dialog_text_character_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	int posY = (int) (mCanvas.getHeight() - GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	characterDialog.setText(text);
		        	characterDialog.show(mCanvas, posX, posY);
	        	}
	        	else if (mState == TStateDialog.Actor)
	        	{
	        		if (actorDialog == null)
	        		{
	        			actorDialog = new TextDialog(getActivity(), R.layout.dialog_text_actor_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ANCHURA_LATERAL() + GamePreferences.MARCO_ANCHURA_INTERIOR());
		        	int posY = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	actorDialog.setText(text);
		        	actorDialog.show(mCanvas, posX, posY);
	        	}
	        }
	    });
	}
	
	@Override
	public void onDismissDialog()
	{
		if (mState == TStateDialog.Scene)
		{
			sceneDialog.dismiss();
		}
		else if (mState == TStateDialog.Character)
		{
			characterDialog.dismiss();
		}
		else if (mState == TStateDialog.Actor)
		{
			actorDialog.dismiss();
		}
	}
}
