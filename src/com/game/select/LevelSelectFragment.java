package com.game.select;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.view.OpenGLFragment;
import com.project.main.R;

public class LevelSelectFragment extends OpenGLFragment
{	
	private ImageButton botonNivel;
	
	private boolean nivelBloqueado;
	private int imageLevel, textTitle, textColor;
	
	/* SECTION Constructora */
	
	public static final LevelSelectFragment newInstance(boolean bloqueado, TLevelTipo tipo)
	{
		LevelSelectFragment fragment = new LevelSelectFragment();
		
		switch(tipo)
		{
			case Moon:
				fragment.setParameters(bloqueado, R.drawable.background_moon, R.string.text_level_section_moon, Color.WHITE);
			break;
			case NewYork:
				fragment.setParameters(bloqueado, R.drawable.background_newyork, R.string.text_level_section_newyork, Color.BLACK);
			break;
			case Rome:
				fragment.setParameters(bloqueado, R.drawable.background_rome, R.string.text_level_section_rome, Color.WHITE);
			break;
			case Egypt:
				fragment.setParameters(bloqueado, R.drawable.background_egypt, R.string.text_level_section_egypt, Color.BLACK);
			break;
			case Stonehenge:
				fragment.setParameters(bloqueado, R.drawable.background_stonehenge, R.string.text_level_section_stonehenge, Color.BLACK);
			break;
		}
		
		return fragment;
	}
	
	private void setParameters(boolean bloqueado, int level, int text, int color)
	{
		nivelBloqueado = bloqueado;
		imageLevel = level;
		textTitle = text;
		textColor = color;
	}

	/* SECTION Métodos Fragment */
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_game_level_select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
        ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect1);
        imageBackground.setBackgroundResource(imageLevel);
        
        TextView textBackground = (TextView) rootView.findViewById(R.id.textViewLevelSelect1);
        textBackground.setText(getString(textTitle));
        textBackground.setTextColor(textColor);
        
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
	
	/* SECTION Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz() { }
	
	@Override
	protected void actualizarInterfaz() { }
	
	/* SECTION Métodos Listener onClick */
	
	private class OnLevelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{			
			if(nivelBloqueado)
			{
				Toast.makeText(getActivity(), R.string.text_level_enabled, Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(getActivity(), R.string.text_level_disabled, Toast.LENGTH_SHORT).show();
			}
		}
    }
	
}
