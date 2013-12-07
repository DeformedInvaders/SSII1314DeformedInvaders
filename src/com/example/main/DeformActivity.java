package com.example.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.deform.DeformGLSurfaceView;

public class DeformActivity extends Activity
{
	private String FILENAME;
	
	private DeformGLSurfaceView canvas;
	private ImageButton botonAdd, botonRemove, botonMover;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
				
		Bundle bundle = getIntent().getExtras();
		FILENAME = bundle.getString("Esqueleto");
		Esqueleto e = null;

		FileInputStream file;
		ObjectInputStream data;
		try
		{
			file = openFileInput(FILENAME);
			data = new ObjectInputStream(file);
			e = (Esqueleto) data.readObject();
			data.close();
			file.close();
		}
		catch (FileNotFoundException e1)
		{
			Toast.makeText(getApplication(), "File not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			Toast.makeText(getApplication(), "IO Exception", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "IO EXCEPTION");
			e1.printStackTrace();
		}
		catch (ClassNotFoundException e1)
		{
			Toast.makeText(getApplication(), "Objecto not found", Toast.LENGTH_SHORT).show();
			Log.d("TEST", "CLASS NOT FOUND EXCEPTION");
			e1.printStackTrace();
		}
		
		setContentView(R.layout.deform_layout);
		
		botonAdd = (ImageButton) findViewById(R.id.imageButton11);
		botonRemove = (ImageButton) findViewById(R.id.imageButton12);
		botonMover = (ImageButton) findViewById(R.id.imageButton13);
		
		canvas = (DeformGLSurfaceView) findViewById(R.id.deformGLSurfaceView1);
		canvas.setEsqueleto(e);
		
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
