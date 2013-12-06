package com.example.main;

import com.example.animation.DownGLSurfaceView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DownFragment extends Fragment {

	private DownGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.down_layout, container, false);
        canvas = (DownGLSurfaceView) rootView.findViewById(R.id.downGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
        
        return rootView;
    }

	public void setEsqueleto(Esqueleto e) {
		
		esqueleto = e;
	}

}
