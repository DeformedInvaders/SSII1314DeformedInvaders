package com.android.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.project.main.R;

public abstract class ColorDialog extends WindowDialog
{
	private float[] colorActual = new float[3];

	private ColorPalette palleteMain;
	private ImageView palleteSecondary;

	private Button buttonOk, buttonCancel;
	private ImageView imageCursorMain, imageCursorSecondary, imageColorSelected;

	/* Constructora */

	public ColorDialog(Context context)
	{
		super(context, R.layout.dialog_color_layout, true);

		buttonOk = (Button) findViewById(R.id.imageButtonColor1);
		buttonCancel = (Button) findViewById(R.id.imageButtonColor2);

		buttonOk.setOnClickListener(new OnOkClickListener());
		buttonCancel.setOnClickListener(new OnCancelClickListener());

		palleteMain = (ColorPalette) findViewById(R.id.paletteColor1);
		palleteSecondary = (ImageView) findViewById(R.id.paletteColor2);

		palleteMain.setOnTouchListener(new OnPalleteMainTouchListener());
		palleteSecondary.setOnTouchListener(new OnPalleteSecondaryTouchListener());

		imageCursorMain = (ImageView) findViewById(R.id.imageViewColor1);
		imageCursorSecondary = (ImageView) findViewById(R.id.imageViewColor2);
		imageColorSelected = (ImageView) findViewById(R.id.imageViewColor3);

		Color.colorToHSV(Color.RED, colorActual);
		moveCursorMain();
		moveCursorSecondary();
		imageColorSelected.setBackgroundColor(getColor());

		// Posicion inicial de los Cursores
		final ViewTreeObserver vto = getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout()
			{
				moveCursorMain();
				moveCursorSecondary();
				removeGlobalLayoutListener(this);
			}
		});
	}

	/* Métodos Abstractos */

	public abstract void onColorSelected(int color);

	/* Métodos Listener onClick */

	private class OnOkClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			onColorSelected(getColor());
			dismiss();
		}
	}

	private class OnCancelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			dismiss();
		}
	}

	/* Métodos Privados */

	private int getColor()
	{
		return Color.HSVToColor(colorActual);
	}

	private float getHue()
	{
		return colorActual[0];
	}

	private float getSat()
	{
		return colorActual[1];
	}

	private float getVal()
	{
		return colorActual[2];
	}

	private void setHue(float hue)
	{
		colorActual[0] = hue;
	}

	private void setSat(float sat)
	{
		colorActual[1] = sat;
	}

	private void setVal(float val)
	{
		colorActual[2] = val;
	}

	private void moveCursorMain()
	{
		float x = getSat() * palleteMain.getMeasuredWidth();
		float y = (1.0f - getVal()) * palleteMain.getMeasuredHeight();

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageCursorMain.getLayoutParams();
		layoutParams.leftMargin = (int) (palleteMain.getLeft() + x - Math.floor(imageCursorMain.getMeasuredWidth() / 2));
		layoutParams.topMargin = (int) (palleteMain.getTop() + y - Math.floor(imageCursorMain.getMeasuredHeight() / 2));
		imageCursorMain.setLayoutParams(layoutParams);
	}

	private void moveCursorSecondary()
	{
		float y = palleteSecondary.getMeasuredHeight() - (getHue() * palleteSecondary.getMeasuredHeight() / 360.f);
		if (y == palleteSecondary.getMeasuredHeight())
		{
			y = 0.0f;
		}
		
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageCursorSecondary.getLayoutParams();
		layoutParams.leftMargin = (int) (palleteSecondary.getLeft() - Math.floor(imageCursorSecondary.getMeasuredWidth() / 2));
		layoutParams.topMargin = (int) (palleteSecondary.getTop() + y - Math.floor(imageCursorSecondary.getMeasuredHeight() / 2));
		imageCursorSecondary.setLayoutParams(layoutParams);
	}

	/* Métodos Listener onTouch */

	private class OnPalleteMainTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int action = event.getAction();

			if (action == MotionEvent.ACTION_MOVE|| action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
				float x = event.getX();
				float y = event.getY();

				// Control de Dimensiones

				if (x < 0)
				{
					x = 0;
				}
				
				if (y < 0)
				{
					y = 0;
				}

				if (x > palleteMain.getMeasuredWidth())
				{
					x = palleteMain.getMeasuredWidth();
				}
				
				if (y > palleteMain.getMeasuredHeight())
				{
					y = palleteMain.getMeasuredHeight();
				}

				setSat(1.f / palleteMain.getMeasuredWidth() * x);
				setVal(1.f - (1.f / palleteMain.getMeasuredHeight() * y));

				// Actualizar la vista

				moveCursorMain();
				imageColorSelected.setBackgroundColor(getColor());

				return true;
			}

			return false;
		}
	}

	private class OnPalleteSecondaryTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View arg0, MotionEvent event)
		{
			int action = event.getAction();

			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
				float y = event.getY();

				// Control de Dimensiones

				if (y < 0)
				{
					y = 0;
				}

				if (y > palleteSecondary.getMeasuredHeight())
				{
					y = palleteSecondary.getMeasuredHeight() - 0.001f;
				}

				float hue = 360.f - 360.f / palleteSecondary.getMeasuredHeight() * y;
				if (hue == 360.f)
				{
					hue = 0.f;
				}
				
				setHue(hue);

				// Actualizar la vista

				palleteMain.setHue(getHue());
				moveCursorSecondary();
				imageColorSelected.setBackgroundColor(getColor());

				return true;
			}

			return false;
		}
	}
}
