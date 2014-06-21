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
	
	private TStateDialog estadoDialogo;
	private TextDialog dialogoEscena, dialogoPersonaje, dialogoActor;
	private ImageView imagenPlay;
	
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
		public void onVideoPlaySoundEffect(int sound, boolean blockable);
		public void onVideoPlayVoice(int voice);
		public void onVideoResumeMusic();
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_video_layout, container, false);

		canvas = (VideoOpenGLSurfaceView) rootView.findViewById(R.id.videoGLSurfaceViewVideo1);
		canvas.setParameters(this, video);		
		setCanvasListener(canvas);
		
		imagenPlay = (ImageView) rootView.findViewById(R.id.imageViewVideo1);
		imagenPlay.setOnClickListener(new OnPlayVideoClickListener());
	
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
		canvas.seleccionarPause();
		canvas.onPause();
		
		imagenPlay.setVisibility(View.VISIBLE);
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
			canvas.seleccionarResume();
			imagenPlay.setVisibility(View.INVISIBLE);
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
			estadoDialogo = TStateDialog.Scene;
		}
		else if (estado == TStateVideo.Rock)
		{
			estadoDialogo = TStateDialog.Character;
		}
		else if (estado == TStateVideo.Noise || estado == TStateVideo.Brief)
		{
			estadoDialogo = TStateDialog.Actor;
		}
		else
		{
			estadoDialogo = TStateDialog.Nothing;
		}
		
		getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run()
	        {
	        	if (estadoDialogo == TStateDialog.Scene)
	        	{
	        		if (dialogoEscena == null)
	        		{
	        			dialogoEscena = new TextDialog(getActivity(), R.layout.dialog_text_scene_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	int posY = (int) (canvas.getHeight() - GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	dialogoEscena.setText(text);
		        	dialogoEscena.show(canvas, posX, posY);
	        	}
	        	else if (estadoDialogo == TStateDialog.Character)
	        	{
	        		if (dialogoPersonaje == null)
	        		{
	        			dialogoPersonaje = new TextDialog(getActivity(), R.layout.dialog_text_character_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	int posY = (int) (canvas.getHeight() - GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	dialogoPersonaje.setText(text);
		        	dialogoPersonaje.show(canvas, posX, posY);
	        	}
	        	else if (estadoDialogo == TStateDialog.Actor)
	        	{
	        		if (dialogoActor == null)
	        		{
	        			dialogoActor = new TextDialog(getActivity(), R.layout.dialog_text_actor_layout);
	        		}
	        		
	        		int posX = (int) (GamePreferences.MARCO_ANCHURA_LATERAL() + GamePreferences.MARCO_ANCHURA_INTERIOR());
		        	int posY = (int) (GamePreferences.MARCO_ALTURA_LATERAL());
		        	
		        	dialogoActor.setText(text);
		        	dialogoActor.show(canvas, posX, posY);
	        	}
	        }
	    });
	}
	
	@Override
	public void onDismissDialog()
	{
		if (estadoDialogo == TStateDialog.Scene)
		{
			dialogoEscena.dismiss();
		}
		else if (estadoDialogo == TStateDialog.Character)
		{
			dialogoPersonaje.dismiss();
		}
		else if (estadoDialogo == TStateDialog.Actor)
		{
			dialogoActor.dismiss();
		}
	}
}
