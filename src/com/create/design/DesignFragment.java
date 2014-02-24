package com.create.design;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.touch.TTouchEstado;
import com.project.data.Esqueleto;
import com.project.main.OpenGLFragment;
import com.project.main.R;

public class DesignFragment extends OpenGLFragment
{
	private DesignFragmentListener mCallback;
	
	private DesignGLSurfaceView canvas;
	private ImageButton botonReset, botonTriangular, botonListo;
	
	private DesignDataSaved dataSaved;
	
	/* SECTION Constructora */
	
	public static final DesignFragment newInstance()
	{
		DesignFragment fragment = new DesignFragment();
		return fragment;
	}
	
	public interface DesignFragmentListener
	{
        public void onDesignReadyButtonClicked(Esqueleto e);
    }
	
	/* SECTION Métodos Fragment */
	
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
		
		botonListo = (ImageButton) rootView.findViewById(R.id.imageButtonDesign1);
		botonReset = (ImageButton) rootView.findViewById(R.id.imageButtonDesign2);
		botonTriangular = (ImageButton) rootView.findViewById(R.id.imageButtonDesign3);
		
		botonListo.setOnClickListener(new OnReadyClickListener());
		botonReset.setOnClickListener(new onResetClickListener());
		botonTriangular.setOnClickListener(new onTriangularClickListener());
		
		setCanvasListener(canvas);
		
		reiniciarInterfaz();
		actualizarInterfaz();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		canvas = null;
		botonListo = null;
		botonReset = null;
		botonTriangular = null;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
		
		if(dataSaved != null)
		{			
			canvas.restoreData(dataSaved);

			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
		
		dataSaved = canvas.saveData();
	}
	
	/* SECTION Métodos Abstractos OpenGLFragment */
	
	@Override
	protected void reiniciarInterfaz()
	{
		botonListo.setVisibility(View.INVISIBLE);
		botonReset.setVisibility(View.INVISIBLE);
		botonTriangular.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void actualizarInterfaz()
	{
		if(canvas.isPoligonoCompleto())
		{
			botonListo.setVisibility(View.VISIBLE);
			botonReset.setVisibility(View.VISIBLE);
			botonTriangular.setVisibility(View.VISIBLE);
		}
	}
	
	/* SECTION Métodos Listener onClick */
	
	public class OnReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(canvas.seleccionarTriangular())
			{
				canvas.setEstado(TTouchEstado.CoordDetectors);
				
				if(canvas.seleccionarRetoque())
				{
					mCallback.onDesignReadyButtonClicked(canvas.getEsqueleto());
				}
				else
				{
					Toast.makeText(getActivity(), R.string.error_retouch, Toast.LENGTH_SHORT).show();
				}
			}
			else
			{
				canvas.setEstado(TTouchEstado.SimpleTouch);
				
				Toast.makeText(getActivity(), R.string.error_triangle, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private class onResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			canvas.setEstado(TTouchEstado.SimpleTouch);
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}
	
	private class onTriangularClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(!canvas.seleccionarTriangular())
			{
				Toast.makeText(getActivity(), R.string.error_triangle, Toast.LENGTH_SHORT).show();
			}
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}
	}
}
