package com.create.paint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.android.dialog.ColorPicker;
import com.android.dialog.SizePicker;
import com.project.data.Esqueleto;
import com.project.data.Textura;
import com.project.main.R;

public class PaintFragment extends Fragment
{
	private PaintFragmentListener mCallback;
	private Context mContext;
	
	private PaintGLSurfaceView canvas;
	private ColorPicker colorPicker;
	private SizePicker sizePicker;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonPegatina;
	
	private Esqueleto esqueletoActual;
	
	/* Constructora */
	
	public static final PaintFragment newInstance(Esqueleto e)
	{
		PaintFragment fragment = new PaintFragment();
		fragment.setParameters(e);
		return fragment;
	}
	
	private void setParameters(Esqueleto e)
	{
		esqueletoActual = e;
	}
	
	public interface PaintFragmentListener
	{
        public void onPaintReadyButtonClicked(Textura t);
    }
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		mCallback = (PaintFragmentListener) activity;
		mContext = activity.getApplicationContext();
	}
	
	@Override
	public void onDetach()
	{
		super.onDetach();
		mCallback = null;
		mContext = null;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Seleccionar Layout
		View rootView = inflater.inflate(R.layout.fragment_paint_layout, container, false);

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) rootView.findViewById(R.id.paintGLSurfaceViewPaint1);
		canvas.setParameters(esqueletoActual);
		
		botonPincel = (ImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		botonCubo = (ImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		botonColor = (ImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		botonSize = (ImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		botonPegatina = (ImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonMano = (ImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonNext = (ImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonPaint10);

		botonPincel.setOnClickListener(new OnPincelClickListener());	
		botonCubo.setOnClickListener(new OnCuboClickListener());
		botonColor.setOnClickListener(new OnColorClickListener());
		botonSize.setOnClickListener(new OnSizeClickListener());
		botonPegatina.setOnClickListener(new OnPegatinaClickListener());
		botonMano.setOnClickListener(new OnManoClickListener());
		botonNext.setOnClickListener(new OnNextClickListener());
		botonPrev.setOnClickListener(new OnPrevClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonReady.setOnClickListener(new OnReadyClickListener());
		
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
		colorPicker = null;
		sizePicker = null;
		botonPincel = null;
		botonCubo = null;
		botonMano = null;
		botonNext = null;
		botonPrev = null;
		botonDelete = null;
		botonReady = null;
		botonColor = null;
		botonSize = null;
		botonPegatina = null;		
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
	
	/* Métodos abstractos de OpenGLFragment */
	
	private void actualizarBotones()
	{
		if(canvas.bufferSiguienteVacio())
		{
			botonNext.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonNext.setVisibility(View.VISIBLE);
		}
		
		if(canvas.bufferAnteriorVacio())
		{
			botonPrev.setVisibility(View.INVISIBLE);
			botonDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonPrev.setVisibility(View.VISIBLE);
			botonDelete.setVisibility(View.VISIBLE);
		}
		
		if(canvas.pegatinaAnyadida())
		{
			reiniciarImagenesBotones();
		}
	}
	
	private void reiniciarImagenesBotones()
	{
		botonPincel.setBackgroundResource(R.drawable.icon_pencil);
		botonCubo.setBackgroundResource(R.drawable.icon_bucket);
		botonMano.setBackgroundResource(R.drawable.icon_hand);
		botonPegatina.setBackgroundResource(R.drawable.icon_eye);
	}
	
	/* Listener de Botones */
	
	private class OnPincelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();
			
			reiniciarImagenesBotones();
			botonPincel.setBackgroundResource(R.drawable.icon_pencil_selected);
		}
    }
    
    private class OnCuboClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();
			
			reiniciarImagenesBotones();
			botonCubo.setBackgroundResource(R.drawable.icon_bucket_selected);
		}
    }
    
    private class OnColorClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			int color = canvas.getColorPaleta();
			canvas.seleccionarColor(color);
			
			if (colorPicker == null)
			{
				colorPicker = new ColorPicker(mContext, ColorPicker.VERTICAL, canvas, color);    	
			}
			colorPicker.show(v);
		}
    }
    
    private class OnSizeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if (sizePicker == null) 
			{
				sizePicker = new SizePicker(mContext, SizePicker.VERTICAL, canvas);    	
			}
			sizePicker.show(v);
		}
    }
    
    private class OnPegatinaClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			//TODO
			canvas.seleccionarPegatina(0);
			
			reiniciarImagenesBotones();
			botonPegatina.setBackgroundResource(R.drawable.icon_eye_selected);
		}
	}
    
    private class OnManoClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();
			
			reiniciarImagenesBotones();
			botonMano.setBackgroundResource(R.drawable.icon_hand_selected);
		}
    }
    
    private class OnPrevClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.anteriorAccion();
			
			actualizarBotones();
		}
    }
    
    private class OnNextClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.siguienteAccion();
	
			actualizarBotones();
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

    private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.capturaPantalla();
			
			mCallback.onPaintReadyButtonClicked(canvas.getTextura());
		}
    }
}
