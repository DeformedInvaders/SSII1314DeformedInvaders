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
	private ImageButton botonPincel, botonCubo, botonMano, botonNext, botonPrev, botonDelete, botonReady;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		Esqueleto e = (Esqueleto) bundle.get("Esqueleto");
		
		setContentView(R.layout.paint_layout);
		
		botonPincel = (ImageButton) findViewById(R.id.imageButton1);
		botonCubo = (ImageButton) findViewById(R.id.imageButton2);
		botonMano = (ImageButton) findViewById(R.id.imageButton3);
		botonPrev = (ImageButton) findViewById(R.id.imageButton4);
		botonNext = (ImageButton) findViewById(R.id.imageButton5);
		botonDelete = (ImageButton) findViewById(R.id.imageButton6);
		botonReady = (ImageButton) findViewById(R.id.imageButton7);
		
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		canvas.setEsqueleto(e);
		
		botonPincel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarPincel();
				//TODO Lanzar Size Picker y RGB Picker
				Toast.makeText(getApplication(), "Pincel Seleccionado", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonCubo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				canvas.seleccionarCubo();
				//TODO Lanzar RGB Picker
				Toast.makeText(getApplication(), "Cubo Seleccionado", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonMano.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				canvas.seleccionarMano();
				Toast.makeText(getApplication(), "Mano Seleccionada", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				canvas.siguienteAccion();
				Toast.makeText(getApplication(), "Next", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				canvas.anteriorAccion();
				Toast.makeText(getApplication(), "Previous", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonDelete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				canvas.reiniciar();
				Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonReady.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Obtener BMP, Procesar Coordenadas de Textura, Intent a Animaciones
			    //View content = findViewById(R.id.designGLSurfaceView1);
			    //Bitmap bitmap = content.getDrawingCache();
				Toast.makeText(getApplication(), "Coming Soon!", Toast.LENGTH_SHORT).show();
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
