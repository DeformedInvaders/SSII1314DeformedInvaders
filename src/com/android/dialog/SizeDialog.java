package com.android.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.creation.paint.TTypeSize;
import com.project.main.R;

public abstract class SizeDialog extends WindowDialog
{
	private Button buttonEnlarge, buttonShrink;
	private ImageView imagePencil;
	private TTypeSize sizeActual;

	/* Constructora */

	public SizeDialog(Context context)
	{
		super(context, R.layout.dialog_size_layout, true);

		sizeActual = TTypeSize.Small;

		buttonEnlarge = (Button) findViewById(R.id.imageButtonSize1);
		buttonShrink = (Button) findViewById(R.id.imageButtonSize2);
		imagePencil = (ImageView) findViewById(R.id.imageButtonSize3);

		buttonEnlarge.setOnClickListener(new OnEnlargeClickListener());
		buttonShrink.setOnClickListener(new OnShrinkClickListener());
		imagePencil.setOnClickListener(new OnPencilClickListener());

		updateButtons();
	}

	/* Métodos Abstractos */

	public abstract void onSizeSelected(TTypeSize size);

	/* Métodos Privados */

	private void updateButtons()
	{
		if (sizeActual.ordinal() > 0)
		{
			buttonShrink.setEnabled(true);
		}
		else
		{
			buttonShrink.setEnabled(false);
		}

		if (sizeActual.ordinal() < 2)
		{
			buttonEnlarge.setEnabled(true);
		}
		else
		{
			buttonEnlarge.setEnabled(false);
		}
		
		imagePencil.setBackgroundResource(sizeActual.getImage());
	}

	/* Métodos Listener onClick */

	private class OnEnlargeClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			TTypeSize[] tipoSize = TTypeSize.values();
			sizeActual = tipoSize[sizeActual.ordinal() + 1];
			updateButtons();
		}
	}

	private class OnShrinkClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			TTypeSize[] tipoSize = TTypeSize.values();
			sizeActual = tipoSize[sizeActual.ordinal() - 1];
			updateButtons();
		}
	}

	private class OnPencilClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			onSizeSelected(sizeActual);
			dismiss();
		}
	}
}
