package com.game.select;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.view.OpenGLFragment;
import com.project.main.R;

public class LevelSelectFragment extends OpenGLFragment
{
	private int level;
	private int text;
	
	/* SECTION Constructora */
	
	public static final LevelSelectFragment newInstance(int l, int t)
	{
		LevelSelectFragment fragment = new LevelSelectFragment();
		fragment.setParameters(l, t);
		return fragment;
	}
	
	private void setParameters(int l, int t)
	{
		level = l;
		text = t;
	}

	/* SECTION Métodos Fragment */
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_game_level_select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
        ImageView imageBackground = (ImageView) rootView.findViewById(R.id.imageViewLevelSelect1);
        imageBackground.setBackgroundResource(level);
        
        TextView textBackground = (TextView) rootView.findViewById(R.id.textViewLevelSelect1);
        textBackground.setText(getString(text));
		
		reiniciarInterfaz();
		actualizarInterfaz();
        return rootView;
    }
	
	/* SECTION Métodos abstractos de OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz() { }
	
	@Override
	protected void actualizarInterfaz() { }
	
}
