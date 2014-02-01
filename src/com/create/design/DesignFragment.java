package com.create.design;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.project.data.Esqueleto;
import com.project.main.R;

public class DesignFragment extends Fragment
{
	private DesignFragmentListener mCallback;
	
	private DesignGLSurfaceView canvas;
	private ImageButton botonDesignReady, botonDesignNuevo, botonDesignTest;
	
	public static final DesignFragment newInstance()
	{
		DesignFragment fragment = new DesignFragment();
		return fragment;
	}
	
	public interface DesignFragmentListener
	{
        public void onDesignReadyButtonClicked(Esqueleto e);
        public void onDesignTestButtonClicked(boolean test);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (DesignFragmentListener) activity;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_design_layout, container, false);
		
		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) rootView.findViewById(R.id.designGLSurfaceViewDesign1);
		botonDesignReady = (ImageButton) rootView.findViewById(R.id.imageButtonDesign1);
		botonDesignNuevo = (ImageButton) rootView.findViewById(R.id.imageButtonDesign2);
		botonDesignTest = (ImageButton) rootView.findViewById(R.id.imageButtonDesign3);
		
		botonDesignReady.setOnClickListener(new OnDesignReadyClickListener());
		botonDesignNuevo.setOnClickListener(new onDesignNewClickListener());
		botonDesignTest.setOnClickListener(new onDesignTestClickListener());
		
		botonDesignReady.setEnabled(false);
		botonDesignNuevo.setEnabled(false);
		botonDesignTest.setEnabled(false);
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(event);
				actualizarDesignBotones();
				
				return true;
			}
		});
        return rootView;
    }
	
	private void actualizarDesignBotones()
	{
		if(canvas.poligonoCompleto())
		{
			botonDesignReady.setEnabled(true);
			botonDesignNuevo.setEnabled(true);
			botonDesignTest.setEnabled(true);
		}
		else
		{
			botonDesignReady.setEnabled(false);
			botonDesignNuevo.setEnabled(false);
			botonDesignTest.setEnabled(false);
		}
	}
	
	public class OnDesignReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onDesignReadyButtonClicked(canvas.getEsqueleto());
		}
	}
	
	private class onDesignNewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			actualizarDesignBotones();
		}
	}
	
	private class onDesignTestClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onDesignTestButtonClicked(canvas.pruebaCompleta());
		}
	}
}
