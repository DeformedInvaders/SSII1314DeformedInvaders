package com.project.main;

import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.alert.ConfirmationAlert;
import com.android.alert.TextInputAlert;
import com.android.social.SocialConnector;
import com.android.storage.ExternalStorageManager;
import com.android.storage.InternalStorageManager;
import com.create.deform.AnimationFragment;
import com.create.design.DesignFragment;
import com.create.paint.PaintFragment;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Personaje;
import com.project.data.Textura;
import com.project.loading.LoadingFragment;
import com.test.main.TestFragment;
import com.view.select.SelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, SelectionFragment.SelectionFragmentListener
{	
	/* Estructura de Datos */
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	private int personajeSeleccionado;

	/* Almacenamiento */
	private InternalStorageManager internalManager;
	private ExternalStorageManager externalManager;
	
	/* Conectores Sociales */
	private SocialConnector connector;
	
	/* Elementos de la Interafaz */
	private ActionBar actionBar;
	private MenuItem botonTwitter, botonFacebook;
	
	/* Estado */
	private TEstado estado;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		internalManager = new InternalStorageManager(this);
		externalManager = new ExternalStorageManager();
		connector = new SocialConnector(this);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		changeFragment(LoadingFragment.newInstance(internalManager));
	}
	
	@Override
	public void onBackPressed()
	{
		if(estado != TEstado.Main)
		{	
			limpiarActionBar();
			
			super.onBackPressed();
			actualizarEstado();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    
	    botonTwitter = menu.getItem(0);
	    botonFacebook = menu.getItem(1);
	    
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
	        case R.id.menuIcon1:
	            onMenuTwitterButtonClicked();
	            return true;
	        case R.id.menuIcon2:
	        	onMenuFacebookButtonClicked();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/* Métodos de Modificación del FrameLayout */
	
	private void changeFragment(Fragment fragmento)
	{
		boolean clearBackStack = false;
		boolean addToBackStack = true;
		
		if(estado == TEstado.Selection) addToBackStack = false;
		
		actualizarEstado(fragmento);
		limpiarActionBar();
		
		if(estado == TEstado.Main) clearBackStack = true;
		
		FragmentManager manager = getSupportFragmentManager();
		
		// Limpiar la BackStack
		if(clearBackStack) clearBackStack(manager);
		
		// Reemplazar el Fragmento
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento, estado.toString());
		
		// Añadir a la BackStack
		if(addToBackStack) transaction.addToBackStack(estado.toString());
		
		transaction.commit();
	}
	
	private void clearBackStack(FragmentManager manager)
	{
		manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	/* LOADING FRAGMENT */
	
	@Override
	public void onLoadingListCharacters(List<Personaje> lista, int seleccionado)
	{
		listaPersonajes = lista;
		personajeSeleccionado = seleccionado;
		
		changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
	}
	
	/* MAIN FRAGMENT */
	
	@Override
	public void onMainCreateButtonClicked()
	{
		personajeActual = new Personaje();
		
		changeFragment(DesignFragment.newInstance());
	}
	
	@Override
    public void onMainSelectButtonClicked()
    {
    	changeFragment(SelectionFragment.newInstance(listaPersonajes, externalManager, connector));
    }
    
	@Override
    public void onMainPlayButtonClicked()
    {
		Toast.makeText(getApplication(), R.string.error_play, Toast.LENGTH_SHORT).show();
    }
	
	@Override
    public void onMainTestButtonClicked()
    {
    	changeFragment(TestFragment.newInstance());
    }
	
	/* DESIGN FRAGMENT */
    
	@Override
    public void onDesignReadyButtonClicked(Esqueleto esqueleto)
    {
    	if(esqueleto == null)
    	{
    		Toast.makeText(getApplication(), R.string.error_design, Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setEsqueleto(esqueleto);    		
    		changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()));
    	}
    }
    
	@Override
    public void onDesignTestButtonClicked(boolean test)
    {
    	if(!test)
    	{
    		Toast.makeText(getApplication(), R.string.error_triangle, Toast.LENGTH_SHORT).show();
    	}
    }
	
	/* PAINT FRAGMENT */
    
	@Override
    public void onPaintReadyButtonClicked(Textura textura)
    {
    	if(textura == null)
    	{
    		Toast.makeText(getApplication(), R.string.error_paint, Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setTextura(textura);
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura(), externalManager));
    	}
    }
    
	/* ANIMATION FRAGMENT */
    
	@Override
    public void onAnimationReadyButtonClicked(Movimientos movimientos)
    {
		if(movimientos == null)
		{
			Toast.makeText(getApplication(), R.string.error_animation, Toast.LENGTH_SHORT).show();
		}
		else
		{
			if(movimientos.isReady())
			{
				personajeActual.setMovimientos(movimientos);
			
				TextInputAlert alert = new TextInputAlert(this, getString(R.string.text_save_character_title), getString(R.string.text_save_character_description), getString(R.string.text_button_yes), getString(R.string.text_button_no)) {
	
					@Override
					public void onPossitiveButtonClick()
					{
						String value = getText().toUpperCase(Locale.getDefault());
						personajeActual.setNombre(value);
						
						if(internalManager.guardarPersonaje(personajeActual))
						{
							listaPersonajes.add(personajeActual);
							personajeActual = null;
							
							Toast.makeText(getApplication(), R.string.text_save_character_confirmation, Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(getApplication(), R.string.error_save_character, Toast.LENGTH_SHORT).show();
						}
							
						changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
					}
	
					@Override
					public void onNegativeButtonClick()
					{
						changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
					}
					
				};
	
				alert.show();
			}
			else
			{
				Toast.makeText(getApplication(), R.string.error_deform, Toast.LENGTH_SHORT).show();
			}
		}
	}
    
	/* SELECTION FRAGMENT */
    
	@Override
    public void onSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		internalManager.guardarSeleccionado(personajeSeleccionado);
		Toast.makeText(getApplication(), R.string.text_select_character_confirmation, Toast.LENGTH_SHORT).show();

		changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
    }
    
	@Override
    public void onSelectionDeleteButtonClicked(final int indice)
    {   	
    	ConfirmationAlert alert = new ConfirmationAlert(this, getString(R.string.text_delete_character_title), getString(R.string.text_delete_character_description), getString(R.string.text_button_ok), getString(R.string.text_button_no)) {

			@Override
			public void onPossitiveButtonClick()
			{
				if(internalManager.eliminarPersonaje(listaPersonajes.get(indice)))
		    	{
		    		listaPersonajes.remove(indice);
		    		
		    		if(personajeSeleccionado == indice)
		    		{
		    			personajeSeleccionado = -1;
		    			internalManager.guardarSeleccionado(personajeSeleccionado);
		    		}
		    		
		    		Toast.makeText(getApplication(), R.string.text_delete_character_confirmation, Toast.LENGTH_SHORT).show();
		    	}
		    	else
		    	{
		    		Toast.makeText(getApplication(), R.string.error_delete_character, Toast.LENGTH_SHORT).show();
		    	}
		    	
				changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
			}

			@Override
			public void onNegativeButtonClick() { }
    	};

		alert.show();
    }
	
	/* Métodos de Conexión Social */
	
	/* Métodos de Modificación de la ActionBar */
    
	public void onMenuTwitterButtonClicked()
	{
		if(connector.isTwitterConnected())
		{
			connector.desconectarTwitter();
		}
		else
		{
			connector.conectarTwitter();
		}
	}
	
	public void onMenuFacebookButtonClicked()
	{
		if(connector.isFacebookConnected())
		{
			connector.desconectarFacebook();
		}
		else
		{
			connector.conectarFacebook();
		}
	}
	
	public void actualizarActionBar()
	{
		if(connector.isTwitterConnected())
		{
			botonTwitter.setIcon(R.drawable.menu_twitter_connected);
		}
		else
		{
			botonTwitter.setIcon(R.drawable.menu_twitter);
		}
		
		if(connector.isFacebookConnected())
		{
			botonFacebook.setIcon(R.drawable.menu_facebook_connected);
		}
		else
		{
			botonFacebook.setIcon(R.drawable.menu_facebook);
		}
	}
	
    private void limpiarActionBar()
    {
    	actionBar.removeAllTabs();
    }
    
    /* Métodos de Modificación del Estado */
    
    private void actualizarEstado(Fragment fragmento)
    {
    	if(fragmento != null)
    	{
    		if(fragmento instanceof LoadingFragment)
    		{
    			estado = TEstado.Loading;
    			setTitle(R.string.title_app);
    		}
    		if(fragmento instanceof MainFragment)
    		{
    			estado = TEstado.Main;
    			setTitle(R.string.title_app);
    		}
    		else if(fragmento instanceof DesignFragment)
    		{
    			estado = TEstado.Design;
    			setTitle(R.string.title_design_phase);
    		}
    		else if(fragmento instanceof PaintFragment)
    		{
    			estado = TEstado.Paint;
    			setTitle(R.string.title_paint_phase);
    		}
    		else if(fragmento instanceof AnimationFragment)
    		{
    			estado = TEstado.Animation;
    			setTitle(R.string.title_animation_phase);
    		}
    		else if(fragmento instanceof SelectionFragment)
    		{
    			estado = TEstado.Selection;
    			setTitle(R.string.title_selection_phase);
    		}
    		else if(fragmento instanceof TestFragment)
    		{
    			estado = TEstado.Test;
    			setTitle(R.string.title_test_phase);
    		}
    	}
    }
    
    private void actualizarEstado()
    {
    	String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		actualizarEstado(getSupportFragmentManager().findFragmentByTag(tag));
    }
}
