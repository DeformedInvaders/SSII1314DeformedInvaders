package com.view.select;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.data.Esqueleto;
import com.example.main.R;

public class SelectFragment extends Fragment
{
	private SelectGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.select_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (SelectGLSurfaceView) rootView.findViewById(R.id.selectGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
        
        return rootView;
    }

	public void setEsqueleto(Esqueleto e)
	{	
		esqueleto = e;
	}
}
