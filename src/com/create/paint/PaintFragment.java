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
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
	private Esqueleto esqueletoActual;
	
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
		botonEye = (ImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonMano = (ImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonNext = (ImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonDelete = (ImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonReady = (ImageButton) rootView.findViewById(R.id.imageButtonPaint10);
		
		botonNext.setEnabled(false);
		botonPrev.setEnabled(false);
		botonDelete.setEnabled(false);
		
		botonPincel.setOnClickListener(new OnPaintPincelClickListener());	
		botonCubo.setOnClickListener(new OnPaintCuboClickListener());
		botonColor.setOnClickListener(new OnPaintColorClickListener());
		botonSize.setOnClickListener(new OnPaintSizeClickListener());
		botonEye.setOnClickListener(new OnPaintEyeClickListener());
		botonMano.setOnClickListener(new OnPaintManoClickListener());
		botonNext.setOnClickListener(new OnPaintNextClickListener());
		botonPrev.setOnClickListener(new OnPaintPrevClickListener());
		botonDelete.setOnClickListener(new OnPaintDeleteClickListener());
		botonReady.setOnClickListener(new OnPaintReadyClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(v, event);
				actualizarPaintBotones();
				return true;
			}
		});
		
		return rootView;
    }


	private void destroyPaintActivity()
	{
		canvas.capturaPantalla();
		
		mCallback.onPaintReadyButtonClicked(canvas.getTextura());
	}
	
	private class OnPaintPincelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();
		}
    }
    
    private class OnPaintCuboClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();
		}
    }
    
    private class OnPaintColorClickListener implements OnClickListener
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
    
    private class OnPaintSizeClickListener implements OnClickListener
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
    
    private class OnPaintEyeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			//Toast.makeText(getApplication(), "Textures", Toast.LENGTH_SHORT).show();
		}
	}
    
    private class OnPaintManoClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();
		}
    }
    
    private class OnPaintPrevClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.anteriorAccion();
			
			actualizarPaintBotones();
		}
    }
    
    private class OnPaintNextClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.siguienteAccion();
	
			actualizarPaintBotones();
		}
    }
    
    private class OnPaintDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
				
			actualizarPaintBotones();
		}
    }

    private class OnPaintReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			destroyPaintActivity();
		}
    }
	
	private void actualizarPaintBotones()
	{
		if(canvas.bufferSiguienteVacio())
		{
			botonNext.setEnabled(false);
		}
		else
		{
			botonNext.setEnabled(true);
		}
		
		if(canvas.bufferAnteriorVacio())
		{
			botonPrev.setEnabled(false);
			botonDelete.setEnabled(false);
		}
		else
		{
			botonPrev.setEnabled(true);
			botonDelete.setEnabled(true);
		}
	}

}
