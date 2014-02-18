package com.create.deform;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lib.utils.FloatArray;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.R;

public class DeformFragment extends Fragment
{	
	private DeformGLSurfaceView canvas;
	private Esqueleto esqueletoActual;
	private Textura texturaActual;
	
	private DeformDataSaved dataSaved;
	
	private ImageButton botonAdd, botonRemove, botonMove, botonDelete, botonRecord;
		
	/* Constructora */
	
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
		botonMove = (ImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		botonRecord = (ImageButton) rootView.findViewById(R.id.imageButtonDeform5);
		
		botonAdd.setOnClickListener(new OnAddClickListener());
		botonRemove.setOnClickListener(new OnRemoveClickListener());
		botonMove.setOnClickListener(new OnMoveClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonRecord.setOnClickListener(new OnRecordClickListener());
		
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
		botonAdd = null;
		botonRemove = null;
		botonMove = null;
		botonDelete = null;
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
			reiniciarImagenesBotones(dataSaved.getEstado());
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		canvas.onPause();
		
		dataSaved = canvas.saveData();
	}
	
	/* Métodos abstractos de OpenGLFramgent */
	
	private void actualizarBotones()
	{
		if(canvas.handlesVacio())
		{
			botonRemove.setVisibility(View.INVISIBLE);
			botonMove.setVisibility(View.INVISIBLE);
			botonDelete.setVisibility(View.INVISIBLE);
			botonRecord.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonRemove.setVisibility(View.VISIBLE);
			botonMove.setVisibility(View.VISIBLE);
			botonDelete.setVisibility(View.VISIBLE);
			botonRecord.setVisibility(View.VISIBLE);
			
			if(canvas.getEstadoGrabacion())
			{	
				botonRemove.setVisibility(View.INVISIBLE);
				botonMove.setVisibility(View.INVISIBLE);
				botonDelete.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private void reiniciarImagenesBotones()
	{
		botonAdd.setBackgroundResource(R.drawable.icon_add);
		botonRemove.setBackgroundResource(R.drawable.icon_remove);
		botonMove.setBackgroundResource(R.drawable.icon_hand);
	}
	
	private void reiniciarImagenesBotones(TDeformEstado estado)
	{
		reiniciarImagenesBotones();
		
		switch(estado)
		{
			case Anyadir:
				botonAdd.setBackgroundResource(R.drawable.icon_add_selected);
			break;
			case Eliminar:
				botonRemove.setBackgroundResource(R.drawable.icon_remove_selected);
			break;
			case Deformar:
				botonMove.setBackgroundResource(R.drawable.icon_hand_selected);
			break;
			default:
			break;
		}
	}
	
	/* Listener de Botones */
	
	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnyadir();
			
			reiniciarImagenesBotones();
			botonAdd.setBackgroundResource(R.drawable.icon_add_selected);
		}	
	}
	
	private class OnRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarEliminar();
			
			reiniciarImagenesBotones();
			botonRemove.setBackgroundResource(R.drawable.icon_remove_selected);
		}	
	}
	
	private class OnMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMover();
			
			reiniciarImagenesBotones();
			botonMove.setBackgroundResource(R.drawable.icon_hand_selected);
		}	
	}
	
	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			
			reiniciarImagenesBotones();
			actualizarBotones();
		}	
	}
	
	private class OnRecordClickListener implements OnClickListener 
	{ 
		public void onClick(View v)
		{
			canvas.seleccionarGrabado();
			
			reiniciarImagenesBotones();
			botonRecord.setBackgroundResource(R.drawable.icon_record_selected);
		}	
	}
	
	/* Métodos de Obtención de Información */
	
	public List<FloatArray> getMovimientos() 
	{ 
		return canvas.getMovimientos();
	}
}
