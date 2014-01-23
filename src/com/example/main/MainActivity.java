package com.example.main;

import java.util.Locale;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.animation.AttackFragment;
import com.example.animation.DownFragment;
import com.example.animation.JumpFragment;
import com.example.animation.RunFragment;
import com.example.deform.DeformGLSurfaceView;
import com.example.design.DesignGLSurfaceView;
import com.example.dialog.ColorPickerDialog;
import com.example.dialog.SizePicker;
import com.example.paint.PaintGLSurfaceView;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{	
	private TEstado estado;
	private Esqueleto esqueleto;
	private GLSurfaceView canvas;
	private Context mContext;

	/* LOADING ACTIVITY */
	private ImageButton botonMainAdd, botonMainPlay, botonMainView;

	/* DESIGN ACTIVITY */
	private ImageButton botonDesignReady;
	
	/* PAINT ACTIVITY */
	private ColorPickerDialog colorPicker;
	private SizePicker sizePicker;
	private ImageButton botonPaintPincel, botonPaintCubo, botonPaintMano, botonPaintNext, botonPaintPrev, botonPaintDelete, botonPaintReady, botonPaintColor, botonPaintSize, botonPaintEye;
	
	/* DEFORM ACTIVITY */
	private ImageButton botonDeformAdd, botonDeformRemove, botonDeformMover, botonDeformDelete;
	
	/* ANIM ACTIVITY */
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;

		createLoadingActivity();
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

	private void createLoadingActivity()
	{
		// Estado
		estado = TEstado.Loading;
		
		// Seleccionar Layout
		setContentView(R.layout.main_layout);
		
		// Recolector de Basura
		//TODO
		System.gc();
		
		// Instanciar Elementos de la GUI
		botonMainAdd = (ImageButton) findViewById(R.id.imageButtonMain1);
		botonMainPlay = (ImageButton) findViewById(R.id.imageButtonMain2);
		botonMainView = (ImageButton) findViewById(R.id.imageButtonMain3);
		
		botonMainAdd.setOnClickListener(new OnMainAddClickListener());
		botonMainView.setOnClickListener(new OnMainViewClickListener());
		botonMainPlay.setOnClickListener(new OnMainPlayClickListener());		
	}
	
	private void destroyLoadingActivity(int buttonId)
	{
		if(buttonId == botonMainAdd.getId())
		{
			createDesignActivity();
		}
		else if(buttonId == botonMainView.getId())
		{
			Toast.makeText(getApplication(), "View Characters", Toast.LENGTH_SHORT).show();
		}
		else if(buttonId == botonMainPlay.getId())
		{
			Toast.makeText(getApplication(), "Play Game", Toast.LENGTH_SHORT).show();
		}
	}
	
	private class OnMainAddClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}
	
	private class OnMainViewClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}
	
	private class OnMainPlayClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			destroyLoadingActivity(v.getId());
		}
	}
	
	/* DESIGN ACTIVITY */
	
	private void createDesignActivity()
	{	
		// Estado
		estado = TEstado.Design;
		
		// Seleccionar Layout
		setContentView(R.layout.design_layout);
		
		// Recolector de Basura
		//TODO
		System.gc();
		
		// Instanciar Elementos de la GUI
		canvas = (DesignGLSurfaceView) findViewById(R.id.designGLSurfaceView1);
		botonDesignReady = (ImageButton) findViewById(R.id.imageButtonDesign1);
		
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
		
		// Recolector de Basura
		//TODO
		System.gc();

		// Instanciar Elementos de la GUI
		canvas = (PaintGLSurfaceView) findViewById(R.id.PaintGLSurfaceView1);
		((PaintGLSurfaceView) canvas).setEsqueleto(esqueleto);
		
		botonPaintPincel = (ImageButton) findViewById(R.id.imageButtonPaint1);
		botonPaintCubo = (ImageButton) findViewById(R.id.imageButtonPaint2);
		botonPaintColor = (ImageButton) findViewById(R.id.imageButtonPaint3);
		botonPaintSize = (ImageButton) findViewById(R.id.imageButtonPaint4);
		botonPaintEye = (ImageButton) findViewById(R.id.imageButtonPaint5);
		botonPaintMano = (ImageButton) findViewById(R.id.imageButtonPaint6);
		botonPaintPrev = (ImageButton) findViewById(R.id.imageButtonPaint7);
		botonPaintNext = (ImageButton) findViewById(R.id.imageButtonPaint8);
		botonPaintDelete = (ImageButton) findViewById(R.id.imageButtonPaint9);
		botonPaintReady = (ImageButton) findViewById(R.id.imageButtonPaint10);
		
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
				createAnimActivity();
			}
			else if(buttonId == botonPaintReady.getId())
			{
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
				((PaintGLSurfaceView) canvas).seleccionarColor(0xff000000);
				colorPicker = new ColorPickerDialog(mContext, 0xff000000, (PaintGLSurfaceView)canvas);
				colorPicker.show();
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
				if (sizePicker == null) sizePicker= new SizePicker(mContext, SizePicker.VERTICAL, (PaintGLSurfaceView)canvas);    	
				sizePicker.show(v);
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
		
		// Recolector de Basura
		//TODO
		System.gc();
		
		// Instanciar Elementos de la GUI
		canvas = (DeformGLSurfaceView) findViewById(R.id.deformGLSurfaceView1);
		((DeformGLSurfaceView) canvas).setEsqueleto(esqueleto);
		
		botonDeformAdd = (ImageButton) findViewById(R.id.imageButtonDeform1);
		botonDeformRemove = (ImageButton) findViewById(R.id.imageButtonDeform2);
		botonDeformMover = (ImageButton) findViewById(R.id.imageButtonDeform3);
		botonDeformDelete = (ImageButton) findViewById(R.id.imageButtonDeform4);
		
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
	
	/* ANIM ACTIVITY */
	
	private void createAnimActivity()
	{
		// Estado
		estado = TEstado.Anim;
		
		// Seleccionar Layout
		setContentView(R.layout.move_layout);
		
		// Recolector de Basura
		//TODO
		System.gc();
				
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
		        case 0:
		        	RunFragment run = new RunFragment();
		        	run.setEsqueleto(esqueleto);
		            return run;
		        case 1:
		        	JumpFragment jump = new JumpFragment();
		        	jump.setEsqueleto(esqueleto);
		            return jump;
		        case 2:
		        	DownFragment down = new DownFragment();
		        	down.setEsqueleto(esqueleto);
		            return down;
		        case 3:
		        	AttackFragment attack = new AttackFragment();
		        	attack.setEsqueleto(esqueleto);
		            return attack;
	        }
	 
	        return null;
		}

		@Override
		public int getCount()
		{
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			Locale l = Locale.getDefault();
			switch (position)
			{
				case 0:
					return getString(R.string.title_section1).toUpperCase(l);
				case 1:
					return getString(R.string.title_section2).toUpperCase(l);
				case 2:
					return getString(R.string.title_section3).toUpperCase(l);
				case 3:
					return getString(R.string.title_section4).toUpperCase(l);
			}
			
			return null;
		}
	}
}
