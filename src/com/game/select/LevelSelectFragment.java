package com.game.select;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.view.OpenGLFragment;
import com.game.data.Nivel;
import com.project.main.R;

public class LevelSelectFragment extends OpenGLFragment
{
	private OnLevelListener listener;

	private boolean lockNivel;
	
	private Typeface fuenteNivel;
	private TTipoLevel tipoNivel;
	private int fondoNivel, descripcionNivel, colorTextoNivel;

	private ImageButton botonNivel;

	/* Constructora */

	public static final LevelSelectFragment newInstance(OnLevelListener l, Nivel nivel, boolean lock)
	{
		LevelSelectFragment fragment = new LevelSelectFragment();
		fragment.setParameters(l, nivel.getFondoNivel(), nivel.getDescripcionNivel(), nivel.getColorTextoNivel(), nivel.getTipoNivel(), nivel.getFuenteNivel(), lock);
		return fragment;
	}

	private void setParameters(OnLevelListener l, int fondo, int descripcion, int color, TTipoLevel tipo, Typeface fuente, boolean lock)
	{
		listener = l;

		fondoNivel = fondo;
		descripcionNivel = descripcion;
		colorTextoNivel = color;
		tipoNivel = tipo;
		lockNivel = lock;
		fuenteNivel = fuente;
	}

	/* Métodos Fragment */

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_game_select_layout, container, false);

		// Instanciar Elementos de la GUI
		ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect1);
		imageBackground.setBackgroundResource(fondoNivel);

		TextView textBackground = (TextView) rootView.findViewById(R.id.textViewLevelSelect1);
		textBackground.setText(getString(descripcionNivel));
		textBackground.setTextColor(colorTextoNivel);
		textBackground.setTypeface(fuenteNivel);

		botonNivel = (ImageButton) rootView.findViewById(R.id.imageButtonLevel1);
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

	/* Métodos abstractos de OpenGLFragment */

	@Override
	protected void reiniciarInterfaz()
	{
		botonNivel.setBackgroundResource(R.drawable.icon_level_locked);
	}

	@Override
	protected void actualizarInterfaz()
	{
		if (lockNivel)
		{
			botonNivel.setBackgroundResource(R.drawable.icon_level_unlocked);
		}
	}

	/* Métodos Listener onClick */

	private class OnLevelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (lockNivel)
			{
				listener.onLevelSelected(tipoNivel);
			}
			else
			{
				Toast.makeText(getActivity(), R.string.text_level_disabled, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
