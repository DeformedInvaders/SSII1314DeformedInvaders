package com.example.deform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;

public class DeformActivity extends Activity
{
	private InternalStorageManager manager;
	
	private DeformGLSurfaceView canvas;
	private ImageButton botonAdd, botonRemove, botonMover, botonDelete;
	
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
		setContentView(R.layout.deform_layout);
		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) findViewById(R.id.deformGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
		
		botonAdd = (ImageButton) findViewById(R.id.imageButton11);
		botonRemove = (ImageButton) findViewById(R.id.imageButton12);
		botonMover = (ImageButton) findViewById(R.id.imageButton13);
		botonDelete = (ImageButton) findViewById(R.id.imageButton14);
		
		//botonRemove.setVisibility(View.INVISIBLE);
		//botonMover.setVisibility(View.INVISIBLE);
		//botonDelete.setVisibility(View.INVISIBLE);
		
		botonAdd.setOnClickListener(new OnAddClickListener());
		botonRemove.setOnClickListener(new OnRemoveClickListener());
		botonMover.setOnClickListener(new OnMoveClickListener());
		botonDelete.setOnClickListener(new OnDeleteClickListener());
		
		
		
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
		if(canvas.handlesVacio())
		{
			//botonRemove.setVisibility(View.INVISIBLE);
			//botonMover.setVisibility(View.INVISIBLE);
			//botonDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			//botonRemove.setVisibility(View.VISIBLE);
			//botonMover.setVisibility(View.VISIBLE);
			//botonDelete.setVisibility(View.VISIBLE);
		}
	}
	
	private class OnAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarAnyadir();
		}	
	}
	
	private class OnRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarEliminar();
		}	
	}
	
	private class OnMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.seleccionarMover();
		}	
	}
	
	private class OnDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			canvas.reiniciar();
			
			actualizarBotones();
		}	
	}
}
