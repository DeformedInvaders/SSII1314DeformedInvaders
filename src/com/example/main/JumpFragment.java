package com.example.main;

import com.example.animation.JumpGLSurfaceView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class JumpFragment extends Fragment {

	private JumpGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.jump_layout, container, false);
        canvas = (JumpGLSurfaceView) rootView.findViewById(R.id.jumpGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
         
        return rootView;
    }
	
	public void setEsqueleto(Esqueleto e) {
		
		esqueleto = e;
	}

}
