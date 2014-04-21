package com.android.dialog;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.creation.paint.TTipoSize;
import com.project.main.R;

public abstract class SizeDialog extends WindowDialog
{
	private Button botonMas, botonMenos;
	private ImageView botonPincel;
	private TTipoSize size;

	/* Constructora */

	public SizeDialog(Context context)
	{
		super(context, R.layout.dialog_size_layout);

		size = TTipoSize.Small;

		botonMas = (Button) findViewById(R.id.imageButtonSize1);
		botonMenos = (Button) findViewById(R.id.imageButtonSize2);
		botonPincel = (ImageView) findViewById(R.id.imageButtonSize3);

		botonMas.setOnClickListener(new OnMasClickListener());
		botonMenos.setOnClickListener(new OnMenosClickListener());
		botonPincel.setOnClickListener(new OnPincelClickListener());

		actualizarBotones();
	}

	/* Métodos Abstractos */

	public abstract void onSizeSelected(TTipoSize size);

	/* Métodos Abstractos WindowDialog */

	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event) { }

	/* Métodos Privados */

	private void actualizarBotones()
	{
		if (size.ordinal() > 0)
		{
			botonMenos.setEnabled(true);
		}
		else
		{
			botonMenos.setEnabled(false);
		}

		if (size.ordinal() < 2)
		{
			botonMas.setEnabled(true);
		}
		else
		{
			botonMas.setEnabled(false);
		}
		
		botonPincel.setBackgroundResource(size.getImage());
	}

	/* Métodos Listener onClick */

	private class OnMasClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			TTipoSize[] tipoSize = TTipoSize.values();
			size = tipoSize[size.ordinal() + 1];
			actualizarBotones();
		}
	}

	private class OnMenosClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			TTipoSize[] tipoSize = TTipoSize.values();
			size = tipoSize[size.ordinal() - 1];
			actualizarBotones();
		}
	}

	private class OnPincelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			onSizeSelected(size);
			dismiss();
		}
	}
}
