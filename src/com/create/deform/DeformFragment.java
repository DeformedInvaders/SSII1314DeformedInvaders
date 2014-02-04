package com.create.deform;

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
import com.project.data.Textura;
import com.project.main.R;

public class DeformFragment extends Fragment
{
	private DeformGLSurfaceView canvas;
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	
	private DeformDataSaved dataSaved;
	
	private ImageButton botonAdd, botonRemove, botonMover, botonDelete;
		
	public static final DeformFragment newInstance(Esqueleto e, Textura t)
	{
		DeformFragment fragment = new DeformFragment();
		fragment.setParameters(e, t);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t)
	{	
		esqueletoActual = e;
		texturaActual = t;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_deform_layout, container, false);
		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceViewDeform1);
		canvas.setParameters(esqueletoActual, texturaActual);
		
		botonAdd = (ImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		botonRemove = (ImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		botonMover = (ImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		
		botonRemove.setEnabled(false);
		botonMover.setEnabled(false);
		botonDelete.setEnabled(false);
		
		botonAdd.setOnClickListener(new OnDeformAddClickListener());
		botonRemove.setOnClickListener(new OnDeformRemoveClickListener());
		botonMover.setOnClickListener(new OnDeformMoveClickListener());
		botonDelete.setOnClickListener(new OnDeformDeleteClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(v, event);
				actualizarDeformBotones();
				return true;
			}
		});
		
        return rootView;
    }
	
	@Override
	public void onResume()
	{
		super.onResume();
		canvas.onResume();
		
		if(dataSaved != null)
		{
			canvas.restoreData(dataSaved);
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
		
		dataSaved = canvas.saveData();
	}
	
	/* DEFORM ACTIVITY */
	
	private void actualizarDeformBotones()
	{
		if(canvas.handlesVacio())
		{
			botonRemove.setEnabled(false);
			botonMover.setEnabled(false);
			botonDelete.setEnabled(false);
		}
		else
		{
			botonRemove.setEnabled(true);
			botonMover.setEnabled(true);
			botonDelete.setEnabled(true);
		}
	}
	
	private class OnDeformAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnyadir();
		}	
	}
	
	private class OnDeformRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarEliminar();
		}	
	}
	
	private class OnDeformMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMover();
		}	
	}
	
	private class OnDeformDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			actualizarDeformBotones();
		}	
	}
}
