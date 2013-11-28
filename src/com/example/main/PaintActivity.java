package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.paint.PaintGLSurfaceView;

public class PaintActivity extends Activity {

	private PaintGLSurfaceView canvas;
	private Button botonPincel, botonCubo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.paint_layout);
		
		botonPincel = (Button) findViewById(R.id.button2);
		botonCubo = (Button) findViewById(R.id.button3);
		
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		
		botonPincel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplication(), "Pincel Seleccionado", Toast.LENGTH_SHORT).show();
			}
		});
		
		botonCubo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplication(), "Cubo Seleccionado", Toast.LENGTH_SHORT).show();
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
