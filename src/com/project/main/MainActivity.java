package com.project.main;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.multitouch.MultitouchFragment;
import com.android.storage.InternalStorageManager;
import com.create.deform.AnimationFragment;
import com.create.design.DesignFragment;
import com.create.paint.PaintFragment;
import com.project.data.Esqueleto;
import com.project.data.Movimientos;
import com.project.data.Personaje;
import com.project.data.Textura;
import com.view.select.SelectionFragment;

public class MainActivity extends FragmentActivity implements LoadingFragment.LoadingFragmentListener, DesignFragment.DesignFragmentListener, PaintFragment.PaintFragmentListener, AnimationFragment.AnimationFragmentListener, SelectionFragment.SelectionFragmentListener
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
			personajeSeleccionado = manager.cargarSeleccionado();
			listaPersonajes = manager.cargarListaPersonajes();

    		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
        }
	}
	
	@Override
	public void onBackPressed()
	{
		if(estado != TEstado.Loading)
		{	
			limpiarActionBar();
			
			super.onBackPressed();
			
			actualizarEstado();
		}
	}
	
	private void changeFragment(Fragment fragmento)
	{
		actualizarEstado(fragmento);
		limpiarActionBar();
		
		FragmentManager manager = getSupportFragmentManager();
		
		if(estado == TEstado.Loading)
		{
			clearBackStack(manager);
		}
		
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.frameLayoutMain1, fragmento, estado.toString());
		transaction.addToBackStack(estado.toString());
		transaction.commit();
	}
	
	private void clearBackStack(FragmentManager manager)
	{
		manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
	
	/* LOADING ACTIVITY */
	
	public void onLoadingCreateButtonClicked()
	{
		personajeActual = new Personaje();
		
		changeFragment(DesignFragment.newInstance());
	}
	
    public void onLoadingSelectButtonClicked()
    {
    	changeFragment(SelectionFragment.newInstance(listaPersonajes));
    }
    
    public void onLoadingPlayButtonClicked()
    {
    	//Toast.makeText(getApplication(), "Play Game", Toast.LENGTH_SHORT).show();
    	changeFragment(MultitouchFragment.newInstance());
    }
	
	/* DESIGN ACTIVITY */
    
    public void onDesignReadyButtonClicked(Esqueleto esqueleto)
    {
    	if(esqueleto == null)
    	{
    		Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setEsqueleto(esqueleto);    		
    		changeFragment(PaintFragment.newInstance(personajeActual.getEsqueleto()));
    	}
    }
    
    public void onDesignTestButtonClicked(boolean test)
    {
    	if(!test)
    	{
    		Toast.makeText(getApplication(), "The Polygon is Complex", Toast.LENGTH_SHORT).show();
    	}
    }
	
	/* PAINT ACTIVITY */
    
    public void onPaintReadyButtonClicked(Textura textura)
    {
    	if(textura == null)
    	{
    		Toast.makeText(getApplication(), "The Texture is Corrupted", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		personajeActual.setTextura(textura);
    		changeFragment(AnimationFragment.newInstance(personajeActual.getEsqueleto(), personajeActual.getTextura()));
    	}
    }
    
	/* ANIM ACTIVITY */
    
    public void onAnimationReadyButtonClicked(Movimientos movimientos)
    {
		if(movimientos != null)
		{
			personajeActual.setMovimientos(movimientos);
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
	
			alert.setTitle("Personaje Finalizado");
			alert.setMessage("Introduzca un nombre si desea guardar el personaje");
	
			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);
	
			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					String value = input.getText().toString();
					personajeActual.setNombre(value);
					
					if(manager.guardarPersonaje(personajeActual))
					{
						listaPersonajes.add(personajeActual);
						personajeActual = null;
					}
						
					changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
				}
			});
	
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
				}
			});
	
			alert.show();
		}
	}
    
	/* SELECTION ACTIVITY */
    
    public void onSelectionSelectClicked(int indice)
    {
		personajeSeleccionado = indice;
		
		manager.guardarSeleccionado(personajeSeleccionado);
		
		changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
    }
    
    public void onSelectionDeleteButtonClicked(int indice)
    {
    	if(manager.eliminarPersonaje(listaPersonajes.get(indice)))
    	{
    		listaPersonajes.remove(indice);
    		
    		if(personajeSeleccionado == indice)
    		{
    			personajeSeleccionado = -1;	
    		}
    		
    		manager.guardarSeleccionado(personajeSeleccionado);
    	}
		
		if(listaPersonajes.size() > 0)
		{
			changeFragment(SelectionFragment.newInstance(listaPersonajes));
		}
		else 
		{
			changeFragment(LoadingFragment.newInstance(listaPersonajes, personajeSeleccionado));
		}
    }
    
    private void limpiarActionBar()
    {
    	actionBar.removeAllTabs();
    }
    
    private void actualizarEstado(Fragment fragmento)
    {
    	if(fragmento != null)
    	{
    		if(fragmento instanceof LoadingFragment) estado = TEstado.Loading;
    		else if(fragmento instanceof DesignFragment) estado = TEstado.Design;
    		else if(fragmento instanceof PaintFragment) estado = TEstado.Paint;
    		else if(fragmento instanceof AnimationFragment) estado = TEstado.Animation;
    		else if(fragmento instanceof SelectionFragment) estado = TEstado.Selection;
    		else if(fragmento instanceof MultitouchFragment) estado = TEstado.Game;
    	}
    	
    	Toast.makeText(getApplication(), "Fase: "+estado.toString(), Toast.LENGTH_SHORT).show();
    }
    
    private void actualizarEstado()
    {
    	String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		actualizarEstado(getSupportFragmentManager().findFragmentByTag(tag));
    }
}
