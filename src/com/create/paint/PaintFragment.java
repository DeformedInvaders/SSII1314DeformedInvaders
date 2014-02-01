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
	private ImageButton botonPaintPincel, botonPaintCubo, botonPaintMano, botonPaintNext, botonPaintPrev, botonPaintDelete, botonPaintReady, botonPaintColor, botonPaintSize, botonPaintEye;
	
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
		
		botonPaintPincel = (ImageButton) rootView.findViewById(R.id.imageButtonPaint1);
		botonPaintCubo = (ImageButton) rootView.findViewById(R.id.imageButtonPaint2);
		botonPaintColor = (ImageButton) rootView.findViewById(R.id.imageButtonPaint3);
		botonPaintSize = (ImageButton) rootView.findViewById(R.id.imageButtonPaint4);
		botonPaintEye = (ImageButton) rootView.findViewById(R.id.imageButtonPaint5);
		botonPaintMano = (ImageButton) rootView.findViewById(R.id.imageButtonPaint6);
		botonPaintPrev = (ImageButton) rootView.findViewById(R.id.imageButtonPaint7);
		botonPaintNext = (ImageButton) rootView.findViewById(R.id.imageButtonPaint8);
		botonPaintDelete = (ImageButton) rootView.findViewById(R.id.imageButtonPaint9);
		botonPaintReady = (ImageButton) rootView.findViewById(R.id.imageButtonPaint10);
		
		botonPaintNext.setEnabled(false);
		botonPaintPrev.setEnabled(false);
		botonPaintDelete.setEnabled(false);
		
		botonPaintPincel.setOnClickListener(new OnPaintPincelClickListener());	
		botonPaintCubo.setOnClickListener(new OnPaintCuboClickListener());
		botonPaintColor.setOnClickListener(new OnPaintColorClickListener());
		botonPaintSize.setOnClickListener(new OnPaintSizeClickListener());
		botonPaintEye.setOnClickListener(new OnPaintEyeClickListener());
		botonPaintMano.setOnClickListener(new OnPaintManoClickListener());
		botonPaintNext.setOnClickListener(new OnPaintNextClickListener());
		botonPaintPrev.setOnClickListener(new OnPaintPrevClickListener());
		botonPaintDelete.setOnClickListener(new OnPaintDeleteClickListener());
		botonPaintReady.setOnClickListener(new OnPaintReadyClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(event);
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
			botonPaintNext.setEnabled(false);
		}
		else
		{
			botonPaintNext.setEnabled(true);
		}
		
		if(canvas.bufferAnteriorVacio())
		{
			botonPaintPrev.setEnabled(false);
			botonPaintDelete.setEnabled(false);
		}
		else
		{
			botonPaintPrev.setEnabled(true);
			botonPaintDelete.setEnabled(true);
		}
	}

}
