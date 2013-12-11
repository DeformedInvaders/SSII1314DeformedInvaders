package com.example.deform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;

public class DeformActivity extends Activity
{
	private InternalStorageManager manager;
	
	private DeformGLSurfaceView canvas;
	private ImageButton botonAdd, botonRemove, botonMover;
	
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
		botonAdd = (ImageButton) findViewById(R.id.imageButton11);
		botonRemove = (ImageButton) findViewById(R.id.imageButton12);
		botonMover = (ImageButton) findViewById(R.id.imageButton13);
		
		canvas = (DeformGLSurfaceView) findViewById(R.id.deformGLSurfaceView1);
		canvas.setEsqueleto(esqueleto);
		
		botonAdd.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarAnyadir();
			}
		});
		
		botonRemove.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarEliminar();
			}
		});
		
		botonMover.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarMover();
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
}
