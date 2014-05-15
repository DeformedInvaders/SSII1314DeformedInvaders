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
import com.game.data.Nivel;
import com.main.model.GamePreferences;
import com.main.model.GameStatistics;
import com.project.main.R;

public class LevelSelectFragment extends OpenGLFragment
{
	private OnLevelListener mListener;

	private GameStatistics estadisticas;
	private Nivel nivel;

	private IconImageButton botonNivel;

	/* Constructora */

	public static final LevelSelectFragment newInstance(OnLevelListener l, Nivel n, GameStatistics e)
	{
		LevelSelectFragment fragment = new LevelSelectFragment();
		fragment.setParameters(l, n, e);
		return fragment;
	}

	private void setParameters(OnLevelListener l, Nivel n, GameStatistics e)
	{
		mListener = l;
		nivel = n;
		estadisticas = e;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_select_layout, container, false);

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect1);
		imageBackground.setBackgroundResource(nivel.getFondoNivel());
		
		ImageView imagenCompleted = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect2);
		if(estadisticas.isCompleted())
		{
			imagenCompleted.setBackgroundResource(nivel.getImagenCompleted());
			imagenCompleted.setVisibility(View.VISIBLE);
		}
		
		ImageView imagenPerfected = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect3);
		if(estadisticas.isPerfected())
		{
			imagenPerfected.setBackgroundResource(nivel.getImagenPerfected());
			imagenPerfected.setVisibility(View.VISIBLE);
		}
		
		ImageView imagenMastered = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect4);
		if(estadisticas.isMastered())
		{
			imagenMastered.setBackgroundResource(nivel.getImagenMastered());
			imagenMastered.setVisibility(View.VISIBLE);
		}
		
		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewLevelSelect1);
		textBackground.setText(getString(nivel.getDescripcionNivel()));
		textBackground.setTextColor(nivel.getColorTextoNivel());
		textBackground.setTypeface(nivel.getFuenteNivel());

		botonNivel = (IconImageButton) rootView.findViewById(R.id.imageButtonLevel1);
		botonNivel.setOnClickListener(new OnLevelClickListener());

		reiniciarInterfaz();
		actualizarInterfaz();
		return rootView;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		botonNivel = null;
	}
	
	/*@Override
	public void onDetach()
	{
		super.onDetach();

		mListener = null;
		estadisticas = null;
		nivel = null;
	}*/

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void reiniciarInterfaz() { }

	@Override
	protected void actualizarInterfaz()
	{
		botonNivel.setActivo(GamePreferences.IS_DEBUG_ENABLED() || estadisticas.isUnlocked());
	}

	/* Métodos Listener onClick */

	private class OnLevelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (GamePreferences.IS_DEBUG_ENABLED() || estadisticas.isUnlocked())
			{
				mListener.onLevelSelected(nivel.getTipoNivel());
			}
			else
			{
				sendToastMessage(R.string.text_level_disabled);
			}
		}
	}

}
