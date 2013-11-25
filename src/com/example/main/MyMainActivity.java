package com.example.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MyMainActivity extends Activity
{
	private MyGLSurfaceView canvas;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_layout);
		
		canvas = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_manu, menu);
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
			case R.id.itemZoomIn:
				canvas.zoom(0.8f);
			break;
			case R.id.itemZoomOut:
				canvas.zoom(1.2f);
			break;
			case R.id.itemDragRight:
				canvas.drag(-0.1f, 0.0f);
			break;
			case R.id.itemDragLeft:
				canvas.drag(0.1f, 0.0f);
			break;
			case R.id.itemDragUp:
				canvas.drag(0.0f, -0.1f);
			break;
			case R.id.itemDragDown:
				canvas.drag(0.0f, 0.1f);
			break;
			case R.id.itemReset:
				canvas.restore();
			break;
			case R.id.itemNew:
				canvas.reiniciarPuntos();
			break;
			case R.id.itemRun:
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
