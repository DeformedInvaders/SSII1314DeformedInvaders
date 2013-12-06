package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.example.deform.DeformGLSurfaceView;

public class DeformActivity extends Activity
{
	private DeformGLSurfaceView canvas;
	private ImageButton botonAdd, botonRemove, botonMover;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		Esqueleto e = (Esqueleto) bundle.get("Esqueleto");
		
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
