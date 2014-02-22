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
	
	private ColorPalette paletaPrincipal;
	private ImageView paletaSecundaria;
	
	private Button botonAceptar, botonCancelar;
	private ImageView imagenCursorPrincipal, imagenCursorSecundario, imagenSeleccionado;
	
	/* SECTION Constructora */
	
	public ColorDialog(Context context)
	{
		super(context, R.layout.dialog_color_layout);
		
		botonAceptar = (Button) findViewById(R.id.imageButtonColor1);
		botonCancelar = (Button) findViewById(R.id.imageButtonColor2);
		
		botonAceptar.setOnClickListener(new OnAceptarClickListener());
		botonCancelar.setOnClickListener(new OnCancelarClickListener());
		
		paletaPrincipal = (ColorPalette) findViewById(R.id.paletteColor1);
		paletaSecundaria = (ImageView) findViewById(R.id.paletteColor2);
		
		paletaPrincipal.setOnTouchListener(new OnPaletaPrincipalTouchListener());
		paletaSecundaria.setOnTouchListener(new OnPaletaSecundariaTouchListener());
		
		imagenCursorPrincipal = (ImageView) findViewById(R.id.imageViewColor1);
		imagenCursorSecundario = (ImageView) findViewById(R.id.imageViewColor2);
		imagenSeleccionado = (ImageView) findViewById(R.id.imageViewColor3);
		
		Color.colorToHSV(Color.RED, colorActual);
		moverCursorPrincipal();
		moverCursorSecundario();
		imagenSeleccionado.setBackgroundColor(getColor());
		
        // Posicion inicial de los Cursores
        final ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            
        	@Override 
            public void onGlobalLayout()
            {
        		moverCursorPrincipal();
        		moverCursorSecundario();
                removeGlobalLayoutListener(this);
            }
        });
	}
	
	/* SECTION Métodos Abstractos */
	
	public abstract void onColorSelected(int color);
	
	/* SECTION Métodos Abstractos WindowDialog */
	
	@Override
	protected void onTouchOutsidePopUp(View v, MotionEvent event)
	{
		dismiss();
	}
	
	/* SECTION Métodos Listener onClick */
	
	private class OnAceptarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			onColorSelected(getColor());
			dismiss();
		}
	}
	
	private class OnCancelarClickListener implements OnClickListener
	{
		@Override
		public void onClick(View arg0)
		{
			dismiss();
		}
	}
	
	/* SECTION Métodos Privados */
	
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
	
	private void moverCursorPrincipal()
	{
        float x = getSat() * paletaPrincipal.getMeasuredWidth();
        float y = (1.0f - getVal()) * paletaPrincipal.getMeasuredHeight();
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagenCursorPrincipal.getLayoutParams();
        layoutParams.leftMargin = (int) (paletaPrincipal.getLeft() + x - Math.floor(imagenCursorPrincipal.getMeasuredWidth() / 2));
        layoutParams.topMargin = (int) (paletaPrincipal.getTop() + y - Math.floor(imagenCursorPrincipal.getMeasuredHeight() / 2));
        imagenCursorPrincipal.setLayoutParams(layoutParams);		
	}
	
	private void moverCursorSecundario()
	{
	    float y = paletaSecundaria.getMeasuredHeight() - (getHue() * paletaSecundaria.getMeasuredHeight() / 360.f);
	    if (y == paletaSecundaria.getMeasuredHeight()) y = 0.0f;
	    
	    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imagenCursorSecundario.getLayoutParams();
	    layoutParams.leftMargin = (int) (paletaSecundaria.getLeft() - Math.floor(imagenCursorSecundario.getMeasuredWidth() / 2));
	    layoutParams.topMargin = (int) (paletaSecundaria.getTop() + y - Math.floor(imagenCursorSecundario.getMeasuredHeight() / 2));
	    imagenCursorSecundario.setLayoutParams(layoutParams);
	}
	
	/* SECTION Métodos Listener onTouch */
	
	private class OnPaletaPrincipalTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
				float x = event.getX();
                float y = event.getY();

                // Control de Dimensiones
                
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                
                if (x > paletaPrincipal.getMeasuredWidth()) x = paletaPrincipal.getMeasuredWidth();
                if (y > paletaPrincipal.getMeasuredHeight()) y = paletaPrincipal.getMeasuredHeight();

                setSat(1.f / paletaPrincipal.getMeasuredWidth() * x);
                setVal(1.f - (1.f / paletaPrincipal.getMeasuredHeight() * y));

                // Actualizar la vista
                
                moverCursorPrincipal();
                imagenSeleccionado.setBackgroundColor(getColor());

                return true;
            }
            
            return false;
		}
	}
	
	private class OnPaletaSecundariaTouchListener implements OnTouchListener
	{
		@Override
		public boolean onTouch(View arg0, MotionEvent event)
		{
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP)
			{
                float y = event.getY();
                
                // Control de Dimensiones
                
                if (y < 0) y = 0;
                
                if (y > paletaSecundaria.getMeasuredHeight()) y = paletaSecundaria.getMeasuredHeight() - 0.001f;
                
                float hue = 360.f - 360.f / paletaSecundaria.getMeasuredHeight() * y;
                if (hue == 360.f) hue = 0.f;
                setHue(hue);

                // Actualizar la vista
                
                paletaPrincipal.setHue(getHue());
                moverCursorSecundario();
                imagenSeleccionado.setBackgroundColor(getColor());
              
                return true;
            }
			
            return false;
		}
	}
}
