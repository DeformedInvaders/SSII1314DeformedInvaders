package com.example.main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.dialog.ColorPickerDialog;
import com.android.dialog.SizePicker;
import com.android.storage.InternalStorageManager;
import com.create.deform.DeformFragment;
import com.create.design.DesignGLSurfaceView;
import com.create.paint.PaintGLSurfaceView;
import com.example.data.Esqueleto;
import com.view.select.SelectFragment;

public class MainActivity extends FragmentActivity
{	
	/* DATA */
	private TEstado estado;
	private List<Esqueleto> esqueletoLista;
	private Esqueleto esqueletoActual;
	
	private GLSurfaceView canvas;
	private Context mContext;
	private InternalStorageManager manager;

	/* LOADING ACTIVITY */
	private ImageButton botonMainAdd, botonMainPlay, botonMainView;

	/* DESIGN ACTIVITY */
	private ImageButton botonDesignReady;
	
	/* PAINT ACTIVITY */
	private ColorPickerDialog colorPicker;
	private SizePicker sizePicker;
	private ImageButton botonPaintPincel, botonPaintCubo, botonPaintMano, botonPaintNext, botonPaintPrev, botonPaintDelete, botonPaintReady, botonPaintColor, botonPaintSize, botonPaintEye;
	
	/* ANIM ACTIVITY */
	private ImageButton botonAnimReady;
	private SectionsAnimPagerAdapter sectionsAnimPagerAdapter;
	private ViewPager viewAnimPager;
	private DeformFragment fragmentoAnimAttack, fragmentoAnimRun, fragmentoAnimJump, fragmentoAnimDown;
	
	/* VIEW ACTIVITY */
	private ImageButton botonViewReady;
	private SectionsViewPagerAdapter sectionsViewPagerAdapter;
	private ViewPager viewViewPager;
	private List<SelectFragment> listaViewFragmentos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		mContext = this;
		
		esqueletoLista = new ArrayList<Esqueleto>();
		manager = new InternalStorageManager();
		
		// Cargar Esqueletos Guardados
		try
		{
			FileInputStream file = openFileInput(manager.getFileName());
			manager.cargarEsqueleto(file, esqueletoLista);
		}
		catch (FileNotFoundException e)
		{
			Log.d("TEST", "FILE NOT FOUND EXCEPTION");
			e.printStackTrace();
		}

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
		ActionBar actionBar = getActionBar();
		actionBar.removeAllTabs();
		
		botonMainAdd = (ImageButton) findViewById(R.id.imageButtonMain1);
		botonMainPlay = (ImageButton) findViewById(R.id.imageButtonMain2);
		botonMainView = (ImageButton) findViewById(R.id.imageButtonMain3);
		
		botonMainAdd.setOnClickListener(new OnMainAddClickListener());
		botonMainView.setOnClickListener(new OnMainViewClickListener());
		botonMainPlay.setOnClickListener(new OnMainPlayClickListener());	
		
		// Canvas con esqueleto seleccionado
		//TODO
	}
	
	private void destroyLoadingActivity(int buttonId)
	{
		if(buttonId == botonMainAdd.getId())
		{
			createDesignActivity();
		}
		else if(buttonId == botonMainView.getId())
		{
			createViewActivity();
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
			esqueletoActual = e;

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
		((PaintGLSurfaceView) canvas).setEsqueleto(esqueletoActual);
		
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
	
	private void destroyPaintActivity()
	{
		((PaintGLSurfaceView) canvas).capturaPantalla();
		Esqueleto e = ((PaintGLSurfaceView) canvas).getEsqueleto();
		if(e != null)
		{
			esqueletoActual = e;
			createAnimActivity();
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
				int color = ((PaintGLSurfaceView) canvas).getColorPaleta();
				((PaintGLSurfaceView) canvas).seleccionarColor(color);
				colorPicker = new ColorPickerDialog(mContext, color, (PaintGLSurfaceView)canvas);
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
			Toast.makeText(getApplication(), "Textures", Toast.LENGTH_SHORT).show();
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
			destroyPaintActivity();
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
	
	/* ANIM ACTIVITY */
	
	private void createAnimActivity()
	{
		// Estado
		estado = TEstado.Animation;
		
		// Seleccionar Layout
		setContentView(R.layout.move_layout);
		
		// Recolector de Basura
		//TODO
		System.gc();
		
		// Instanciar Elementos de la GUI
		botonAnimReady = (ImageButton) findViewById(R.id.imageButtonAnim1);		
		botonAnimReady.setOnClickListener(new OnAnimReadyClickListener());	
				
		final ActionBar actionBar = getActionBar();
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		fragmentoAnimAttack = new DeformFragment();
    	fragmentoAnimRun = new DeformFragment();
    	fragmentoAnimJump = new DeformFragment();
    	fragmentoAnimDown = new DeformFragment();
  
		fragmentoAnimAttack.setEsqueleto(esqueletoActual);
    	fragmentoAnimRun.setEsqueleto(esqueletoActual);
    	fragmentoAnimJump.setEsqueleto(esqueletoActual);
    	fragmentoAnimDown.setEsqueleto(esqueletoActual);

		sectionsAnimPagerAdapter = new SectionsAnimPagerAdapter(getSupportFragmentManager());

		viewAnimPager = (ViewPager) findViewById(R.id.pagerAnim1);
		viewAnimPager.removeAllViews();
		viewAnimPager.setAdapter(sectionsAnimPagerAdapter);

		viewAnimPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < sectionsAnimPagerAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(sectionsAnimPagerAdapter.getPageTitle(i)).setTabListener(new TabAnimListener()));
		}
	}
	
    private class OnAnimReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.Animation)
			{
				esqueletoLista.add(esqueletoActual);
				
				// Cargar Esqueletos Guardados
				try
				{
					FileOutputStream file = openFileOutput(manager.getFileName(), Context.MODE_PRIVATE);
					manager.guardarEsqueleto(file, esqueletoLista);
					
					createLoadingActivity();
				}
				catch (FileNotFoundException e)
				{
					Log.d("TEST", "FILE NOT FOUND EXCEPTION");
					e.printStackTrace();
				}
			}
		}
    }
	
	public class TabAnimListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			viewAnimPager.setCurrentItem(tab.getPosition());
		}
	
		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
	
	public class SectionsAnimPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsAnimPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			switch (position)
			{
		        case 0:
		            return fragmentoAnimRun;
		        case 1:
		        	return fragmentoAnimJump;
		        case 2:
		        	return fragmentoAnimDown;
		        case 3:
		        	return fragmentoAnimAttack;
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
					return getString(R.string.title_section_run).toUpperCase(l);
				case 1:
					return getString(R.string.title_section_jump).toUpperCase(l);
				case 2:
					return getString(R.string.title_section_down).toUpperCase(l);
				case 3:
					return getString(R.string.title_section_attack).toUpperCase(l);
			}
			
			return null;
		}
	}

	/* VIEW ACTIVITY */
	
	private void createViewActivity()
	{
		// Estado
		estado = TEstado.View;
		
		// Seleccionar Layout
		setContentView(R.layout.view_layout);
		
		// Recolector de Basura
		//TODO
		System.gc();
		
		// Instanciar Elementos de la GUI
		botonViewReady = (ImageButton) findViewById(R.id.imageButtonView1);		
		botonViewReady.setOnClickListener(new OnViewReadyClickListener());			
		
		final ActionBar actionBar = getActionBar();
		actionBar.removeAllTabs();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		listaViewFragmentos = new ArrayList<SelectFragment>();
		
		Iterator<Esqueleto> it = esqueletoLista.iterator();
		while(it.hasNext())
		{
			SelectFragment sf = new SelectFragment();
			sf.setEsqueleto(it.next());
			
			listaViewFragmentos.add(sf);
		}

		sectionsViewPagerAdapter = new SectionsViewPagerAdapter(getSupportFragmentManager());

		viewViewPager = (ViewPager) findViewById(R.id.pagerView1);
		viewViewPager.removeAllViews();
		viewViewPager.setAdapter(sectionsViewPagerAdapter);

		viewViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < sectionsViewPagerAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(sectionsViewPagerAdapter.getPageTitle(i)).setTabListener(new TabViewListener()));
		}
	}
	
    private class OnViewReadyClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v)
		{
			if(estado == TEstado.View)
			{
				createLoadingActivity();
			}
		}
    }
	
	public class TabViewListener implements ActionBar.TabListener
	{
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
		{
			viewViewPager.setCurrentItem(tab.getPosition());
		}
	
		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	
		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) { }
	}
	
	public class SectionsViewPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsViewPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if(position >= 0 && position < listaViewFragmentos.size())
			{
				return listaViewFragmentos.get(position);
			}
			
			return null;
		}

		@Override
		public int getCount()
		{
			return listaViewFragmentos.size();
		}

		@Override
		public CharSequence getPageTitle(int position)
		{			
			if(position >= 0 && position < listaViewFragmentos.size())
			{
				return "PERSONAJE "+position;
			}
			
			return null;
		}
	}

}
