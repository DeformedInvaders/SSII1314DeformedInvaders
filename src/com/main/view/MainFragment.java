package com.main.view;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.character.display.DisplayOpenGLSurfaceView;
import com.character.display.OnDisplayListener;
import com.game.data.Character;
import com.main.model.GamePreferences;
import com.project.main.R;

public class MainFragment extends OpenGLFragment implements OnDisplayListener
{
	private MainFragmentListener mCallback;

	private DisplayOpenGLSurfaceView mCanvas;
	private IconImageButton buttonCreation, buttonImport, buttonPlay, buttonSelect, buttonVideo;

	private Character mCharacter;
	private int mNumCharacters, mNumFiles;

	/* Constructora */

	public static final MainFragment newInstance(MainFragmentListener callback, Character character, int numCharacters, int numFiles)
	{
		MainFragment fragment = new MainFragment();
		fragment.setParameters(callback, character, numCharacters, numFiles);
		return fragment;
	}

	private void setParameters(MainFragmentListener callback, Character character, int numCharacters, int numFiles)
	{
		mCallback = callback;
		mCharacter = character;
		mNumCharacters = numCharacters;
		mNumFiles = numFiles;
	}

	public interface MainFragmentListener
	{
		public void onMainImportCharacter();
		public void onMainCreateCharacter();
		public void onMainSelectCharacter();
		public void onMainPlayGame();
		public void onMainPlaySoundEffect(int sound);
		public void onMainPlayVideo();
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_project_main_layout, container, false);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.removeAllTabs();

		// Instanciar Elementos de la GUI
		mCanvas = (DisplayOpenGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewMain1);
		
		if (mCharacter != null)
		{
			mCanvas.setParameters(this, mCharacter, true);
		}
		else
		{
			mCanvas.setParameters();
		}
		
		ImageView fondo = (ImageView) rootView.findViewById(R.id.imageViewMain1);
		if (GamePreferences.IS_LONG_RATIO())
		{
			fondo.setBackgroundResource(R.drawable.background_long_main);
		}
		else
		{
			fondo.setBackgroundResource(R.drawable.background_notlong_main);
		}

		buttonCreation = (IconImageButton) rootView.findViewById(R.id.imageButtonMain1);
		buttonSelect = (IconImageButton) rootView.findViewById(R.id.imageButtonMain3);
		buttonPlay = (IconImageButton) rootView.findViewById(R.id.imageButtonMain2);
		buttonImport = (IconImageButton) rootView.findViewById(R.id.imageButtonMain4);
		buttonVideo = (IconImageButton) rootView.findViewById(R.id.imageButtonMain5);
		
		buttonCreation.setOnClickListener(new OnAddClickListener());
		buttonSelect.setOnClickListener(new OnViewClickListener());
		buttonPlay.setOnClickListener(new OnGameClickListener());
		buttonImport.setOnClickListener(new OnImportClickListener());
		buttonVideo.setOnClickListener(new OnVideoClickListener());

		setCanvasListener(mCanvas);

		resetInterface();
		updateInterface();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		mCanvas = null;
		buttonCreation = null;
		buttonPlay = null;
		buttonSelect = null;
		buttonImport = null;
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		
		mCallback = null;
		mCharacter = null;
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
		mCanvas.onPause();
	}

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void resetInterface()
	{
		buttonSelect.setVisibility(View.INVISIBLE);
		buttonPlay.setVisibility(View.INVISIBLE);
		buttonImport.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void updateInterface()
	{
		if (mNumCharacters > 0 && mNumCharacters <= GamePreferences.MAX_CHARACTERS)
		{
			buttonSelect.setVisibility(View.VISIBLE);
			
			if (mCharacter != null)
			{
				buttonPlay.setVisibility(View.VISIBLE);
			}
		}
		
		if (mNumFiles > 0)
		{
			buttonImport.setVisibility(View.VISIBLE);
		}
	}

	/* Métodos Listener onClick */

	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainCreateCharacter();
		}
	}

	private class OnViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainSelectCharacter();
		}
	}

	private class OnGameClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayGame();
		}
	}
	
	private class OnImportClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainImportCharacter();
		}
	}
	
	private class OnVideoClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onMainPlayVideo();
		}
	}
	
	/* Métodos de la interfaz OnDisplayListener */

	@Override
	public void onDisplayPlaySoundEffect(int sound)
	{
		mCallback.onMainPlaySoundEffect(sound);
	}
}
