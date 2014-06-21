package com.game.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.view.IconImageButton;
import com.android.view.OpenGLFragment;
import com.game.data.Level;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.project.main.R;

public class LevelSelectFragment extends OpenGLFragment
{
	private OnLevelListener mListener;

	private GameStatistics mStatistics;
	private Level mLevel;

	private IconImageButton buttonLevel;

	/* Constructora */

	public static final LevelSelectFragment newInstance(OnLevelListener listener, Level level, GameStatistics statistics)
	{
		LevelSelectFragment fragment = new LevelSelectFragment();
		fragment.setParameters(listener, level, statistics);
		return fragment;
	}

	private void setParameters(OnLevelListener listener, Level level, GameStatistics statistics)
	{
		mListener = listener;
		mLevel = level;
		mStatistics = statistics;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_select_layout, container, false);

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect1);
		imageBackground.setBackgroundResource(mLevel.getLevelBackground());
		
		ImageView imagenCompleted = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect2);
		if(mStatistics.isCompleted())
		{
			imagenCompleted.setBackgroundResource(mLevel.getLevelImageCompleted());
			imagenCompleted.setVisibility(View.VISIBLE);
		}
		
		ImageView imagenPerfected = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect3);
		if(mStatistics.isPerfected())
		{
			imagenPerfected.setBackgroundResource(mLevel.getLevelImagePerfected());
			imagenPerfected.setVisibility(View.VISIBLE);
		}
		
		ImageView imagenMastered = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect4);
		if(mStatistics.isMastered())
		{
			imagenMastered.setBackgroundResource(mLevel.getLevelImageMastered());
			imagenMastered.setVisibility(View.VISIBLE);
		}
		
		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewLevelSelect1);
		textBackground.setText(getString(mLevel.getLevelDescription()));
		textBackground.setTextColor(mLevel.getLevelColor());
		textBackground.setTypeface(mLevel.getLevelFont());

		buttonLevel = (IconImageButton) rootView.findViewById(R.id.imageButtonLevel1);
		buttonLevel.setOnClickListener(new OnLevelClickListener());

		resetInterface();
		updateInterface();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		buttonLevel = null;
	}

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void resetInterface() { }

	@Override
	protected void updateInterface()
	{
		buttonLevel.setActivo(GamePreferences.IS_DEBUG_ENABLED() || mStatistics.isUnlocked());
	}

	/* Métodos Listener onClick */

	private class OnLevelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (GamePreferences.IS_DEBUG_ENABLED() || mStatistics.isUnlocked())
			{
				mListener.onLevelSelected(mLevel.getLevelType());
			}
			else
			{
				sendToastMessage(R.string.text_level_disabled);
			}
		}
	}

}
