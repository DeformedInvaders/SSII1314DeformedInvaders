package com.example.design;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.main.Esqueleto;
import com.example.main.InternalStorageManager;
import com.example.main.R;
import com.example.paint.PaintActivity;

public class DesignActivity extends Activity
{
	private InternalStorageManager manager;
	
	private DesignGLSurfaceView canvas;
	private ImageButton botonReady;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Recuperar Esqueleto de la Memoria Interna
		manager = new InternalStorageManager();
		
		// Seleccionar Animación entre Actividades
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
		// Seleccionar Layout
		setContentView(R.layout.design_layout);
		
		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) findViewById(R.id.designGLSurfaceView1);
		botonReady = (ImageButton) findViewById(R.id.imageButton0);
		
		botonReady.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
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
					
					Intent intent = new Intent(DesignActivity.this, PaintActivity.class);
					startActivity(intent);
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.design_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
	
		switch(item.getItemId())
		{
			case R.id.itemBSpline:
				canvas.calcularBSpline();
				Toast.makeText(getApplication(), "B-Spline", Toast.LENGTH_SHORT).show();
			break;
			case R.id.itemConvexHull:
				canvas.calcularConvexHull();
				Toast.makeText(getApplication(), "Convex Hull", Toast.LENGTH_SHORT).show();	
			break;
			case R.id.itemDelaunay:
				canvas.calcularDelaunay();
				Toast.makeText(getApplication(), "Delaunay Triangulator", Toast.LENGTH_SHORT).show();
			break;
			case R.id.itemEarClipping:
				canvas.calcularEarClipping();
				Toast.makeText(getApplication(), "Ear Clipping Triangulator", Toast.LENGTH_SHORT).show();
			break;
			case R.id.itemMesh:
				canvas.calcularMeshTriangles();
				Toast.makeText(getApplication(), "Delaunay Mesh Generator", Toast.LENGTH_SHORT).show();
			break;
			case R.id.itemSimple:
				if(canvas.calcularTestSimple())
				{
					Toast.makeText(getApplication(), "The Polygon is Simple", Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
				}
			break;
			case R.id.itemNew:
				canvas.reiniciar();
			break;
			case R.id.itemFull:
				if(!canvas.pruebaCompleta())
				{
					Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
				}
			break;
		}
	
		return true;
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
