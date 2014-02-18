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
	private ImageButton botonReady, botonNuevo, botonTest;
	
	private DesignDataSaved dataSaved;
	
	/* Constructora */
	
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
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_design_layout, container, false);
		
		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) rootView.findViewById(R.id.designGLSurfaceViewDesign1);
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonDesign1);
		botonNuevo = (ImageButton) rootView.findViewById(R.id.imageButtonDesign2);
		botonTest = (ImageButton) rootView.findViewById(R.id.imageButtonDesign3);
		
		botonReady.setOnClickListener(new OnReadyClickListener());
		botonNuevo.setOnClickListener(new onNewClickListener());
		botonTest.setOnClickListener(new onTestClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event)
			{
				canvas.onTouch(view, event);
				actualizarBotones();
				
				return true;
			}
		});
		
		actualizarBotones();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		canvas = null;
		botonReady = null;
		botonNuevo = null;
		botonTest = null;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
		
		if(dataSaved != null)
		{			
			canvas.restoreData(dataSaved);
			actualizarBotones();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
		
		dataSaved = canvas.saveData();
	}
	
	/* Métodos abstractos de OpenGLFragmentListener */
	
	private void actualizarBotones()
	{
		if(canvas.poligonoCompleto())
		{
			botonReady.setVisibility(View.VISIBLE);
			botonNuevo.setVisibility(View.VISIBLE);
			botonTest.setVisibility(View.VISIBLE);
		}
		else
		{
			botonReady.setVisibility(View.INVISIBLE);
			botonNuevo.setVisibility(View.INVISIBLE);
			botonTest.setVisibility(View.INVISIBLE);
		}
	}
	
	/* Listener de Botones */
	
	public class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onDesignReadyButtonClicked(canvas.getEsqueleto());
		}
	}
	
	private class onNewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			actualizarBotones();
		}
	}
	
	private class onTestClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			mCallback.onDesignTestButtonClicked(canvas.seleccionarTriangular());
		}
	}
}
