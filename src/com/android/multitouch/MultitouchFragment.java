package com.android.multitouch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.main.R;

public class MultitouchFragment extends Fragment
{
	private MultitouchGLSurfaceView canvas;
	
	public static final MultitouchFragment newInstance()
	{
		MultitouchFragment fragment = new MultitouchFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_multitouch_layout, container, false);
		
		// Instanciar Elementos de la GUI
		canvas = (MultitouchGLSurfaceView) rootView.findViewById(R.id.multitouchGLSurfaceView1);
		
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
