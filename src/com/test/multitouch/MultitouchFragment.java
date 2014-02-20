package com.test.multitouch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.main.OpenGLFragment;
import com.project.main.R;

public class MultitouchFragment extends OpenGLFragment
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
		
		setCanvasListener(canvas);
		
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

	@Override
	protected void reiniciarInterfaz() { }

	@Override
	protected void actualizarInterfaz() { }
}
