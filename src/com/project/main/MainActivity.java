package com.project.main;

import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.alert.AlertDialogConfirmation;
import com.android.alert.AlertDialogTextInput;
import com.android.multitouch.MultitouchFragment;
import com.android.storage.InternalStorageManager;
import com.create.deform.AnimationFragment;
import com.create.design.DesignFragment;
import com.create.paint.PaintFragment;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Personaje;
import com.project.data.Textura;
import com.project.loading.LoadingFragment;
import com.view.select.SelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, MainFragment.MainFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, SelectionFragment.SelectionFragmentListener
{	
	private List<Personaje> listaPersonajes;
	private Personaje personajeActual;
	private int personajeSeleccionado;

	private ActionBar actionBar;
	private InternalStorageManager manager;
	private FrameLayout layout;
	private TEstado estado;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);
		
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		manager = new InternalStorageManager(this);
		layout = (FrameLayout) findViewById(R.id.frameLayoutMain1);
        
		if(layout != null)
        {
    		changeFragment(LoadingFragment.newInstance(manager));
        }
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
    	changeFragment(SelectionFragment.newInstance(listaPersonajes));
    }
    
	@Override
    public void onMainPlayButtonClicked()
    {
    	changeFragment(MultitouchFragment.newInstance());
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
    		Toast.makeText(getApplication(), R.string.error_test, Toast.LENGTH_SHORT).show();
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
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura()));
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
			personajeActual.setMovimientos(movimientos);
		
			AlertDialogTextInput alert = new AlertDialogTextInput(this, getString(R.string.text_save_character_title), getString(R.string.text_save_character_description), getString(R.string.text_confirmation_yes), getString(R.string.text_confirmation_no)) {

				@Override
				public void onPossitiveButtonClick()
				{
					String value = getText().toUpperCase(Locale.getDefault());
					personajeActual.setNombre(value);
					
					if(manager.guardarPersonaje(personajeActual))
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
	}
    
	/* SELECTION FRAGMENT */
    
	@Override
    public void onSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		manager.guardarSeleccionado(personajeSeleccionado);
		Toast.makeText(getApplication(), R.string.text_select_character_confirmation, Toast.LENGTH_SHORT).show();

		changeFragment(MainFragment.newInstance(listaPersonajes, personajeSeleccionado));
    }
    
	@Override
    public void onSelectionDeleteButtonClicked(final int indice)
    {   
    	
    	AlertDialogConfirmation alert = new AlertDialogConfirmation(this, getString(R.string.text_delete_character_title), getString(R.string.text_delete_character_description), getString(R.string.text_confirmation_yes), getString(R.string.text_confirmation_no)) {

			@Override
			public void onPossitiveButtonClick()
			{
				if(manager.eliminarPersonaje(listaPersonajes.get(indice)))
		    	{
		    		listaPersonajes.remove(indice);
		    		
		    		if(personajeSeleccionado == indice)
		    		{
		    			personajeSeleccionado = -1;
		    			manager.guardarSeleccionado(personajeSeleccionado);
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
    
    private void limpiarActionBar()
    {
    	actionBar.removeAllTabs();
    }
    
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
    		else if(fragmento instanceof MultitouchFragment)
    		{
    			estado = TEstado.Game;
    			setTitle(R.string.title_game_phase);
    		}
    	}
    }
    
    private void actualizarEstado()
    {
    	String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		actualizarEstado(getSupportFragmentManager().findFragmentByTag(tag));
    }
}
