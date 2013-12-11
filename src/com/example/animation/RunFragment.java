package com.example.animation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.Esqueleto;
import com.example.main.R;

public class RunFragment extends Fragment {
	
	private RunGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.run_layout, container, false);
        canvas = (RunGLSurfaceView) rootView.findViewById(R.id.runGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
        
        return rootView;
    }

	public void setEsqueleto(Esqueleto e) {
		
		esqueleto = e;
	}
}
