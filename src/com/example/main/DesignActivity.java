package com.example.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.design.DesignGLSurfaceView;

public class DesignActivity extends Activity
{
	private DesignGLSurfaceView canvas;
	private ImageButton botonReady;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.design_layout);
		
		canvas = (DesignGLSurfaceView) findViewById(R.id.designGLSurfaceView1);
		botonReady = (ImageButton) findViewById(R.id.imageButton0);
		
		botonReady.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				//boolean b = canvas.pruebaCompleta();
				
				//if(b) {
					Esqueleto e = canvas.getPruebaCompleta();
					if(e != null)
					{
						Intent intent = new Intent(DesignActivity.this, PaintActivity.class);
						intent.putExtra("Esqueleto", e);
						startActivity(intent);
					}
				//}
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
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);

	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
	    {
	        Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
	    }
	    else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
	    {
	        Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
	    }
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
