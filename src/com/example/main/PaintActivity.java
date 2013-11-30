package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.paint.PaintGLSurfaceView;
import com.example.utils.FloatArray;
import com.example.utils.ShortArray;

public class PaintActivity extends Activity
{
	private PaintGLSurfaceView canvas;
	private Button botonPincel, botonCubo, botonMano, botonNext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		FloatArray puntos = (FloatArray) bundle.get("Puntos");
		ShortArray triangulos = (ShortArray) bundle.get("Triangulos");
		
		setContentView(R.layout.paint_layout);
		
		botonPincel = (Button) findViewById(R.id.button2);
		botonCubo = (Button) findViewById(R.id.button3);
		botonMano = (Button) findViewById(R.id.button4);
		botonNext = (Button) findViewById(R.id.button5);
		
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		canvas.setEsqueleto(puntos, triangulos);
		
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
				// TODO Obtener BMP, Procesar Coordenadas de Textura
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
