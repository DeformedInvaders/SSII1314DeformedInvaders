package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ZoomControls;

public class MyMainActivity extends Activity {

	private MyGLSurfaceView canvas;
	private Button boton_bspline, boton_convexhull, boton_delaunay, boton_earclipping, boton_mesh, boton_reiniciar;
	private ZoomControls zoom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		canvas = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
		
		boton_bspline = (Button) findViewById(R.id.button1);
		boton_convexhull = (Button) findViewById(R.id.button2);
		boton_delaunay = (Button) findViewById(R.id.button3);
		boton_earclipping = (Button) findViewById(R.id.button4);
		boton_reiniciar = (Button) findViewById(R.id.button5);
		boton_mesh = (Button) findViewById(R.id.button6);
		
		zoom = (ZoomControls) findViewById(R.id.zoomControls1);
		
		boton_bspline.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.calcularBSpline();
				Toast.makeText(getApplication(), "B-Spline", Toast.LENGTH_SHORT).show();
			}
		});
		
		boton_convexhull.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.calcularConvexHull();
				Toast.makeText(getApplication(), "Convex Hull", Toast.LENGTH_SHORT).show();	
			}
		});
		
		boton_delaunay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.calcularDelaunay();
				Toast.makeText(getApplication(), "Delaunay", Toast.LENGTH_SHORT).show();
			}
		});
		
		boton_earclipping.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.calcularEarClipping();
				Toast.makeText(getApplication(), "Ear Clipping", Toast.LENGTH_SHORT).show();
			}
		});

		boton_mesh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				canvas.calcularMeshTriangles();
				Toast.makeText(getApplication(), "Mesh Triangles", Toast.LENGTH_SHORT).show();
			}
		});
		
		boton_reiniciar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				canvas.reiniciarPuntos();
				Toast.makeText(getApplication(), "Reiniciado", Toast.LENGTH_SHORT).show();
			}
		});
		
		zoom.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canvas.zoom(0.8f);
			}
		});
		
		zoom.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				canvas.zoom(1.2f);
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
