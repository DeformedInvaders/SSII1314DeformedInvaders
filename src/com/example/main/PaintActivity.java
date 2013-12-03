package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.paint.PaintGLSurfaceView;

public class PaintActivity extends Activity
{
	private PaintGLSurfaceView canvas;
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady, botonColor, botonSize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		Esqueleto e = (Esqueleto) bundle.get("Esqueleto");
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.paint_layout);
		
		botonPincel = (ImageButton) findViewById(R.id.imageButton1);
		botonCubo = (ImageButton) findViewById(R.id.imageButton2);
		botonMano = (ImageButton) findViewById(R.id.imageButton3);
		botonPrev = (ImageButton) findViewById(R.id.imageButton4);
		botonNext = (ImageButton) findViewById(R.id.imageButton5);
		botonDelete = (ImageButton) findViewById(R.id.imageButton6);
		botonReady = (ImageButton) findViewById(R.id.imageButton7);
		botonColor = (ImageButton) findViewById(R.id.imageButton8);
		botonSize = (ImageButton) findViewById(R.id.imageButton9);
		
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		canvas.setEsqueleto(e);
		
		botonPincel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarPincel();
			}
		});
		
		botonCubo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarCubo();
			}
		});
		
		botonColor.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//TODO Lanzar RGB Picker
				canvas.seleccionarColor();
			}
		});
		
		botonSize.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//TODO Lanzar Size Picker
				canvas.seleccionarSize();
			}
		});
		
		botonMano.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarMano();
			}
		});
		
		botonNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.siguienteAccion();
			}
		});
		
		botonPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.anteriorAccion();
			}
		});
		
		botonDelete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.reiniciar();
				Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonReady.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				// TODO Obtener BMP, Procesar Coordenadas de Textura, Intent a Animaciones
			    canvas.testBitMap();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		canvas.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		canvas.onPause();
	}
}
