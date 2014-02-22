package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.project.main.R;

public abstract class SizeDialog extends WindowDialog
{
	private Button botonMas, botonMenos;
	private ImageView botonPincel;
	private int posicion;
	
	/* SECTION Constructora */
	
	public SizeDialog(Context context)
	{
		super(context, R.layout.dialog_size_layout);
		
		posicion = 0;
		
		botonMas = (Button) findViewById(R.id.imageButtonSize1);
		botonMenos = (Button) findViewById(R.id.imageButtonSize2);
		botonPincel = (ImageView) findViewById(R.id.imageButtonSize3);
		
		botonMas.setOnClickListener(new OnMasClickListener());
		botonMenos.setOnClickListener(new OnMenosClickListener());
		botonPincel.setOnClickListener(new OnPincelClickListener());
		
		actualizarBotones();
	}
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onSizeSelected(int size);
	
	/* SECTION Métodos Abstractos WindowDialog */
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	/* SECTION Métodos Privados */
	
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
		
		switch(posicion)
		{
			case 0:
				botonPincel.setBackgroundResource(R.drawable.image_size_small);
			break;
			case 1:
				botonPincel.setBackgroundResource(R.drawable.image_size_medium);
			break;
			case 2:
				botonPincel.setBackgroundResource(R.drawable.image_size_big);
			break;
		}
	}
	
	/* SECTION Métodos Listener onClick */
	
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
			onSizeSelected(posicion);
			dismiss();
		}
	}
}
