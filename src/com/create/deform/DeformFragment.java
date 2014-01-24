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

import com.example.data.Esqueleto;
import com.example.main.R;

public class DeformFragment extends Fragment
{
	private DeformGLSurfaceView canvas;
	private Esqueleto esqueleto;
	
	private ImageButton botonDeformAdd, botonDeformRemove, botonDeformMover, botonDeformDelete;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
        View rootView = inflater.inflate(R.layout.deform_layout, container, false);
 		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
		
		botonDeformAdd = (ImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		botonDeformRemove = (ImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		botonDeformMover = (ImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonDeformDelete = (ImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		
		//botonDeformRemove.setVisibility(View.INVISIBLE);
		//botonDeformMover.setVisibility(View.INVISIBLE);
		//botonDeformDelete.setVisibility(View.INVISIBLE);
		
		botonDeformAdd.setOnClickListener(new OnDeformAddClickListener());
		botonDeformRemove.setOnClickListener(new OnDeformRemoveClickListener());
		botonDeformMover.setOnClickListener(new OnDeformMoveClickListener());
		botonDeformDelete.setOnClickListener(new OnDeformDeleteClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(event);
				actualizarDeformBotones();
				return true;
			}
		});
        
        return rootView;
    }

	public void setEsqueleto(Esqueleto e)
	{	
		esqueleto = e;
	}
	
	/* DEFORM ACTIVITY */
	
	private void actualizarDeformBotones()
	{
		if(((DeformGLSurfaceView) canvas).handlesVacio())
		{
			botonDeformRemove.setVisibility(View.INVISIBLE);
			botonDeformMover.setVisibility(View.INVISIBLE);
			botonDeformDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonDeformRemove.setVisibility(View.VISIBLE);
			botonDeformMover.setVisibility(View.VISIBLE);
			botonDeformDelete.setVisibility(View.VISIBLE);
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
