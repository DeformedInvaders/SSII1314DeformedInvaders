package com.example.main;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.deform.DeformGLSurfaceView;
import com.example.design.DesignGLSurfaceView;
import com.example.paint.PaintGLSurfaceView;

public class MainActivity extends Activity
{	
	private TEstado estado;
	private Esqueleto esqueleto;
	private GLSurfaceView canvas;

	/* LOADING ACTIVITY */
	//private Thread threadTimer;
	//private static final int segundos = 1;

	/* DESIGN ACTIVITY */
	private ImageButton botonDesignReady;
	
	/* PAINT ACTIVITY */
	//private ColorPicker colorPicker;
	//private SizePicker sizePicker;
	private ImageButton botonPaintPincel, botonPaintCubo, botonPaintMano, botonPaintNext, botonPaintPrev, botonPaintDelete, botonPaintReady, botonPaintColor, botonPaintSize, botonPaintEye;
	
	/* DEFORM ACTIVITY */
	private ImageButton botonDeformAdd, botonDeformRemove, botonDeformMover, botonDeformDelete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		createDesignActivity();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		if(estado != null && estado != TEstado.Loading)
		{
			canvas.onResume();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if(estado != null && estado != TEstado.Loading)
		{
			canvas.onPause();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.design_menu, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{    
	    menu.clear();    
	    if (estado == TEstado.Design)
	    {
	        getMenuInflater().inflate(R.menu.design_menu, menu);
	    } 
	    return super.onPrepareOptionsMenu(menu);
	}
	
	/* LOADING ACTIVITY */
	
	/*private void createLoadingActivity()
	{
		// Estado
		estado = TEstado.Loading;
		
		// Seleccionar Layout
		setContentView(R.layout.main_layout);
		
		threadTimer = new Thread() {
			@Override
			public void run()
			{
				try
				{
	                synchronized(this)
	                {
	                    wait(segundos*1000);
	                }
					
					// TODO
	                createDesignActivity();
				}
				catch(InterruptedException e)
				{ 
					e.printStackTrace();
				}             
			}
		};
		
		threadTimer.start(); 
	}	*/
	
	/* DESIGN ACTIVITY */
	
	private void createDesignActivity()
	{	
		// Estado
		estado = TEstado.Design;
		
		// Seleccionar Layout
		setContentView(R.layout.design_layout);
		
		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) findViewById(R.id.designGLSurfaceView1);
		botonDesignReady = (ImageButton) findViewById(R.id.imageButton0);
		
		botonDesignReady.setOnClickListener(new OnDesignReadyClickListener());
		
		//botonDesignReady.setVisibility(View.INVISIBLE);
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				((DesignGLSurfaceView) canvas).onTouch(event);
				actualizarDesignBotones();
				
				return true;
			}
		});
	}
	
	private void destroyDesignActiviy()
	{
		Esqueleto e = ((DesignGLSurfaceView) canvas).getEsqueleto();
		if(e != null)
		{					
			esqueleto = e;
			
			// TODO
			createPaintActivity();
		}
	}
	
	private void actualizarDesignBotones()
	{
		if(((DesignGLSurfaceView) canvas).poligonoCompleto())
		{
			botonDesignReady.setVisibility(View.VISIBLE);
		}
		else
		{
			botonDesignReady.setVisibility(View.INVISIBLE);
		}
	}
	
	private class OnDesignReadyClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyDesignActiviy();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
	
		if(estado == TEstado.Design)
		{
			switch(item.getItemId())
			{
				case R.id.itemBSpline:
					((DesignGLSurfaceView) canvas).calcularBSpline();
					Toast.makeText(getApplication(), "B-Spline", Toast.LENGTH_SHORT).show();
				break;
				case R.id.itemConvexHull:
					((DesignGLSurfaceView) canvas).calcularConvexHull();
					Toast.makeText(getApplication(), "Convex Hull", Toast.LENGTH_SHORT).show();	
				break;
				case R.id.itemDelaunay:
					((DesignGLSurfaceView) canvas).calcularDelaunay();
					Toast.makeText(getApplication(), "Delaunay Triangulator", Toast.LENGTH_SHORT).show();
				break;
				case R.id.itemEarClipping:
					((DesignGLSurfaceView) canvas).calcularEarClipping();
					Toast.makeText(getApplication(), "Ear Clipping Triangulator", Toast.LENGTH_SHORT).show();
				break;
				case R.id.itemMesh:
					((DesignGLSurfaceView) canvas).calcularMeshTriangles();
					Toast.makeText(getApplication(), "Delaunay Mesh Generator", Toast.LENGTH_SHORT).show();
				break;
				case R.id.itemSimple:
					if(((DesignGLSurfaceView) canvas).calcularTestSimple())
					{
						Toast.makeText(getApplication(), "The Polygon is Simple", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
					}
				break;
				case R.id.itemNew:
					((DesignGLSurfaceView) canvas).reiniciar();
					actualizarDesignBotones();
				break;
				case R.id.itemFull:
					if(!((DesignGLSurfaceView) canvas).pruebaCompleta())
					{
						Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
					}
				break;
			}
		}
	
		return true;
	}
	
	/* PAINT ACTIVITY */
	
	private void createPaintActivity()
	{
		// Estado
		estado = TEstado.Paint;
		
		// Seleccionar Layout
		setContentView(R.layout.paint_layout);

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		((PaintGLSurfaceView) canvas).setEsqueleto(esqueleto);
		
		botonPaintPincel = (ImageButton) findViewById(R.id.imageButton1);
		botonPaintCubo = (ImageButton) findViewById(R.id.imageButton2);
		botonPaintMano = (ImageButton) findViewById(R.id.imageButton3);
		botonPaintPrev = (ImageButton) findViewById(R.id.imageButton4);
		botonPaintNext = (ImageButton) findViewById(R.id.imageButton5);
		botonPaintDelete = (ImageButton) findViewById(R.id.imageButton6);
		botonPaintReady = (ImageButton) findViewById(R.id.imageButton7);
		botonPaintColor = (ImageButton) findViewById(R.id.imageButton8);
		botonPaintSize = (ImageButton) findViewById(R.id.imageButton9);
		botonPaintEye = (ImageButton) findViewById(R.id.imageButton10);
		
		//botonPaintNext.setVisibility(View.INVISIBLE);
		//botonPaintPrev.setVisibility(View.INVISIBLE);
		//botonPaintDelete.setVisibility(View.INVISIBLE);
		
		botonPaintPincel.setOnClickListener(new OnPaintPincelClickListener());	
		botonPaintCubo.setOnClickListener(new OnPaintCuboClickListener());
		botonPaintColor.setOnClickListener(new OnPaintColorClickListener());
		botonPaintSize.setOnClickListener(new OnPaintSizeClickListener());
		botonPaintEye.setOnClickListener(new OnPaintEyeClickListener());
		botonPaintMano.setOnClickListener(new OnPaintManoClickListener());
		botonPaintNext.setOnClickListener(new OnPaintNextClickListener());
		botonPaintPrev.setOnClickListener(new OnPaintPrevClickListener());
		botonPaintDelete.setOnClickListener(new OnPaintDeleteClickListener());
		botonPaintReady.setOnClickListener(new OnPaintReadyClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				((PaintGLSurfaceView) canvas).onTouch(event);
				actualizarPaintBotones();
				return true;
			}
		});
		
		//colorPicker = new ColorPicker();
		//sizePicker = new SizePicker();
	}
	
	private void destroyPaintActivity(int buttonId)
	{
		((PaintGLSurfaceView) canvas).capturaPantalla();
		Esqueleto e = ((PaintGLSurfaceView) canvas).getEsqueleto();
		if(e != null)
		{
			esqueleto = e;
			
			if(buttonId == botonPaintEye.getId())
			{
				//TODO
				//estado = TEstado.Anim;
				//recreate();
			}
			else if(buttonId == botonPaintReady.getId())
			{
				//TODO
				createDeformActivity();
			}
		}
	}
	
	private class OnPaintPincelClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).seleccionarPincel();
			}
		}
    }
    
    private class OnPaintCuboClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).seleccionarCubo();		
			}
		}
    }
    
    private class OnPaintColorClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).seleccionarColor();
				//colorPicker.cargarColorPicker();
				//canvasPaint.seleccionarColor(colorPicker.getColor());
			}
		}
    }
    
    private class OnPaintSizeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).seleccionarSize();
				//sizePicker.cargarSizePicker();
				//canvasPaint.seleccionarSize(colorPicker.getSize());
			}
		}
    }
    
    private class OnPaintEyeClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			destroyPaintActivity(v.getId());
		}
	}
    
    private class OnPaintManoClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).seleccionarMano();
			}
		}
    }
    
    private class OnPaintPrevClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).anteriorAccion();
				
				actualizarPaintBotones();
			}
		}
    }
    
    private class OnPaintNextClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).siguienteAccion();
	
				actualizarPaintBotones();
			}
		}
    }
    
    private class OnPaintDeleteClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Paint)
			{
				((PaintGLSurfaceView) canvas).reiniciar();
				Toast.makeText(getApplication(), "Deleted", Toast.LENGTH_SHORT).show();
				
				actualizarPaintBotones();
			}
		}
    }

    private class OnPaintReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			destroyPaintActivity(v.getId());
		}
    }
	
	private void actualizarPaintBotones()
	{
		if(((PaintGLSurfaceView) canvas).bufferSiguienteVacio())
		{
			botonPaintNext.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonPaintNext.setVisibility(View.VISIBLE);
		}
		
		if(((PaintGLSurfaceView) canvas).bufferAnteriorVacio())
		{
			botonPaintPrev.setVisibility(View.INVISIBLE);
			botonPaintDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonPaintPrev.setVisibility(View.VISIBLE);
			botonPaintDelete.setVisibility(View.VISIBLE);
		}
	}
	
	/* DEFORM ACTIVITY */
	
	private void createDeformActivity()
	{
		// Estado
		estado = TEstado.Deform;
		
		// Seleccionar Layout
		setContentView(R.layout.deform_layout);
		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) findViewById(R.id.deformGLSurfaceView1);
		((DeformGLSurfaceView) canvas).setEsqueleto(esqueleto);
		
		botonDeformAdd = (ImageButton) findViewById(R.id.imageButton11);
		botonDeformRemove = (ImageButton) findViewById(R.id.imageButton12);
		botonDeformMover = (ImageButton) findViewById(R.id.imageButton13);
		botonDeformDelete = (ImageButton) findViewById(R.id.imageButton14);
		
		//botonDeformRemove.setVisibility(View.INVISIBLE);
		//botonDeformMover.setVisibility(View.INVISIBLE);
		//botonDeformDelete.setVisibility(View.INVISIBLE);
		
		botonDeformAdd.setOnClickListener(new OnDeformAddClickListener());
		botonDeformRemove.setOnClickListener(new OnDeformRemoveClickListener());
		botonDeformMover.setOnClickListener(new OnDeformMoveClickListener());
		botonDeformDelete.setOnClickListener(new OnDeformDeleteClickListener());
		
		canvas.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				((DeformGLSurfaceView) canvas).onTouch(event);
				actualizarDeformBotones();
				return true;
			}
		});
	}
	
	/*private void destroyDeformActiviy()
	{
	
	}*/
	
	private void actualizarDeformBotones()
	{
		if(((DeformGLSurfaceView) canvas).handlesVacio())
		{
			botonDeformRemove.setVisibility(View.INVISIBLE);
			botonDeformMover.setVisibility(View.INVISIBLE);
			botonDeformDelete.setVisibility(View.INVISIBLE);
		}
		else
		{
			botonDeformRemove.setVisibility(View.VISIBLE);
			botonDeformMover.setVisibility(View.VISIBLE);
			botonDeformDelete.setVisibility(View.VISIBLE);
		}
	}
	
	private class OnDeformAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Deform)
			{
				((DeformGLSurfaceView) canvas).seleccionarAnyadir();
			}
		}	
	}
	
	private class OnDeformRemoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Deform)
			{
				((DeformGLSurfaceView) canvas).seleccionarEliminar();
			}
		}	
	}
	
	private class OnDeformMoveClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Deform)
			{
				((DeformGLSurfaceView) canvas).seleccionarMover();
			}
		}	
	}
	
	private class OnDeformDeleteClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Deform)
			{
				((DeformGLSurfaceView) canvas).reiniciar();
			
				actualizarDeformBotones();
			}
		}	
	}
}
