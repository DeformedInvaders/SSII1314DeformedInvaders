package com.view.select;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.R;
import com.view.display.DisplayGLSurfaceView;

public class SelectFragment extends Fragment
{
	private DisplayGLSurfaceView canvas;
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	
	public static final SelectFragment newInstance(Esqueleto e, Textura t)
	{
		SelectFragment fragment = new SelectFragment();
		fragment.setParameters(e, t);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t)
	{	
		esqueletoActual = e;
		texturaActual = t;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.fragment_select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (DisplayGLSurfaceView) rootView.findViewById(R.id.displayGLSurfaceViewSelect1);
		canvas.setParameters(esqueletoActual, texturaActual);
        
        return rootView;
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
		canvas.onPause();
	}
}
