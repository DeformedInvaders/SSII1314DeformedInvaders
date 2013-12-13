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
import com.example.dialog.ActionItem;
import com.example.dialog.ColorPickerDialog;
import com.example.dialog.ColorPickerDialog.OnColorPickerListener;
import com.example.dialog.QuickAction;
import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;

public class PaintActivity extends Activity
{
	private InternalStorageManager manager;
    
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize, botonEye;
	
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
		
		botonNext.setVisibility(View.INVISIBLE);
		botonPrev.setVisibility(View.INVISIBLE);
		botonDelete.setVisibility(View.INVISIBLE);
		
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
	}
	
	public void cargarColorpicker()
    {
	 	// El segundo parametro pasado al constructor es el color inicial, el cual el prefijo "0xff" se corresponde con el componente alpha
		// En este caso, el color inicial es el negro
        ColorPickerDialog dialog = new ColorPickerDialog(this, 0xff000000, new OnColorPickerListener()
    	{

        	// Si se pulsa el boton Cancelar
    		@Override
    		public void onCancel(ColorPickerDialog dialog){
    	
    		}
    		
    		// Si se pulsa el boton Aceptar
    		@Override
    		public void onOk(ColorPickerDialog dialog, int color) {
    			canvas.seleccionarColor(color);			
    		}
    			
    	});
        dialog.show();
    }
	
    
 	private static final int ID_FINO = 1;
 	private static final int ID_NORMAL = 2;
 	private static final int ID_ANCHO = 3;
    
    public void cargarSizePicker(View v)
    {
    	ActionItem finoItem = new ActionItem(ID_FINO, "1", getResources().getDrawable(R.drawable.linea1));
		ActionItem normalItem = new ActionItem(ID_NORMAL, "6", getResources().getDrawable(R.drawable.linea2));
		ActionItem anchoItem = new ActionItem(ID_ANCHO, "11", getResources().getDrawable(R.drawable.linea3));
		// Crear el  QuickAction,usando el parametro QuickAction.VERTICAL o QuickAction.HORIZONTAL para definir la orientacion del layout
		final QuickAction quickAction = new QuickAction(this, QuickAction.VERTICAL);

		// Añadir los action items al QuickAction
		quickAction.addActionItem(finoItem);
		quickAction.addActionItem(normalItem);
		quickAction.addActionItem(anchoItem);

		// Set listener para el action item clickado
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						ActionItem actionItem = quickAction.getActionItem(pos);
						canvas.seleccionarSize(pos);

						Toast.makeText(
								getApplicationContext(),
								"grosor " + actionItem.getTitle()
										+ " seleccionado", Toast.LENGTH_SHORT)
								.show();

					}
				});
    	 
		// set listener para el evento de descarte, este listener se llamará solo si el QuickAction dialog fuera descartado por pulsar fuera del area del dialogo.
		/*quickAction.setOnDismissListener(new QuickAction.OnDismissListener() {
			@Override
			public void onDismiss() {
				Toast.makeText(getApplicationContext(), "Dismissed",
						Toast.LENGTH_SHORT).show();
			}
		});*/
		
		quickAction.show(v);
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
			cargarColorpicker();
		}
    }
    
    private class OnSizeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			cargarSizePicker(v);
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
