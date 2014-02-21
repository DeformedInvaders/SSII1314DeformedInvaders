package com.create.deform;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.lib.utils.FloatArray;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.OpenGLFragment;
import com.project.main.R;

public class DeformFragment extends OpenGLFragment
{	
	private DeformGLSurfaceView canvas;
	private Esqueleto esqueleto;
	private Textura textura;
	
	private DeformDataSaved dataSaved;
	
	private ImageButton botonAnyadir, botonEliminar, botonDeformar, botonReiniciar, botonGrabar, botonAudio, botonReproducir;
		
	/* Constructora */
	
	public static final DeformFragment newInstance(Esqueleto e, Textura t)
	{
		DeformFragment fragment = new DeformFragment();
		fragment.setParameters(e, t);
		return fragment;
	}
	
	private void setParameters(Esqueleto e, Textura t)
	{	
		esqueleto = e;
		textura = t;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{        
		View rootView = inflater.inflate(R.layout.fragment_deform_layout, container, false);
		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) rootView.findViewById(R.id.deformGLSurfaceViewDeform1);
		canvas.setParameters(esqueleto, textura);
		
		botonAnyadir = (ImageButton) rootView.findViewById(R.id.imageButtonDeform1);
		botonEliminar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform2);
		botonDeformar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform3);
		botonReiniciar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform4);
		botonGrabar = (ImageButton) rootView.findViewById(R.id.imageButtonDeform5);
		botonAudio = (ImageButton) rootView.findViewById(R.id.imageButtonDeform6);
		botonReproducir = (ImageButton) rootView.findViewById(R.id.imageButtonDeform7);
		
		botonAnyadir.setOnClickListener(new OnAddClickListener());
		botonEliminar.setOnClickListener(new OnRemoveClickListener());
		botonDeformar.setOnClickListener(new OnMoveClickListener());
		botonReiniciar.setOnClickListener(new OnResetClickListener());
		botonGrabar.setOnClickListener(new OnRecordClickListener());
		botonAudio.setOnClickListener(new OnAudioClickListener());
		botonReproducir.setOnClickListener(new OnPlayClickListener());
		
		setCanvasListener(canvas);
		
		reiniciarInterfaz();
		actualizarInterfaz();
        return rootView;
    }
	
	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		
		botonAnyadir = null;
		botonEliminar = null;
		botonDeformar = null;
		botonReiniciar = null;
		botonGrabar = null;
		botonReproducir = null;
		botonAudio = null;
	}
	
	@Override	
	public void onDetach()
	{
		super.onDetach();
		
		canvas = null;
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
	
	/* Métodos abstractos de OpenGLFramgent */
	
	@Override
	protected void actualizarInterfaz()
	{
		if(canvas.isHandlesVacio())
		{
			botonAnyadir.setVisibility(View.VISIBLE);
		}
		else 
		{
			botonGrabar.setVisibility(View.VISIBLE);
			
			if(!canvas.isEstadoGrabacion())
			{
				botonAnyadir.setVisibility(View.VISIBLE);
				botonEliminar.setVisibility(View.VISIBLE);
				botonDeformar.setVisibility(View.VISIBLE);
				botonReiniciar.setVisibility(View.VISIBLE);
				botonAudio.setVisibility(View.VISIBLE);
					
				//TODO audio ready
				if(canvas.isGrabacionReady())
				{
					botonReproducir.setVisibility(View.VISIBLE);
				}
			}
		}
		
		if(canvas.isEstadoAnyadir())
		{
			botonAnyadir.setBackgroundResource(R.drawable.icon_add_selected);
		}
		else if(canvas.isEstadoEliminar())
		{
			botonEliminar.setBackgroundResource(R.drawable.icon_remove_selected);
		}
		else if(canvas.isEstadoGrabacion())
		{
			botonGrabar.setBackgroundResource(R.drawable.icon_record_selected);
		}
		else if(canvas.isEstadoDeformar())
		{
			botonDeformar.setBackgroundResource(R.drawable.icon_deform_selected);
		}
		else if(canvas.isEstadoAudio())
		{
			botonAudio.setBackgroundResource(R.drawable.icon_microphone_selected);
		}
		else if(canvas.isEstadoReproduccion())
		{
			botonReproducir.setBackgroundResource(R.drawable.icon_play_selected);
		}
	}
	
	@Override
	protected void reiniciarInterfaz()
	{
		botonAnyadir.setVisibility(View.INVISIBLE);
		botonEliminar.setVisibility(View.INVISIBLE);
		botonDeformar.setVisibility(View.INVISIBLE);
		botonReiniciar.setVisibility(View.INVISIBLE);
		botonGrabar.setVisibility(View.INVISIBLE);
		botonReproducir.setVisibility(View.INVISIBLE);
		botonAudio.setVisibility(View.INVISIBLE);
		
		botonAnyadir.setBackgroundResource(R.drawable.icon_add);
		botonEliminar.setBackgroundResource(R.drawable.icon_remove);
		botonDeformar.setBackgroundResource(R.drawable.icon_deform);
		botonGrabar.setBackgroundResource(R.drawable.icon_record);
		botonAudio.setBackgroundResource(R.drawable.icon_microphone);
		botonReproducir.setBackgroundResource(R.drawable.icon_play);
	}
	
	/* Listener de Botones */
	
	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnyadir();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	private class OnRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarEliminar();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	private class OnMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMover();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	private class OnResetClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	private class OnRecordClickListener implements OnClickListener 
	{ 
		public void onClick(View v)
		{
			canvas.seleccionarGrabado();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	//TODO
	private class OnAudioClickListener implements OnClickListener 
	{ 
		public void onClick(View v)
		{
			canvas.seleccionarAudio();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	private class OnPlayClickListener implements OnClickListener 
	{ 
		public void onClick(View v)
		{
			canvas.seleccionarPlay();
			
			reiniciarInterfaz();
			actualizarInterfaz();
		}	
	}
	
	/* Métodos de Obtención de Información */
	
	public List<FloatArray> getMovimientos() 
	{ 
		if(canvas != null)
		{
			return canvas.getMovimientos();
		}
		
		return null;
	}
}
