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
	
	private TEstadoDialogo estadoDialogo;
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
		canvas.onPause();
	}

	/* Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz() { }

	@Override
	protected void actualizarInterfaz() { }

	/* Métodos Listener onClick */

	private class OnPlayVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			imagenPlay.setVisibility(View.INVISIBLE);
			canvas.iniciarVideo();
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
	public void onPlaySoundEffect(int sound)
	{
		mCallback.onVideoPlaySoundEffect(sound);
	}
	
	@Override
	public void onChangeDialog(final int text, TEstadoVideo estado)
	{
		if (estado == TEstadoVideo.Outside || estado == TEstadoVideo.Door)
		{
			estadoDialogo = TEstadoDialogo.Escena;
		}
		else if (estado == TEstadoVideo.Rock)
		{
			estadoDialogo = TEstadoDialogo.Personaje;
		}
		else if (estado == TEstadoVideo.Noise || estado == TEstadoVideo.Brief)
		{
			estadoDialogo = TEstadoDialogo.Actor;
		}
		else
		{
			estadoDialogo = TEstadoDialogo.Nada;
		}
		
		getActivity().runOnUiThread(new Runnable() {
	        @Override
	        public void run()
	        {
	        	if (estadoDialogo == TEstadoDialogo.Escena)
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
	        	else if (estadoDialogo == TEstadoDialogo.Personaje)
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
	        	else if (estadoDialogo == TEstadoDialogo.Actor)
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
		if (estadoDialogo == TEstadoDialogo.Escena)
		{
			dialogoEscena.dismiss();
		}
		else if (estadoDialogo == TEstadoDialogo.Personaje)
		{
			dialogoPersonaje.dismiss();
		}
		else if (estadoDialogo == TEstadoDialogo.Actor)
		{
			dialogoActor.dismiss();
		}
	}
}
