package com.android.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.create.paint.PaintFragment;
import com.project.main.R;

public class SizePicker extends WindowPicker
{
	private PaintFragment fragmento;
	
	private Button botonMas, botonMenos;
	private ImageView botonPincel;
	private int posicion;
	
	public SizePicker(Context context, PaintFragment view)
	{
		super(context, R.layout.dialog_size_layout);
		
		fragmento = view; 
		posicion = 0;
		
		botonMas = (Button) findViewById(R.id.imageButtonSize1);
		botonMenos = (Button) findViewById(R.id.imageButtonSize2);
		botonPincel = (ImageView) findViewById(R.id.imageButtonSize3);
		
		botonMas.setOnClickListener(new OnMasClickListener());
		botonMenos.setOnClickListener(new OnMenosClickListener());
		botonPincel.setOnClickListener(new OnPincelClickListener());
		
		actualizarBotones();
	}
	
	private void actualizarBotones()
	{
		if(posicion > 0)
		{
			botonMenos.setEnabled(true);
		}
		else
		{
			botonMenos.setEnabled(false);
		}
		
		if(posicion < 2)
		{
			botonMas.setEnabled(true);
		}
		else
		{
			botonMas.setEnabled(false);
		}
		
		actualizarImagen();
	}
	
	private void actualizarImagen()
	{
		
		Resources resources = fragmento.getActivity().getResources();
		
		switch(posicion)
		{
			case 0:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_small));
			break;
			case 1:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_medium));
			break;
			case 2:
				botonPincel.setBackground(resources.getDrawable(R.drawable.image_size_big));
			break;
		}
	}
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	private class OnMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			posicion++;
			actualizarBotones();
		}
	}
	
	private class OnMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			posicion--;
			actualizarBotones();
		}
	}
	
	private class OnPincelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			fragmento.seleccionarSize(posicion);
			dismiss();
		}
	}
}
