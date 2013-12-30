package com.example.paint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.animation.MoveActivity;
import com.example.deform.DeformActivity;
import com.example.dialog.ColorPickerDialog;
import com.example.dialog.SizePicker;
import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;

public class PaintActivity extends Activity
{
	private InternalStorageManager manager;
    
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	private SizePicker quickAction;
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Recuperar Esqueleto de la Memoria Interna
		manager = new InternalStorageManager();

		Esqueleto esqueleto = null;
		try
		{
			FileInputStream file = openFileInput(manager.getFileName());
			esqueleto = manager.cargarEsqueleto(file);
			deleteFile(manager.getFileName());
		}
		catch (FileNotFoundException e1)
		{
			Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}

		// Seleccionar Animación entre Actividades
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		// Seleccionar Layout
		setContentView(R.layout.paint_layout);

		mContext = this;
		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
		
		botonPincel = (ImageButton) findViewById(R.id.imageButton1);
		botonCubo = (ImageButton) findViewById(R.id.imageButton2);
		botonMano = (ImageButton) findViewById(R.id.imageButton3);
		botonPrev = (ImageButton) findViewById(R.id.imageButton4);
		botonNext = (ImageButton) findViewById(R.id.imageButton5);
		botonDelete = (ImageButton) findViewById(R.id.imageButton6);
		botonReady = (ImageButton) findViewById(R.id.imageButton7);
		botonColor = (ImageButton) findViewById(R.id.imageButton8);
		botonSize = (ImageButton) findViewById(R.id.imageButton9);
		botonEye = (ImageButton) findViewById(R.id.imageButton10);
		
		//botonNext.setVisibility(View.INVISIBLE);
		//botonPrev.setVisibility(View.INVISIBLE);
		//botonDelete.setVisibility(View.INVISIBLE);
		
		botonPincel.setOnClickListener(new OnPincelClickListener());	
		botonCubo.setOnClickListener(new OnCuboClickListener());
		botonColor.setOnClickListener(new OnColorClickListener());
		botonSize.setOnClickListener(new OnSizeClickListener());
		botonEye.setOnClickListener(new OnEyeClickListener());
		botonMano.setOnClickListener(new OnManoClickListener());
		botonNext.setOnClickListener(new OnNextClickListener());
		botonPrev.setOnClickListener(new OnPrevClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		botonReady.setOnClickListener(new OnReadyClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				canvas.onTouch(event);
				actualizarBotones();
				return true;
			}
		});
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
	
	private void actualizarBotones()
	{
		if(canvas.bufferSiguienteVacio())
		{
			//botonNext.setVisibility(View.INVISIBLE);
		}
		else
		{
			//botonNext.setVisibility(View.VISIBLE);
		}
		
		if(canvas.bufferAnteriorVacio())
		{
			//botonPrev.setVisibility(View.INVISIBLE);
			//botonDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			//botonPrev.setVisibility(View.VISIBLE);
			//botonDelete.setVisibility(View.VISIBLE);
		}
	}


    private class OnPincelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarPincel();
		}
    }
    
    private class OnCuboClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarCubo();			
		}
    }
    
    private class OnColorClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			// El segundo parametro pasado al constructor es el color inicial, el cual el prefijo "0xff" se corresponde con el componente alfa
			// En este caso, el color inicial es el negro
	        ColorPickerDialog dialog = new ColorPickerDialog(mContext, 0xff000000, canvas);
	        dialog.show();
		}
    }
    
    private class OnSizeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if (quickAction == null) quickAction= new SizePicker(mContext, SizePicker.VERTICAL, canvas);    	
			quickAction.show(v);
		}
    }
    
    private class OnEyeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.capturaPantalla();
			Esqueleto esqueleto = canvas.getEsqueleto();
			if(esqueleto != null)
			{
				try
				{
					FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
					manager.guardarEsqueleto(file, esqueleto);
				}
				catch (FileNotFoundException e)
				{
					Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
					Log.d("TEST", "FILE NOT FOUND EXCEPTION");
					e.printStackTrace();
				}
				
				Intent intent = new Intent(PaintActivity.this, MoveActivity.class);
				startActivity(intent);
			}
		}
	}
    
    private class OnManoClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMano();
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
			Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
			
			actualizarBotones();
		}
    }

    private class OnReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			canvas.capturaPantalla();
			Esqueleto esqueleto = canvas.getEsqueleto();
			if(esqueleto != null)
			{
				try
				{
					FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
					manager.guardarEsqueleto(file, esqueleto);
				}
				catch (FileNotFoundException e)
				{
					Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
					Log.d("TEST", "FILE NOT FOUND EXCEPTION");
					e.printStackTrace();
				}
				
				Intent intent = new Intent(PaintActivity.this, DeformActivity.class);
				startActivity(intent);
			}
		}
    }
}
