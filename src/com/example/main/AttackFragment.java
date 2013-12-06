package com.example.main;

import com.example.animation.AttackGLSurfaceView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AttackFragment extends Fragment {

	private AttackGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.attack_layout, container, false);
        canvas = (AttackGLSurfaceView) rootView.findViewById(R.id.attackGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
        
        return rootView;
    }

	public void setEsqueleto(Esqueleto e) {
		
		esqueleto = e;
	}

}
